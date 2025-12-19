package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.DrawPrizeParam;
import com.zzz.lotterysystem.dao.dataobject.ActivityDO;
import com.zzz.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.zzz.lotterysystem.dao.mapper.ActivityMapper;
import com.zzz.lotterysystem.dao.mapper.ActivityPrizeMapper;
import com.zzz.lotterysystem.service.DrawPrizeService;
import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.zzz.lotterysystem.common.config.DirectRabbitConfig.EXCHANGE_NAME;
import static com.zzz.lotterysystem.common.config.DirectRabbitConfig.ROUTING;

@Service
public class DrawPrizeServiceImpl implements DrawPrizeService {

    private static final Logger logger = LoggerFactory.getLogger(DrawPrizeServiceImpl.class);

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;

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
}
