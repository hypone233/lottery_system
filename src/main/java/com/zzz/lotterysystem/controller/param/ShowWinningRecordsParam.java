package com.zzz.lotterysystem.controller.param;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShowWinningRecordsParam {

  @NotNull(message = "活动Id不为空")
  private Long activityId;

  private Long prizeId;


}
