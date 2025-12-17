package com.zzz.lotterysystem.service.dto;


import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import lombok.Data;

@Data
public class ActivityDTO {

    /**
     * 活动id
     */
    private Long activityId;
    /**
     * 活动名
     */
    private String activityName;
    /**
     * 活动描述
     */
    private String description;
    /**
     * 活动状态
     */
    private ActivityStatusEnum status;

    /**
     * 活动是否有效
     * @return
     */
    public Boolean valid(){
        return status.equals(ActivityStatusEnum.RUNNING);
    }

}
