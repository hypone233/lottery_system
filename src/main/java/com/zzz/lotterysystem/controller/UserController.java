package com.zzz.lotterysystem.controller;


import com.zzz.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ControllerException;
import com.zzz.lotterysystem.common.pojo.CommonResult;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.ShortMessageLoginParam;
import com.zzz.lotterysystem.controller.param.UserPasswordLoginParam;
import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.controller.result.BaseUserInfoResult;
import com.zzz.lotterysystem.controller.result.UserLoginResult;
import com.zzz.lotterysystem.controller.result.UserRegisterResult;
import com.zzz.lotterysystem.service.UserService;
import com.zzz.lotterysystem.service.VerificationCodeService;
import com.zzz.lotterysystem.service.dto.UserDTO;
import com.zzz.lotterysystem.service.dto.UserLoginDTO;
import com.zzz.lotterysystem.service.dto.UserRegisterDTO;
import com.zzz.lotterysystem.service.enums.UserIdentityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    /**
     *注册
     */
    @RequestMapping("/register")
    public CommonResult<UserRegisterResult> userRegister(
           @Validated @RequestBody UserRegisterParam param){

        logger.info("userRegister userRegisterParam:{}", JacksonUtil.writeValueAsString(param));

        UserRegisterDTO userRegisterDTO = userService.register(param);
        return CommonResult.success(convertToUserRegisterResult(userRegisterDTO));

    }

    /**
     * 短信验证码发送
     * @param phoneNumber
     * @return
     */
    @RequestMapping("/verification-code/send")
    public CommonResult<Boolean> senVerificationCode(String phoneNumber){
        logger.info("senVerificationCode phoneNumber:{}", phoneNumber);
        verificationCodeService.sendVerificationCode(phoneNumber);
        return CommonResult.success(Boolean.TRUE);
    }

    @RequestMapping("/password/login")
    public CommonResult<UserLoginResult> userPasswordLogin(
           @Validated @RequestBody UserPasswordLoginParam param){
        logger.info("userPasswordLogin UserPasswordLoginParam:{}",
                JacksonUtil.writeValueAsString(param));
        UserLoginDTO userLoginDTO = userService.login(param);
        return CommonResult.success(convertToUserLoginResult(userLoginDTO));

    }


    @RequestMapping("/message/login")
    public CommonResult<UserLoginResult> shortMessageLogin(
            @Validated @RequestBody ShortMessageLoginParam param){
        logger.info("shortMessageLogin ShortMessageLoginParam:{}",
                JacksonUtil.writeValueAsString(param));
        UserLoginDTO userLoginDTO = userService.login(param);
        return CommonResult.success(convertToUserLoginResult(userLoginDTO));


    }

    @RequestMapping("/base-user/find-list")
    public CommonResult<List<BaseUserInfoResult>> findBaseUserInfo(String identity) {
        logger.info("BaseUserInfoResult identity:{}", identity);
        List<UserDTO> userDTOList = userService.findUserInfo(
                UserIdentityEnum.forName(identity));
        return CommonResult.success(converToList(userDTOList));
    }

    private List<BaseUserInfoResult> converToList(List<UserDTO> userDTOList) {
        if(CollectionUtils.isEmpty(userDTOList)){
            return Arrays.asList();
        }
        return  userDTOList.stream()
                .map(userDTO -> {
                    BaseUserInfoResult result = new BaseUserInfoResult();
                    result.setUserId(userDTO.getUserId());
                    result.setUserName(userDTO.getUserName());
                    result.setIdentity(userDTO.getIdentity().name());
                    return result;
                }).collect(Collectors.toList());
    }


    private UserLoginResult convertToUserLoginResult(UserLoginDTO userLoginDTO) {

        if(null == userLoginDTO){
            throw new ControllerException(ControllerErrorCodeConstants.LOGIN_ERROR);
        }
        UserLoginResult result = new UserLoginResult();
        result.setToken(userLoginDTO.getToken());
        result.setIdentity(userLoginDTO.getIdentity().name());
        return result;
    }

    private UserRegisterResult convertToUserRegisterResult(UserRegisterDTO userRegisterDTO){
        UserRegisterResult result = new UserRegisterResult();
        if(null == userRegisterDTO){
            throw new ControllerException(ControllerErrorCodeConstants.REGISTER_ERROR);
        }

        result.setUserId(userRegisterDTO.getUserId());
        return result;
    }
}
