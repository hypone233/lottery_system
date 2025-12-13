package com.zzz.lotterysystem.service.dto;

import com.zzz.lotterysystem.dao.dataobject.Encrypt;
import com.zzz.lotterysystem.service.enums.UserIdentityEnum;
import lombok.Data;

@Data
public class UserDTO {

    private Long UserId;

    private String userName;

    private String email;

    private String phoneNumber;

    private UserIdentityEnum identity;

}
