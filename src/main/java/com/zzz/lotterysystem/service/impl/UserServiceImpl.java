package com.zzz.lotterysystem.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.zzz.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.zzz.lotterysystem.common.exception.ServiceException;
import com.zzz.lotterysystem.common.utils.JWTUtil;
import com.zzz.lotterysystem.common.utils.RegexUtil;
import com.zzz.lotterysystem.controller.param.ShortMessageLoginParam;
import com.zzz.lotterysystem.controller.param.UserLoginParam;
import com.zzz.lotterysystem.controller.param.UserPasswordLoginParam;
import com.zzz.lotterysystem.controller.param.UserRegisterParam;
import com.zzz.lotterysystem.dao.dataobject.Encrypt;
import com.zzz.lotterysystem.dao.dataobject.UserDO;
import com.zzz.lotterysystem.dao.mapper.UserMapper;
import com.zzz.lotterysystem.service.UserService;
import com.zzz.lotterysystem.service.VerificationCodeService;
import com.zzz.lotterysystem.service.dto.UserDTO;
import com.zzz.lotterysystem.service.dto.UserLoginDTO;
import com.zzz.lotterysystem.service.dto.UserRegisterDTO;
import com.zzz.lotterysystem.service.enums.UserIdentityEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class  UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private VerificationCodeService verificationCodeService;

    @Override
    public UserRegisterDTO register(UserRegisterParam param){

        //注册校验
        checkRegisterInfo(param);

        //加密私密数据
        UserDO userDo = new UserDO();
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
    @Override
    public UserLoginDTO login(UserLoginParam param) {
        UserLoginDTO userLoginDTO;

        if(param instanceof UserPasswordLoginParam loginParam){
            //密码登录
            userLoginDTO = loginByUserPassword(loginParam);
        }else if(param instanceof ShortMessageLoginParam loginParam){
            //短信验证码登录
            userLoginDTO = loginByShortMessage(loginParam);
        }else {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_INFO_NOT_EXIST);

        }


        return userLoginDTO;
    }

    @Override
    public List<UserDTO> findUserInfo(UserIdentityEnum identity) {
        String identityString = null == identity ? null : identity.name();

        List<UserDO> userDOList = userMapper.selectUserListByIdentity(identityString);
        List<UserDTO> userDTOList = userDOList.stream()
                .map(userDO -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setUserId(userDO.getId());
                    userDTO.setUserName(userDO.getUserName());
                    userDTO.setEmail(userDO.getEmail());
                    userDTO.setPhoneNumber(userDO.getPhoneNumber().getValue());
                    userDTO.setIdentity(UserIdentityEnum.forName(userDO.getIdentity()));
                    return userDTO;
                }).collect(Collectors.toList());
        return userDTOList;
    }

    /**
     * 短信验证登录
     * @param loginParam
     * @return
     */
    private UserLoginDTO loginByShortMessage(ShortMessageLoginParam loginParam) {
        if(!RegexUtil.checkMobile(loginParam.getLoginMobile())){
            throw new ServiceException(ServiceErrorCodeConstants.PHONE_NUMBER_ERROR);
        }

        //获取用户数据
        UserDO userDO =  userMapper.selectByPhoneNumber(new Encrypt(loginParam.getLoginMobile()));
        if(null == userDO) {
            throw new ServiceException(ServiceErrorCodeConstants.USER_INFO_IS_EMPTY);
        }else if (StringUtils.hasText(loginParam.getMandatoryIdentity())
                   && !loginParam.getMandatoryIdentity()
                                .equalsIgnoreCase(userDO.getIdentity())){
            throw new ServiceException(ServiceErrorCodeConstants.IDENTITY_ERROR);
        }
        //校验验证码
       String code = verificationCodeService.getVerificationCode(loginParam.getLoginMobile());
        if(!loginParam.getVerificationCode().equals(code)){
            throw new ServiceException(ServiceErrorCodeConstants.VERIFICATION_CODE_ERROR);

        }

        Map<String,Object>claim = new HashMap<>();
        claim.put("id",userDO.getId());
        claim.put("identity",userDO.getIdentity());
        String token = JWTUtil.genJwt(claim);


        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setToken(token);
        userLoginDTO.setIdentity(UserIdentityEnum.forName(userDO.getIdentity()));
        return userLoginDTO;

    }

    /**
     * 密码登录
     * @param loginParam
     * @return
     */
    private UserLoginDTO loginByUserPassword(UserPasswordLoginParam loginParam) {
        //判断手机或邮箱登录
        UserDO userDO = null;
        if(RegexUtil.checkMail(loginParam.getLoginName())){
            //邮箱登录
            userDO = userMapper.selectByMail(loginParam.getLoginName());
        }else if(RegexUtil.checkMobile(loginParam.getLoginName())){
            userDO = userMapper.selectByPhoneNumber(new Encrypt(loginParam.getLoginName()));
        }else {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_NOT_EXIST);
        }

        //校验登录
        if(null == userDO){
            throw new ServiceException(ServiceErrorCodeConstants.USER_INFO_IS_EMPTY);
        }else if(StringUtils.hasText(loginParam.getMandatoryIdentity())
                  && !loginParam.getMandatoryIdentity()
                               .equalsIgnoreCase(userDO.getIdentity())){
            //强制身份登录，身份校验不通过
            throw new ServiceException(ServiceErrorCodeConstants.IDENTITY_ERROR);

        }else if (!DigestUtil.sha256Hex(loginParam.getPassword())
                .equals(userDO.getPassword())){
            //校验密码不同
            throw new ServiceException(ServiceErrorCodeConstants.PASSWORD_ERROR);
        }

        Map<String,Object>claim = new HashMap<>();
        claim.put("id",userDO.getId());
        claim.put("identity",userDO.getIdentity());
        String token = JWTUtil.genJwt(claim);


        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setToken(token);
        userLoginDTO.setIdentity(UserIdentityEnum.forName(userDO.getIdentity()));
        return userLoginDTO;


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
