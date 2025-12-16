package com.zzz.lotterysystem.dao.dataobject;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityUserDO extends BaseDO{

    /**
     * 活动id
     */
    private Long activityId;
    /**
     * 圈选的⼈员id
     */
    private Long userId;
    /**
     * ⽤⼾名
     */
    private String userName;
    /**
     * ⼈员状态
     *
     */
    private String status;


}
