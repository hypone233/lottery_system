package com.zzz.lotterysystem.controller;


import com.zzz.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ControllerException;
import com.zzz.lotterysystem.common.pojo.CommonResult;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.controller.result.UserRegisterResult;
import com.zzz.lotterysystem.service.UserService;
import com.zzz.lotterysystem.service.VerificationCodeService;
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





    private UserRegisterResult convertToUserRegisterResult(UserRegisterDTO userRegisterDTO){
        UserRegisterResult result = new UserRegisterResult();
        if(null == userRegisterDTO){
            throw new ControllerException(ControllerErrorCodeConstants.REGISTER_ERROR);
        }

        result.setUserId(userRegisterDTO.getUserId());
        return result;
    }
}
