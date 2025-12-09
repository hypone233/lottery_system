package com.zzz.lotterysystem.service;

import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.service.dto.UserRegisterDTO;

public interface UserService {

    /**
     * 注册
     */
    UserRegisterDTO register(UserRegisterParam param);
}
