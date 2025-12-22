package com.zzz.lotterysystem;

import com.zzz.lotterysystem.common.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailTest {

    @Autowired
    private MailUtil mailUtil;

    @Test
    void sendMessage(){
        mailUtil.sendSampleMail("2788547618@qq.com","123","正文");
    }


}
