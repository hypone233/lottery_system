package com.zzz.lotterysystem.service.dto;

import com.zzz.lotterysystem.service.enums.ActivityPrizeStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityPrizeTiersEnum;
import com.zzz.lotterysystem.service.enums.ActivityStatusEnum;
import com.zzz.lotterysystem.service.enums.ActivityUserStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ActivityDetailDTO {

    //活动信息
    private Long activityID;

    private String activityName;

    private String desc;

    private ActivityStatusEnum status;

    public Boolean valid(){
        return status.equals(ActivityStatusEnum.RUNNING);
    }

    //奖品信息
    private List<PrizeDTO> prizeDTOList;

    //人员信息
    private List<UserDTO> userDTOList;




    @Data
    public static class PrizeDTO{
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
         */
        private ActivityPrizeTiersEnum tiers;

        /**
         * 奖品数量
         */
        private Long prizeAmount;

        /**
         * 奖品状态
         */
        private ActivityPrizeStatusEnum status;

        public Boolean valid(){
            return status.equals(ActivityPrizeStatusEnum.INIT);
        }

    }

    @Data
    public static class UserDTO{

        private Long userId;

        private String userName;

        private ActivityUserStatusEnum status;

        public Boolean valid(){
            return status.equals(ActivityUserStatusEnum.INIT);
        }
    }


}
