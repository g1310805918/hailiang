package com.yunduan.controller;


import cn.hutool.core.util.IdUtil;
import com.yunduan.common.utils.CreateVerifyCode;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/common/captcha")
public class CaptchaController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(value = "初始化验证码")
    public Result<Object> initCaptcha() {
        String captchaId = IdUtil.simpleUUID();
        String code = new CreateVerifyCode().randomStr(4);
        // 缓存验证码
        redisTemplate.opsForValue().set(captchaId, code, 2L, TimeUnit.MINUTES);
        return new ResultUtil<Object>().setData(captchaId);
    }

    @RequestMapping(value = "/draw/{captchaId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据验证码ID获取图片")
    public void drawCaptcha(@PathVariable("captchaId") String captchaId, HttpServletResponse response) throws IOException {
        // 得到验证码 生成指定验证码
        String code = (String) redisTemplate.opsForValue().get(captchaId);
        CreateVerifyCode vCode = new CreateVerifyCode(116, 36, 4, 10, code);
        response.setContentType("image/png");
        vCode.write(response.getOutputStream());
    }
    

}
