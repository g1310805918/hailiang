package com.yunduan.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;

/**
 * 2021/8/12
 * 阿里云发送短信验证码
 * 工具类
 */
public class SendVerificationCodeUtil {

    //访问的域名
    static final String DOMAIN = "dysmsapi.aliyuncs.com";
    // 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String ACCESS_KEY_ID = "LTAI5tPTrKNqCGKjW3poWTV1";
    static final String ACCESS_KEY_SECRET = "NvGw6iWG96jH9KT2fUNdHjg7jIuOry";
    //短信签名-可在短信控制台中找到
    static final String SIGN_NAME = "华度新星";
    //短信模板-可在短信控制台中找到
    static final String TEMPLATE_CODE = "SMS_221805026";


    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId ACCESS_KEY_ID
     * @param accessKeySecret ACCESS_KEY_SECRET
     * @return Client
     */
    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = DOMAIN;
        return new Client(config);
    }


    /**
     * 【新版】发送验证码
     * @param mobile 手机号
     * @return 【验证码】
     * @throws Exception 异常
     */
    public static String sendSms(String mobile) throws Exception {
        Client client = createClient(ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        //生成验证码
        String numbers = RandomUtil.randomNumbers(6);
        SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName(SIGN_NAME)
                .setTemplateCode(TEMPLATE_CODE)
                .setTemplateParam("{\"code\":\"" + numbers + "\"}");
        SendSmsResponse response = client.sendSms(request);
        String responseJson = new Gson().toJson(response.body);
        JSONObject jsonObject = JSONObject.parseObject(responseJson);
        if ("OK".equalsIgnoreCase(jsonObject.getString("code")) && "OK".equalsIgnoreCase(jsonObject.getString("message"))){
            return numbers;
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        sendSms("15235656200");
    }

}
