package com.zzz.lotterysystem;

import com.zzz.lotterysystem.common.pojo.CommonResult;
import com.zzz.lotterysystem.common.utils.JacksonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.script.DigestUtils;

import cn.hutool.crypto.digest.DigestUtil;


import java.util.Arrays;
import java.util.List;

@SpringBootTest
class LotterySystemApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void sha256Test(){

    }
    @Test
    void JacksonUtilTest(){

        CommonResult<String> result = CommonResult.success("success");
        String str;

        str = JacksonUtil.writeValueAsString(result);
        System.out.println(str);

        result=JacksonUtil.readValue(str,CommonResult.class);
        System.out.println(result.getData());

        List<CommonResult<String>> commonResults= Arrays.asList(
                CommonResult.success("success1"),
                CommonResult.success("success2")
        );
        str = JacksonUtil.writeValueAsString(commonResults);
        System.out.println(str);

        commonResults=JacksonUtil.readListValue(str,CommonResult.class);
        for (CommonResult<String> commonResult:commonResults){
            System.out.println(commonResult.getData());
        }
    }







}
