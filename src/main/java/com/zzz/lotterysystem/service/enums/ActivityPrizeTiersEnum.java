package com.zzz.lotterysystem.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityPrizeTiersEnum {

    FIRST_PRIZE(1,"⼀等奖"),
    SECOND_PRIZE(2,"⼆等奖"),
    THIRD_PRIZE(3,"三等奖");
    /**
     * code
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String message;

    public static ActivityPrizeTiersEnum fromName(String name) {
        for (ActivityPrizeTiersEnum activityPrizeTiersEnum :
                ActivityPrizeTiersEnum.values()) {
            if (activityPrizeTiersEnum.name().equalsIgnoreCase(name)) {
                return activityPrizeTiersEnum;
            }
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
   /* public static ActivityPrizeTiersEnum fromMessage(String message) {
        for (ActivityPrizeTiersEnum activityPrizeTiersEnum :
                ActivityPrizeTiersEnum.values()) {
            if (activityPrizeTiersEnum.getMessage().equalsIgnoreCase(message))
            {
                return activityPrizeTiersEnum;
            }
        }
        throw new IllegalArgumentException("Invalid message: " + message);
    }*/
}


