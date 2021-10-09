package com.yunduan.utils;

import java.util.regex.Pattern;

public class MatchDataUtil {


    /**
     * 匹配数据属于手机号还是邮箱
     * @param data 输入数据
     * @return statusCodeUtil.Flag
     */
    public static int matchDataType(String data) {
        if (Pattern.matches("([a-zA-Z0-9_\\.\\-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})",data)){
            return StatusCodeUtil.EMAIL_FLAG;
        }
        if (Pattern.matches("^1[0-9][0-9]\\d{4,8}$", data)){
            return StatusCodeUtil.MOBILE_FLAG;
        }
        return -1;
    }

}
