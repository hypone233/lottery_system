package com.zzz.lotterysystem.controller.param;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Data
public class CreatePrizeByActivityParam implements Serializable {

    /**
     * 活动关联奖品ID
     */
    @NotNull(message = "活动关联奖品ID不能为空!")
    private Long prizeId;
    /**
     * 奖品数量
     */
    @NotNull(message = "奖品数量不能为空!")
    private Long prizeAmount;
    /**
     *
     *  奖品等级
     */
    @NotBlank(message = "奖品等级不能为空!")
    private String prizeTiers;


}
