package com.shanjin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/19
 * @desc 正则匹配工具类
 */
public class RegexUtil {

    private static final String REGEX_PHONE = "^(1)[0-9]{10}$";

    public static final boolean isPhoneNo(String phone) {

        Pattern pattern = Pattern.compile(REGEX_PHONE);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

}
