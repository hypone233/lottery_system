package com.zzz.lotterysystem.service;

import com.zzz.lotterysystem.controller.param.UserLoginParam;
import com.zzz.lotterysystem.controller.param.UserPasswordLoginParam;
import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.service.dto.UserLoginDTO;
import com.zzz.lotterysystem.service.dto.UserRegisterDTO;
import com.zzz.lotterysystem.service.enums.UserIdentityEnum;
import com.zzz.lotterysystem.service.dto.UserDTO;

import java.util.List;

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

    /**
     *
     * 根据身份查询人员列表
     * @param identity
     * @return
     */
    List<UserDTO> findUserInfo(UserIdentityEnum identity);
}
