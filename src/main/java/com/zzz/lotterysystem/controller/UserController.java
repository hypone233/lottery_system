package com.zzz.lotterysystem.controller;


import com.zzz.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ControllerException;
import com.zzz.lotterysystem.common.pojo.CommonResult;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.ShortMessageLoginParam;
import com.zzz.lotterysystem.controller.param.UserPasswordLoginParam;
import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.controller.result.UserLoginResult;
import com.zzz.lotterysystem.controller.result.UserRegisterResult;
import com.zzz.lotterysystem.service.UserService;
import com.zzz.lotterysystem.service.VerificationCodeService;
import com.zzz.lotterysystem.service.dto.UserLoginDTO;
import com.zzz.lotterysystem.service.dto.UserRegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



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
