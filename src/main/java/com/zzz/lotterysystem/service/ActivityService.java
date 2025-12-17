package com.zzz.lotterysystem.service;

import com.zzz.lotterysystem.controller.param.CreateActivityParam;
import com.zzz.lotterysystem.controller.param.PageParam;
import com.zzz.lotterysystem.service.dto.ActivityDTO;
import com.zzz.lotterysystem.service.dto.ActivityDetailDTO;
import com.zzz.lotterysystem.service.dto.CreateActivityDTO;
import com.zzz.lotterysystem.service.dto.PageListDTO;
import org.springframework.stereotype.Service;

@Service
public interface ActivityService {

    /**
     * 创建活动
     * @param param
     * @return
     */
    CreateActivityDTO createActivity(CreateActivityParam param);

    /**
     * 翻页查询活动摘要列表
     * @param param
     * @return
     */
    PageListDTO<ActivityDTO> findActivityList(PageParam param);

    /**
     * 获取活动详细属性
     * @param activityId
     * @return
     */
    ActivityDetailDTO getActivityDetail(Long activityId);
}
