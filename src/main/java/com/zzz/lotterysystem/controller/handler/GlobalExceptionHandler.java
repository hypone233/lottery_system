package com.zzz.lotterysystem.controller.handler;


import com.zzz.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ControllerException;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.pojo.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.rmi.ServerException;

@RestControllerAdvice       //全局异常捕获
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceException(ServiceException e){
        //错误日志打印
        logger.error("ServiceException:",e);
        //错误结果
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                e.getMessage());

    }

    @ExceptionHandler(value = ControllerException.class)
    public CommonResult<?> controllerException(ControllerException e){
        //错误日志打印
        logger.error("ControllerException:",e);
        //错误结果
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                e.getMessage());

    }
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> exception(Exception e){
        //错误日志打印
        logger.error("服务异常:",e);
        //错误结果
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                e.getMessage());

    }



}
