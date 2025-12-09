package com.zzz.lotterysystem.controller.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.PriorityQueue;

@Data
public class UserRegisterParam implements Serializable {
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空！")
    private String name;
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空！")
    private String mail;
    /**
     * 电话
     */
    @NotBlank(message = "电话不能为空！")
    private String phoneNumber;
    /**
     * 密码
     */
    private String password;
    /**
     * 身份信息
     */
    private String identity;
}
