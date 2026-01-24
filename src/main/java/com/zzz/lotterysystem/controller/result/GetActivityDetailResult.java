package com.zzz.lotterysystem.controller.result;

import com.zzz.lotterysystem.service.dto.ActivityDetailDTO;
import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityPrizeTiersEnum;
import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityUserStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetActivityDetailResult implements Serializable {

    //活动信息
    private Long activityID;

    private String activityName;

    private String description;

    private Boolean valid;



    //奖品信息
    private List<Prize> prizes;

    //人员信息
    private List<User> users;




    @Data
    public static class Prize{
        /**
         * 奖品ID
         */
        private Long prizeId;
        /**
         * 奖品名
         */
        private String name;

        /**
         * 图片索引
         */
        private String imageUrl;

        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 描述
         */
        private String description;

        /**
         * 奖品等级
         * @see ActivityPrizeTiersEnum#getMessage()
         */
        private String prizeTierName;

        /**
         * 奖品数量
         */
        private Long prizeAmount;

        /**
         * 奖品状态
         */
        private Boolean valid;



    }

    @Data
    public static class User{

        private Long userId;

        private String userName;

        private Boolean valid;

    }
}
