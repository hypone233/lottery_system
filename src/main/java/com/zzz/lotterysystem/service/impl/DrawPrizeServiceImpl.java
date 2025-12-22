package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.common.utils.RedisUtil;
import com.zzz.lotterysystem.controller.param.DrawPrizeParam;
import com.zzz.lotterysystem.dao.dataobject.*;
import com.zzz.lotterysystem.dao.mapper.*;
import com.zzz.lotterysystem.service.DrawPrizeService;
import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.zzz.lotterysystem.common.config.DirectRabbitConfig.EXCHANGE_NAME;
import static com.zzz.lotterysystem.common.config.DirectRabbitConfig.ROUTING;

@Service
public class DrawPrizeServiceImpl implements DrawPrizeService {

    private static final Logger logger = LoggerFactory.getLogger(DrawPrizeServiceImpl.class);

    private final Long WINNING_RECORDS_TIMEOUT = (long) (60*60*24*2);
    private final String WINNING_RECORDS_PREFIX = "WINNING_RECORDS_";

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PrizeMapper prizeMapper;
    @Autowired
    private WinningRecordMapper winningRecordMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void drawPrize(DrawPrizeParam param) {

        Map<String,String> map = new HashMap<>();
        map.put("messageId",String.valueOf(UUID.randomUUID()));
        map.put("messageData", JacksonUtil.writeValueAsString(param));
        //发消息: 交换机，绑定key,消息体
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,ROUTING,map);
        logger.info("mq消息发送成功: map={}",JacksonUtil.writeValueAsString(map));

    }

    @Override
    public void checkDrawPrizeParam(DrawPrizeParam param) {

        ActivityDO activityDO = activityMapper.selectById(param.getActivityId());

        ActivityPrizeDO activityPrizeDO = activityPrizeMapper.selectByAPId(
                param.getActivityId(),param.getPrizeId());

        //活动或奖品是否存在

        if(null == activityDO || null == activityPrizeDO){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_OR_PRIZE_IS_EMPTY);
        }

        //活动是否有效
        if(activityDO.getStatus()
                .equalsIgnoreCase(ActivityStatusEnum.COMPLETED.name())){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_COMPLETED);
        }
        //奖品是否有效
        if(activityPrizeDO.getStatus()
                .equalsIgnoreCase(ActivityPrizeStatusEnum.COMPLETED.name())){
            throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_COMPLETED);
        }
        //中奖人数是否和设置奖品数一致
        if(activityPrizeDO.getPrizeAmount() != param.getWinnerList().size()){
            throw new ServiceException(ServiceErrorCodeConstants.WINNER_PRIZE_AMOUNT_ERROR);
        }
    }

    @Override
    public List<WinningRecordDO> saveWinnerRecords(DrawPrizeParam param) {
        //查询
        ActivityDO activityDO = activityMapper.selectById(param.getActivityId());
        List<UserDO> userDOList = userMapper.batchSelectByIds(
                param.getWinnerList()
                        .stream()
                        .map(DrawPrizeParam.Winner::getUserId)
                        .collect(Collectors.toList())

        );
         PrizeDO prizeDO = prizeMapper.selectById(param.getPrizeId());
         ActivityPrizeDO activityPrizeDO =
                 activityPrizeMapper.selectByAPId(param.getActivityId(),param.getPrizeId());
        //构造中奖者记录
        List<WinningRecordDO> winningRecordDOList = userDOList.stream()
                .map( userDO -> {
                    WinningRecordDO winningRecordDO = new WinningRecordDO();
                    winningRecordDO.setActivityId(activityDO.getId());
                    winningRecordDO.setActivityName(activityDO.getActivityName());
                    winningRecordDO.setPrizeId(prizeDO.getId());
                    winningRecordDO.setPrizeName(prizeDO.getName());
                    winningRecordDO.setPrizeTier(activityPrizeDO.getPrizeTiers());
                    winningRecordDO.setWinnerId(userDO.getId());
                    winningRecordDO.setWinnerName(userDO.getUserName());
                    winningRecordDO.setWinnerEmail(userDO.getEmail());
                    winningRecordDO.setWinnerPhoneNumber(userDO.getPhoneNumber());
                    winningRecordDO.setWinningTime(param.getWinningTime());
                    return winningRecordDO;
                }).collect(Collectors.toList());
        winningRecordMapper.batchInsert(winningRecordDOList);
        //缓存记录
        //缓存奖品
        cacheWinningRecords(param.getActivityId() + "_" +param.getPrizeId(),
                winningRecordDOList,
                WINNING_RECORDS_TIMEOUT);
        //缓存活动
        if(activityDO.getStatus()
                .equalsIgnoreCase(ActivityStatusEnum.COMPLETED.name())){
            List<WinningRecordDO> allList = winningRecordMapper.selectByActivityId(param.getActivityId());

            cacheWinningRecords(String.valueOf(param.getActivityId()),
                    allList,
                    WINNING_RECORDS_TIMEOUT);
        }
        return winningRecordDOList;

    }

    /**
     * 缓存中奖记录
     *
     */
    private void cacheWinningRecords(String key,
                                     List<WinningRecordDO> winningRecordDOList,
                                     Long time) {
        String str = "";
        try {
            if(!StringUtils.hasText(key)
            || CollectionUtils.isEmpty(winningRecordDOList)){
                logger.warn("要缓存的内容为空! key:{},value:{}",
                        key,JacksonUtil.writeValueAsString(winningRecordDOList));
                return;
            }


            str = JacksonUtil.writeValueAsString(winningRecordDOList);
            redisUtil.set(WINNING_RECORDS_PREFIX + key,
                    str,
                    time);
        } catch (Exception e) {
            logger.error("缓存中奖记录异常! key:{},value:{}",WINNING_RECORDS_PREFIX+key,str);
        }


    }

    private List<WinningRecordDO> getWinnerRecords(String key){

        try {
            if(!StringUtils.hasText(key)){
                logger.warn("从缓存查询记录的key为空");
                return Arrays.asList();
            }
            String str = redisUtil.get(WINNING_RECORDS_PREFIX + key);
            if (!StringUtils.hasText(str)){
                return Arrays.asList();
            }

            List<WinningRecordDO> winningRecordDOList =
                    JacksonUtil.readListValue(str, WinningRecordDO.class);
            return winningRecordDOList;
        } catch (Exception e) {
            logger.error("从缓存查询中奖记录异常! key:{}",WINNING_RECORDS_PREFIX+key);
            return Arrays.asList();
        }

    }
}
