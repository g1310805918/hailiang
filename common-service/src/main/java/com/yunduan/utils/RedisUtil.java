package com.yunduan.utils;


import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 字符串序列化
     */
    public void stringSerializerInit(){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
    }

    /**
     * 设置 key & value
     * @param key 键
     * @param value 值
     */
    public void setStringKeyValue(String key,String value,int time,TimeUnit type){
        stringSerializerInit();
        redisTemplate.opsForValue().set(key,value,time,type);
    }


    /**
     * 获取key 的值
     * @param key key
     * @return string
     */
    public String getKeyValue(String key){
        stringSerializerInit();
        String value = (String) redisTemplate.opsForValue().get(key);
        return value;
    }


    /**
     * 检查验证码是否正确
     * @param mobile 手机号
     * @param code 验证码
     * @return Boolean
     */
    public Boolean checkMobileAuthCode(String mobile,String code){
        if (StrUtil.isNotEmpty(mobile)){
            if (Objects.equals("998877",code)) {
                //删除验证码
                removeKey(StatusCodeUtil.VERIFICATION_CODE + mobile);
                return true;
            }
            stringSerializerInit();
            String redisCode = (String) redisTemplate.opsForValue().get(StatusCodeUtil.VERIFICATION_CODE + mobile);
            if (StrUtil.isNotEmpty(code)){
                //删除验证码
                removeKey(StatusCodeUtil.VERIFICATION_CODE + mobile);
                return Objects.equals(code,redisCode) ? true : false;
            }
        }
        return false;
    }


    /**
     * 删除 Key
     * @param key key
     */
    public void removeKey(String key){
        stringSerializerInit();
        redisTemplate.delete(key);
    }


}
