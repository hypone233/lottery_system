package com.zzz.lotterysystem.dao.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDo extends BaseDo{
    private String userName;

    private String email;

    private Encrypt phoneNumber;

    private String password;

    private  String identity;
}
