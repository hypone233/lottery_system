package com.zzz.lotterysystem.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.RegexUtil;
import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.dao.dataobject.Encrypt;
import com.zzz.lotterysystem.dao.dataobject.UserDo;
import com.zzz.lotterysystem.dao.mapper.UserMapper;
import com.zzz.lotterysystem.service.UserService;
import com.zzz.lotterysystem.service.dto.UserRegisterDTO;
import com.zzz.lotterysystem.service.enums.UserIdentityEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class  UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserRegisterDTO register(UserRegisterParam param){

        //注册校验
        checkRegisterInfo(param);

        //加密私密数据
        UserDo userDo = new UserDo();
        userDo.setUserName(param.getName());
        userDo.setEmail(param.getMail());
        userDo.setPhoneNumber(new Encrypt(param.getPhoneNumber()));
        userDo.setIdentity(param.getIdentity());
        if(StringUtils.hasText(param.getPassword())){
            userDo.setPassword(DigestUtil.sha256Hex(param.getPassword()));

        }
        //保存数据
        userMapper.insert(userDo);
        //返回
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUserId(userDo.getId());
        return userRegisterDTO;
    }

    private void checkRegisterInfo(UserRegisterParam param) {
        if(null == param){
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_INFO_IS_EMPTY);
        }
        //检验邮箱格式
        if(!RegexUtil.checkMail(param.getMail())){
            throw new ServiceException(ServiceErrorCodeConstants.MAIL_ERROR);
        }
        //校验手机号格式
        if(!RegexUtil.checkMobile(param.getPhoneNumber())){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);
        }
        //校验身份信息
        if(null == UserIdentityEnum.forName(param.getIdentity())){
            throw new ServiceException(ServiceErrorCodeConstants.IDENTITY_ERROR);
        }
        //校验管理员密码（必填）
        if(param.getIdentity().equalsIgnoreCase(UserIdentityEnum.ADMIN.name())
               && !StringUtils.hasText(param.getPassword()) ){
            throw new ServiceException(ServiceErrorCodeConstants.PASSWORD_IS_EMPTY);
        }

        //密码至少6位
         if(StringUtils.hasText(param.getPassword())
                    && !RegexUtil.checkPassword(param.getPassword())){
             throw new ServiceException(ServiceErrorCodeConstants.PASSWORD_ERROR);
         }
        //校验邮箱是否被使用
        if(checkMailUsed(param.getMail())){
            throw new ServiceException(ServiceErrorCodeConstants.MAIL_USED);
        }
        //校验手机号是否被使用
        if(checkPhoneNumberUsed(param.getPhoneNumber())){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_USED);
        }

    }

    private boolean checkPhoneNumberUsed( String phoneNumber) {
        int count = userMapper.countByPhone(new Encrypt(phoneNumber));
        return count > 0;
    }

    private boolean checkMailUsed( String mail) {
        int count = userMapper.countByMail(mail);
        return count > 0;
    }
}
