package com.zzz.lotterysystem.service.impl;

import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.CaptchaUtil;
import com.zzz.lotterysystem.common.utils.RedisUtil;
import com.zzz.lotterysystem.common.utils.RegexUtil;
import com.zzz.lotterysystem.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    //redis key标准化：定义前缀
    private static final String VERIFICATION_CODE_PREFIX = "VERIFICATION_CODE_";
    private static final Long VERIFICATION_CODE_TIMEOUT = 60L;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void sendVerificationCode(String phoneNumber) {
        // 校验手机号
        if(!RegexUtil.checkMobile(phoneNumber)){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);

        }
        //生成随机验证码
        String code = CaptchaUtil.getCaptcha(4);

        //发送验证(短信服务无法自用，打算设计为自测)
        System.out.println("验证码是 :" + code);
        //缓存验证码
        redisUtil.set(VERIFICATION_CODE_PREFIX + phoneNumber,code,VERIFICATION_CODE_TIMEOUT);

    }

    @Override
    public String getVerificationCode(String phoneNumber) {
        //校验手机号
        if(!RegexUtil.checkMobile(phoneNumber)){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);

        }


        return redisUtil.get(VERIFICATION_CODE_PREFIX + phoneNumber);
    }
}

