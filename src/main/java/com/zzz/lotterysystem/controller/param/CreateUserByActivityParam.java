package com.zzz.lotterysystem.controller.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreateUserByActivityParam implements Serializable {
    /**
     * 活动人员ID
     */
    @NotNull(message = "活动人员ID不能为空!")
    private Long userId;
    /**
     * 活动人员姓名
     */
    @NotBlank(message = "活动人员姓名不能为空!")
    private String userName;

}
