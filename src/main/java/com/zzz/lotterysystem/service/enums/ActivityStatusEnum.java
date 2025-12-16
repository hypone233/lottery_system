package com.zzz.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.SpringApplication;

@AllArgsConstructor
@Getter
public enum ActivityStatusEnum {

    RUNNING(1,"活动进行中"),

    COMPLETED( 2, "活动结束");

    private final Integer code;

    private final String message;

    public static ActivityStatusEnum fromName(String name) {
        for (ActivityStatusEnum activityStatus : ActivityStatusEnum.values()) {
            if (activityStatus.name().equalsIgnoreCase(name)) {
                return activityStatus;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}


