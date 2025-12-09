package com.zzz.lotterysystem.common.exception;

import com.zzz.lotterysystem.common.errorcode.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.imageio.event.IIOWriteProgressListener;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {
    /**
     * 异常码
     * @see com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants
     */
    private Integer code;

    private String message;

    public ServiceException(){

    }
    public ServiceException(Integer code,String message){
        this.code=code;
        this.message=message;

    }
    public ServiceException(ErrorCode errorCode){
        this.code=errorCode.getCode();
        this.message=errorCode.getMsg();
    }
}
