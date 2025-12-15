package com.zzz.lotterysystem.controller.result;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FindPrizeListResult implements Serializable {
    /**
     * 页面总数
     */
    private Integer total;

    /**
     *
     */
    private List<PrizeInfo> records;

    @Data
    public static class PrizeInfo implements Serializable{

        /**
         * 奖品id
         */
        private Long prizeId;

        /**
         * 名称
         */
        private String prizeName;
        /**
         * 描述
         */
        private String description;
        /**
         * 价值
         */
        private BigDecimal price;
        /**
         * 奖品图
         */
        private String imageUrl;

    }
}
