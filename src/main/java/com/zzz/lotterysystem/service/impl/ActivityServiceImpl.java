package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.controller.param.CreateActivityParam;
import com.zzz.lotterysystem.controller.param.CreatePrizeByActivityParam;
import com.zzz.lotterysystem.controller.param.CreateUserByActivityParam;
import com.zzz.lotterysystem.dao.mapper.PrizeMapper;
import com.zzz.lotterysystem.dao.mapper.UserMapper;
import com.zzz.lotterysystem.service.ActivityService;
import com.zzz.lotterysystem.service.dto.CreateActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.model.SpELExpressionEvaluator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PrizeMapper prizeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) //多表保证完整事务
    public CreateActivityDTO createActivity(CreateActivityParam param) {
        //校验活动信息
        checkActivityInfo(param);
        //保存活动信息

        //保存活动关联奖品信息

        //保存活动关联人员信息

        //整合活动信息，存放到redis

        //返回
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


    }
}
