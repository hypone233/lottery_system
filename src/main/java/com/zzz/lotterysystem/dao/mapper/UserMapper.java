package com.zzz.lotterysystem.dao.mapper;

import com.zzz.lotterysystem.dao.dataobject.Encrypt;
import com.zzz.lotterysystem.dao.dataobject.UserDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    /**
     * 查询邮箱绑定人数
     *
     * @param email
     * @return
     */
    @Select("select count(*) from user where email = #{email}")
    int countByMail(@Param("email")String email);

    @Select("select count(*) from user where phone_number = #{phoneNumber}")
    int countByPhone(@Param("phoneNumber") Encrypt phoneNumber);
    @Insert("insert into user(user_name, email ,phone_number, password, identity)" +
            " values (#{userName},#{email},#{phoneNumber},#{password},#{identity})")
    @Options(useGeneratedKeys = true, keyProperty = "id",keyColumn = "id")
    void insert(UserDO userDo);

    @Select("select * from user where email = #{email}")
    UserDO selectByMail(@Param("email") String email);
    @Select("select * from user where phone_number = #{phoneNumber}")
    UserDO selectByPhoneNumber(@Param("phoneNumber") Encrypt phoneNumber);
}
