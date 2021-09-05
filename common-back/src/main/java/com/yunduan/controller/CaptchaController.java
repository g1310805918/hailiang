package com.yunduan.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.yunduan.common.utils.CreateVerifyCode;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/common")
@Api(tags = {"登录初始化接口"})
public class CaptchaController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/captcha/init", method = RequestMethod.GET)
    @ApiOperation(value = "初始化验证码")
    public Result<Object> initCaptcha() {
        String captchaId = IdUtil.simpleUUID();
        String code = new CreateVerifyCode().randomStr(4);
        // 缓存验证码
        redisTemplate.opsForValue().set(captchaId, code, 2L, TimeUnit.MINUTES);
        return new ResultUtil<Object>().setData(captchaId);
    }

    @RequestMapping(value = "/captcha/draw/{captchaId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据验证码ID获取图片")
    public void drawCaptcha(@PathVariable("captchaId") String captchaId, HttpServletResponse response) throws IOException {
        // 得到验证码 生成指定验证码
        String code = (String) redisTemplate.opsForValue().get(captchaId);
        CreateVerifyCode vCode = new CreateVerifyCode(116, 36, 4, 10, code);
        response.setContentType("image/png");
        vCode.write(response.getOutputStream());
    }


    @RequestMapping(value = "/needLogin", method = RequestMethod.GET)
    @ApiOperation(value = "没有登录")
    public Result<Object> needLogin() {
        return new ResultUtil<Object>().setErrorMsg(401, "您还未登录");
    }

    @RequestMapping(value = "/swagger/login", method = RequestMethod.GET)
    @ApiOperation(value = "Swagger接口文档专用登录接口 方便测试")
    public Result<Object> swaggerLogin(@RequestParam String username, @RequestParam String password,
                                       @ApiParam("验证码") @RequestParam(required = false) String code,
                                       @ApiParam("图片验证码ID") @RequestParam(required = false) String captchaId,
                                       @ApiParam("可自定义登录接口地址")
                                       @RequestParam(required = false, defaultValue = "http://127.0.0.1:8888/highset/login")
                                               String loginUrl) {

        Map<String, Object> params = new HashMap<>(16);
        params.put("username", username);
        params.put("password", password);
        params.put("code", code);
        params.put("captchaId", captchaId);
        String result = HttpUtil.post(loginUrl, params);
        return new ResultUtil<Object>().setData(result);
    }

}
