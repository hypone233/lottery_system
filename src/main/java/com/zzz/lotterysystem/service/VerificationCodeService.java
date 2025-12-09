package com.zzz.lotterysystem.service;

public interface VerificationCodeService {

    /**
     * 发送验证码
     *
     */
    void sendVerificationCode(String phoneNumber);

    /**
     * 从缓存获取验证码
     * @param phoneNumber
     * @return
     */
    String getVerificationCode(String phoneNumber);

}

