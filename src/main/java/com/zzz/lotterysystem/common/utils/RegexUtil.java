package com.zzz.lotterysystem.common.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * 邮箱：xxx@xx.xxx (形如：abc@qq.com)
     *
     * @param content
     * @return
     */
    public static boolean checkMail(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        /**
         * ^ 表示匹配字符串的开始。
         * [a-z0-9]+ 表示匹配一个或多个小写字母或数字。
         * ([._\\-]*[a-z0-9])* 表示匹配零次或多次：符号（. _ -）+ 字母数字。
         * @ 表示必须包含的 "@" 符号。
         * ([a-z0-9]+[-a-z0-9]*[a-z0-9]+\\.){1,63} 表示域名部分，可重复 1-63 次。
         * [a-z0-9]+ 表示顶级域名（字母或数字），例如 com / cn / net。
         * $ 表示匹配字符串结束。
         */
        String regex = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+\\.){1,63}[a-z0-9]+$";
        return Pattern.matches(regex, content);
    }


    /**
     * 手机号码以 1 开头的 11 位数字
     *
     * @param content
     * @return
     */
    public static boolean checkMobile(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        /**
         * ^ 开头
         * 1 开头
         * [3-9] 第二位
         * [0-9]{9} 剩余 9 位
         * $ 结束
         */
        String regex = "^1[3-9][0-9]{9}$";
        return Pattern.matches(regex, content);
    }


    /**
     * 密码强度正则：6 到 12 位，只允许数字和字母
     *
     * @param content
     * @return
     */
    public static boolean checkPassword(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        /**
         * ^ 开头
         * [0-9A-Za-z]{6,12} 允许数字、大小写字母，6-12 位
         * $ 结束
         */
        String regex = "^[0-9A-Za-z]{6,12}$";
        return Pattern.matches(regex, content);
    }
}
