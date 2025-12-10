package com.zzz.lotterysystem.controller.param;

import com.zzz.lotterysystem.service.enums.UserIdentityEnum;

import java.io.Serializable;

public class UserLoginParam implements Serializable {


    /**
     * 强制身份登录，不填不限制身份
     * @see UserIdentityEnum#name()
     */
    private String mandatoryIdentity;



}
