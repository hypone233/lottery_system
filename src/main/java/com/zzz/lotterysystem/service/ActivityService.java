package com.zzz.lotterysystem.service;

import com.zzz.lotterysystem.controller.param.CreateActivityParam;
import com.zzz.lotterysystem.service.dto.CreateActivityDTO;
import org.springframework.stereotype.Service;

@Service
public interface ActivityService {

    /**
     * 创建活动
     * @param param
     * @return
     */
    CreateActivityDTO createActivity(CreateActivityParam param);

}
