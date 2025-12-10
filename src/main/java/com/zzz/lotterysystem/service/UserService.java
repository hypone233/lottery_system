package com.zzz.lotterysystem.service;

import com.zzz.lotterysystem.controller.param.UserLoginParam;
import com.zzz.lotterysystem.controller.param.UserPasswordLoginParam;
import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.service.dto.UserLoginDTO;
import com.zzz.lotterysystem.service.dto.UserRegisterDTO;

public interface UserService {

    /**
     * 注册
     */
    UserRegisterDTO register(UserRegisterParam param);

    /**
     * 用户登录
     * @param param
     * @return
     */
    UserLoginDTO login(UserLoginParam param);
}
