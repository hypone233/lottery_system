package com.zzz.lotterysystem.service.activitystatus.impl;

import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.service.ActivityService;
import com.zzz.lotterysystem.service.activitystatus.ActivityStatusManager;
import com.zzz.lotterysystem.service.activitystatus.operator.AbstractActivityOperator;
import com.zzz.lotterysystem.service.dto.ConvertActivityStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class ActivityStatusManagerImpl implements ActivityStatusManager {

    private static final Logger logger = LoggerFactory.getLogger(ActivityStatusManagerImpl.class);

    @Autowired
    private final Map<String, AbstractActivityOperator> operatorMap = new HashMap<>();

    @Autowired
    private ActivityService activityService;

    @Override
    public void handlerEvent(ConvertActivityStatusDTO convertActivityStatusDTO) {

        if(CollectionUtils.isEmpty(operatorMap)){
            logger.warn("operatorMap 为空！");
            return;
        }

        Map<String,AbstractActivityOperator> currMap = new HashMap<>(operatorMap);
        Boolean update = false;


        //先处理 人员奖品
        update = processConvertStatus(convertActivityStatusDTO,currMap,1);


        //后处理 活动

        update = processConvertStatus(convertActivityStatusDTO,currMap,2) || update;



        //更新缓存
        if(update){
            activityService.cacheActivity(convertActivityStatusDTO.getActivityId());
        }



    }

    @Override
    public void rollbackHandlerEvent(ConvertActivityStatusDTO convertActivityStatusDTO) {

        for(AbstractActivityOperator operator:operatorMap.values()){
            operator.convert(convertActivityStatusDTO);
        }
        activityService.cacheActivity(convertActivityStatusDTO.getActivityId());

    }

    private Boolean processConvertStatus(ConvertActivityStatusDTO convertActivityStatusDTO,
                                         Map<String, AbstractActivityOperator> currMap,
                                         int sequence) {
        Boolean update = false;
        // 遍历currMap
        Iterator<Map.Entry<String,AbstractActivityOperator>> iterator = currMap.entrySet().iterator();
        while (iterator.hasNext()){
            AbstractActivityOperator operator = iterator.next().getValue();

            //Operator是否需要转换
            if (operator.sequence() != sequence
                    || !operator.needConvert(convertActivityStatusDTO)){
                continue;
            }
            //需要转换
            if(!operator.convert(convertActivityStatusDTO)){
                logger.error("{} 状态转换失败",operator.getClass().getName());
                throw new ServiceException(ServiceErrorCodeConstants.ACTIVITY_STATUS_CONVERT_ERROR);
            }

            //currMap 删除当前operator
            iterator.remove();
            update=true;
        }

        //返回
        return update;

    }
}
