package com.zzz.lotterysystem.common.utils;



import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    /**
     * 密钥：Base64编码的密钥
     */
    private static final String SECRET =
            "UzmCvFGKyH7aWDgdi1ks53hRYSZuw3AZJPu5/4DTLzA=";

    /**
     * 生成安全密钥：将一个 Base64 编码的密钥解码并创建一个 HMAC SHA 密钥。
     */
    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

    /**
     * 过期时间(单位: 毫秒)
     */
    private static final long EXPIRATION = 60 * 60 * 1000;

    /**
     * 生成密钥
     */
    public static String genJwt(Map<String, Object> claim) {
        String jwt = Jwts.builder()
                .setClaims(claim)                  // 自定义内容
                .setIssuedAt(new Date())           // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 设置过期时间
                .signWith(SECRET_KEY)              // 签名算法
                .compact();
        return jwt;
    }

    /**
     * 验证密钥
     */
    public static Claims parseJWT(String jwt) {
        if (!StringUtils.hasLength(jwt)) {
            return null;
        }

        JwtParserBuilder jwtParserBuilder =
                Jwts.parserBuilder().setSigningKey(SECRET_KEY);

        Claims claims = null;
        try {
            claims = jwtParserBuilder.build().parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            logger.error("解析令牌错误,jwt:{}", jwt, e);
        }
        return claims;
    }

    /**
     * 从 token 中获取用户ID
     */
    public static Integer getUserIdFromToken(String jwtToken) {
        Claims claims = JWTUtil.parseJWT(jwtToken);
        if (claims != null) {
            Map<String, Object> userInfo = new HashMap<>(claims);
            return (Integer) userInfo.get("userId");
        }
        return null;
    }
}

