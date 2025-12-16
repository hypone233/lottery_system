package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.common.utils.RedisUtil;
import com.zzz.lotterysystem.controller.param.CreateActivityParam;
import com.zzz.lotterysystem.controller.param.CreatePrizeByActivityParam;
import com.zzz.lotterysystem.controller.param.CreateUserByActivityParam;
import com.zzz.lotterysystem.dao.dataobject.ActivityDO;
import com.zzz.lotterysystem.dao.dataobject.ActivityPrizeDO;
import com.zzz.lotterysystem.dao.dataobject.ActivityUserDO;
import com.zzz.lotterysystem.dao.dataobject.PrizeDO;
import com.zzz.lotterysystem.dao.mapper.*;
import com.zzz.lotterysystem.service.ActivityService;
import com.zzz.lotterysystem.service.dto.ActivityDetailDTO;
import com.zzz.lotterysystem.service.dto.CreateActivityDTO;
import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityPrizeTiersEnum;
import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityUserStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.model.SpELExpressionEvaluator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    /**
     * 活动缓存前缀
     */
    private final String ACTIVITY_PREFIX = "ACTIVITY_";
    /**
     * 活动缓存过期时间
     */
    private final Long ACTIVITY_TIMEOUT = (long) (60 * 60 * 24 * 3);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PrizeMapper prizeMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityUserMapper activityUserMapper;

    @Autowired
    private ActivityPrizeMapper activityPrizeMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class) //多表保证完整事务
    public CreateActivityDTO createActivity(CreateActivityParam param) {
        //校验活动信息
        checkActivityInfo(param);
        //保存活动信息
        ActivityDO activityDO = new ActivityDO();
        activityDO.setActivityName(param.getActivityName());
        activityDO.setDescription(param.getDescription());
        activityDO.setStatus(ActivityStatusEnum.RUNNING.name());
        activityMapper.insert(activityDO);


        //保存活动关联奖品信息
        List<CreatePrizeByActivityParam> prizeParams = param.getActivityPrizeList();
        List<ActivityPrizeDO> activityPrizeDOList = prizeParams.stream()
                .map(prizeParam->{
                    ActivityPrizeDO activityPrizeDO = new ActivityPrizeDO();
                    activityPrizeDO.setActivityId(activityDO.getId());
                    activityPrizeDO.setPrizeId(prizeParam.getPrizeId());
                    activityPrizeDO.setPrizeAmount(prizeParam.getPrizeAmount());
                    activityPrizeDO.setPrizeTiers(prizeParam.getPrizeTiers());
                    activityPrizeDO.setStatus(ActivityPrizeStatusEnum.INIT.name());
                    return activityPrizeDO;

                }).collect(Collectors.toList());

        activityPrizeMapper.batchInsert(activityPrizeDOList);



        //保存活动关联人员信息
        List<CreateUserByActivityParam> userParams = param.getActivityUserList();
        List<ActivityUserDO> activityUserDOList = userParams.stream()
                .map(userParam->{
                    ActivityUserDO activityUserDO = new ActivityUserDO();
                    activityUserDO.setActivityId(activityDO.getId());
                    activityUserDO.setUserId(userParam.getUserId());
                    activityUserDO.setUserName(userParam.getUserName());
                    activityUserDO.setStatus(ActivityUserStatusEnum.INIT.name());
                    return activityUserDO;
                }).collect(Collectors.toList());
        activityUserMapper.batchInsert(activityUserDOList);



        //整合活动信息，存放到redis
        List<Long> prizeIds = param.getActivityPrizeList().stream()
                .map(CreatePrizeByActivityParam::getPrizeId)
                .distinct()
                .collect(Collectors.toList());
        List<PrizeDO> prizeDOList =prizeMapper.batchSelectbyIds(prizeIds);
        ActivityDetailDTO detailDTO = convertToactivityDetailDTO(activityDO, activityUserDOList,
                prizeDOList,activityPrizeDOList);
        //缓存
        cacheActivity(detailDTO);


        //返回
        CreateActivityDTO createActivityDTO = new CreateActivityDTO();
        createActivityDTO.setActivityID(activityDO.getId());
        return createActivityDTO;

    }

    /**
     * 缓存完整活动信息
     * @param detailDTO
     */
    private void cacheActivity(ActivityDetailDTO detailDTO) {
        //key: ACTIVITY_
        //value: json
        if(null == detailDTO || null == detailDTO.getActivityID()){
            logger.warn("缓存活动不存在!");
            return;
        }
        try{
            redisUtil.set(ACTIVITY_PREFIX+detailDTO.getActivityID(),
                    JacksonUtil.writeValueAsString(detailDTO),
                    ACTIVITY_TIMEOUT);
        }catch (Exception e){
            logger.error("缓存活动异常，ActivityDetailDTO={}",
                    JacksonUtil.writeValueAsString(detailDTO),
                    e);
        }
    }

    /**
     * 根据活动id从缓存获取活动详细信息
     * @param activityId
     * @return
     */
    private ActivityDetailDTO getActivityFromCache(Long activityId){
        if(null == activityId){
            logger.warn("获取缓存活动数据的activityId为空!");
            return null;
        }
        try{
            String str = redisUtil.get(ACTIVITY_PREFIX + activityId);
            if(!StringUtils.hasText(str)){
                logger.warn("获取缓存活动数据为空! key={}",ACTIVITY_PREFIX + activityId);
                return null;
            }
            return JacksonUtil.readValue(str,ActivityDetailDTO.class);
        } catch (Exception e){
            logger.error("从缓存中获取活动信息异常，key={}",ACTIVITY_PREFIX + activityId,e);
            return null;
        }

    }
    //多表DO转换为ActivityDetailDTO
    private ActivityDetailDTO convertToactivityDetailDTO(ActivityDO activityDO,
                                                         List<ActivityUserDO> activityUserDOList,
                                                         List<PrizeDO> prizeDOList,
                                                         List<ActivityPrizeDO> activityPrizeDOList) {
        ActivityDetailDTO detailDTO = new ActivityDetailDTO();
        detailDTO.setActivityID(activityDO.getId());
        detailDTO.setActivityName(activityDO.getActivityName());
        detailDTO.setDesc(activityDO.getDescription());
        detailDTO.setStatus(ActivityStatusEnum.fromName(activityDO.getStatus()));

        List<ActivityDetailDTO.PrizeDTO> prizeDTOList = activityPrizeDOList
                .stream()
                .map(apDO->{
                    ActivityDetailDTO.PrizeDTO prizeDTO = new ActivityDetailDTO.PrizeDTO();

                    prizeDTO.setPrizeId(apDO.getPrizeId());
                    Optional<PrizeDO> optionalPrizeDO = prizeDOList.stream()
                            .filter(prizeDO -> prizeDO.getId().equals(apDO.getPrizeId()))
                            .findFirst();
                    //如果prizeDO为空，不执行方法，非空执行

                    optionalPrizeDO.ifPresent(prizeDO -> {
                        prizeDTO.setName(prizeDO.getName());
                        prizeDTO.setImageUrl(prizeDO.getImageUrl());
                        prizeDTO.setPrice(prizeDO.getPrice());
                        prizeDTO.setDescription(prizeDO.getDescription());
                    });

                    prizeDTO.setTiers(ActivityPrizeTiersEnum.fromName(apDO.getPrizeTiers()));
                    prizeDTO.setPrizeAmount(apDO.getPrizeAmount());
                    prizeDTO.setStatus(ActivityPrizeStatusEnum.fromName(apDO.getStatus()));
                    return prizeDTO;
                }).collect(Collectors.toList());
        detailDTO.setPrizeDTOList(prizeDTOList);

        List<ActivityDetailDTO.UserDTO> userDTOList = activityUserDOList.stream()
                .map(auDO->{
                    ActivityDetailDTO.UserDTO userDTO = new ActivityDetailDTO.UserDTO();
                    userDTO.setUserId(auDO.getUserId());
                    userDTO.setUserName(auDO.getUserName());
                    userDTO.setStatus(ActivityUserStatusEnum.fromName(auDO.getStatus()));
                    return userDTO;
                }).collect(Collectors.toList());
        detailDTO.setUserDTOList(userDTOList);
        return detailDTO;

    }

    /**
     * 校验活动
     * @param param
     */
    private void checkActivityInfo(CreateActivityParam param) {

        if(null == param){
            throw new ServiceException(ServiceErrorCodeConstants.CREATE_ACTIVITY_INFO_IS_EMPTY);
        }

        //人员在人员表是否存在
        List<Long> userIds = param.getActivityUserList()
                .stream()
                .map(CreateUserByActivityParam::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> existUserIds = userMapper.selectExistById(userIds);
        userIds.forEach(id ->{
            if(!existUserIds.contains(id)){
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_USER_ERROR);
            }

        });

        //奖品id在奖品表是否存在
        List<Long> prizeIds = param.getActivityPrizeList()
                .stream()
                .map(CreatePrizeByActivityParam::getPrizeId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> existPrizeIds = prizeMapper.selectExistByIds(prizeIds);
        prizeIds.forEach(id ->{
            if(!existPrizeIds.contains(id)){
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_ERROR);

            }
        });

        //人员数量大于等于奖品数量
        int userAmount = param.getActivityUserList().size();
        long prizeAmount = param.getActivityPrizeList()
                .stream()
                .mapToLong(CreatePrizeByActivityParam::getPrizeAmount)
                .sum();
        if(userAmount < prizeAmount){
            throw new ServiceException(ServiceErrorCodeConstants.USER_PRIZE_AMOUNT_ERROR);
        }

        //校验活动奖品等级有效
        param.getActivityPrizeList().forEach(prize->{
            if(null == ActivityPrizeTiersEnum.fromName(prize.getPrizeTiers())){
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_PRIZE_TIERS_ERROR);
            }
        });
    }
}
