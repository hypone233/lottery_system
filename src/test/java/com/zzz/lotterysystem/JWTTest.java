package com.zzz.lotterysystem;



import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;

@SpringBootTest
public class JWTTest {

    @Test
    public void genKey() {
        // 使用 HS256 算法生成随机密钥
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // 转为 Base64 字符串
        String secretString = Encoders.BASE64.encode(key.getEncoded());

        System.out.println("生成的 Base64 密钥: " + secretString);
    }


}
