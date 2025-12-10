package com.zzz.lotterysystem.service.dto;


import lombok.Data;

@Data
public class UserLoginDTO {

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 登录人员身份
     */
    private String identity;

}
