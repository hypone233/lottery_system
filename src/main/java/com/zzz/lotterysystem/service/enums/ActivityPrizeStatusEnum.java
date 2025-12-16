package com.zzz.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityPrizeStatusEnum {

    INIT(1, "初始化"),

    COMPLETED(2, "已被抽取");
    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String message;

    public static ActivityPrizeStatusEnum fromName(String name) {
        for (ActivityPrizeStatusEnum activityPrizeStatus :
                ActivityPrizeStatusEnum.values()) {
            if (activityPrizeStatus.name().equalsIgnoreCase(name)) {
                return activityPrizeStatus;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}



