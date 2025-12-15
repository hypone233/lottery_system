package com.zzz.lotterysystem.common.errorcode;

public interface ControllerErrorCodeConstants {
    //人员模块错误码
    ErrorCode REGISTER_ERROR = new ErrorCode(100,"注册失败");
    ErrorCode LOGIN_ERROR = new ErrorCode(101,"登录失败");

    //奖品模块错误码
    ErrorCode FIND_PRIZE_LIST_ERROR = new ErrorCode(200,"查询奖品列表失败");

}
