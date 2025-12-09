package com.zzz.lotterysystem.common.errorcode;

public interface ServiceErrorCodeConstants {

    //人员模块错误码
    ErrorCode REGISTER_INFO_IS_EMPTY = new ErrorCode(100,"注册信息为空");
    ErrorCode MAIL_ERROR = new ErrorCode(101,"邮箱错误");
    ErrorCode PHONE_NUMBER_ERROR = new ErrorCode(102,"手机号错误");
    ErrorCode IDENTITY_ERROR = new ErrorCode(103,"身份错误");
    ErrorCode PASSWORD_IS_EMPTY = new ErrorCode(104,"密码为空");
    ErrorCode PASSWORD_ERROR = new ErrorCode(105,"密码错误");
    ErrorCode MAIL_USED = new ErrorCode(106,"邮箱已被使用");
    ErrorCode PHONE_NUMBER_USED = new ErrorCode(107,"手机号已被使用");

}
