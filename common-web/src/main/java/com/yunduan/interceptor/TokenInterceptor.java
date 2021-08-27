package com.yunduan.interceptor;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.utils.StatusCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisTemplate redisTemplate;

    public static final String FRONT_TOKEN = "Token";

    //设备1.安卓2.苹果3.小程序4.pc
    protected static final String Device = "Device";

    //版本号
    protected static final String Banben = "Banben";

    //时间戳
    protected static final String Timestamp = "Timestamp";

    //设备id
    protected static final String Deviceid = "Deviceid";


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String uri = httpServletRequest.getRequestURI();
        String frontAccountToken = httpServletRequest.getHeader(FRONT_TOKEN);
        if (StrUtil.hasEmpty(frontAccountToken)){
            responseData(httpServletResponse);
            log.error(uri + ":token失效请重新登陆");
            return false;
        }
        if ("yun".equals(frontAccountToken)){
            ContextUtil.removeUserId();
            return true;
        }
        String userId = (String) redisTemplate.opsForValue().get(StatusCodeUtil.ACCOUNT_TOKEN + frontAccountToken);
        if (StrUtil.hasEmpty(userId)){
            responseData(httpServletResponse);
            log.error(uri + ":token失效请重新登陆");
            return false;
        }

        if (StrUtil.isNotEmpty(userId)) {
            ContextUtil.setUserId(Convert.toLong(userId));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }


    private void responseData(HttpServletResponse response) throws IOException {
        ContextUtil.removeUserId();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONObject.toJSONString(new ResultUtil(StatusCodeUtil.TOKEN_FAILURE,"登录失效",null)));
    }

}

