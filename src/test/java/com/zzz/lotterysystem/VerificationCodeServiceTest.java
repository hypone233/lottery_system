package com.zzz.lotterysystem;

import com.zzz.lotterysystem.service.VerificationCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VerificationCodeServiceTest {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Test
    void testSend(){

        verificationCodeService.sendVerificationCode("19212341234");
        System.out.println(verificationCodeService.getVerificationCode("19212341234"));
    }

}
