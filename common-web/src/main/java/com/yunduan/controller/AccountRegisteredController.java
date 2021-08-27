package com.yunduan.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Account;
import com.yunduan.request.front.account.*;
import com.yunduan.service.AccountService;
import com.yunduan.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/login")
@Api(tags = {"登录注册接口"})
public class AccountRegisteredController {

    //日志门面
    private static final transient Logger log = LoggerFactory.getLogger(AccountRegisteredController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SendEmailUtils sendEmailUtils;
    @Autowired
    private AccountService accountService;


    @PostMapping("/registered-account")
    @ApiOperation(httpMethod = "POST",value = "注册用户")
    public ResultUtil<String> registeredAccount(RegisteredReq registeredReq) {
        registeredReq = AESUtil.decryptToObj(registeredReq.getData(),RegisteredReq.class);
        //校验验证码
        Boolean flag = redisUtil.checkMobileAuthCode(registeredReq.getMobile(), registeredReq.getYzm());
        if (!flag) {
            log.error("注册用户【验证码错误】");
            return resultUtil.AesFAILError("验证码错误，请重新获取！");
        }
        int row = accountService.registeredAccount(registeredReq);
        if (row == StatusCodeUtil.HAS_EXIST) {
            log.error("注册用户【" + registeredReq.getMobile() + "~" + registeredReq.getEmail() + "】 ---->  账号已存在");
            return resultUtil.AesFAILError("改账号已存在");
        }
        log.error("测试log");
        return row > 0 ? resultUtil.AesJSONSuccess("注册成功","") : resultUtil.AesFAILError("注册失败");
    }


    @PostMapping("/account-login")
    @ApiOperation(httpMethod = "POST",value = "登录接口")
    public ResultUtil<Map<String,String>> accountLogin(AccountReq accountReq) {
        accountReq = AESUtil.decryptToObj(accountReq.getData(),AccountReq.class);
        Account account = accountService.accountLogin(accountReq);
        if (account == null) {
            return resultUtil.AesFAILError("您输入的用户不存在");
        }
        if (!Objects.equals(AESUtil.encrypt(account.getPassword()),accountReq.getPassword())) {
            return resultUtil.AesFAILError("你输入的账号或者密码错误");
        }
        Map<String, String> map = accountService.changeUserTokenForLogin(account.getId());
        return map != null ? resultUtil.AesJSONSuccess("登录成功",map) : resultUtil.AesFAILError("登录失败");
    }


    @GetMapping("/send-verification-code")
    @ApiOperation(httpMethod = "GET",value = "发送短信验证码")
    public ResultUtil<String> sendVerificationCode(SendVerificationCodeReq sendVerificationCodeReq) {
        sendVerificationCodeReq = AESUtil.decryptToObj(sendVerificationCodeReq.getData(),SendVerificationCodeReq.class);
        if (MatchDataUtil.matchDataType(sendVerificationCodeReq.getMobile()) != 1) {
            log.error("【验证码】非法请求，请检查手机号是否正确！");
            return resultUtil.AesFAILError("非法请求，请检查手机号是否正确！");
        }
        try {
            String code = SendVerificationCodeUtil.sendSms(sendVerificationCodeReq.getMobile());
            //验证码3分钟有效
            redisUtil.setStringKeyValue(StatusCodeUtil.VERIFICATION_CODE + sendVerificationCodeReq.getMobile(),code,3, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("【验证码】发送短信验证码失败 ---> message = " + e.getMessage());
            return resultUtil.AesFAILError("发送失败");
        }
        return resultUtil.AesJSONSuccess("验证码发送成功","");
    }


    @GetMapping(value = "/init")
    @ApiOperation(httpMethod = "GET",value = "初始化图片验证码")
    public ResultUtil<String> initCaptcha() {
        String captchaId = IdUtil.simpleUUID();
        String code = new CaptchaGeneratorUtil().randomStr(4);
        // 缓存验证码
        redisUtil.setStringKeyValue(captchaId,code,2,TimeUnit.MINUTES);
        return resultUtil.AesJSONSuccess("SUCCESS",captchaId);
    }


    @GetMapping(value = "/draw/{captchaId}")
    @ApiOperation(httpMethod = "GET",value = "根据验证码ID获取图片")
    public void drawCaptcha(@PathVariable("captchaId") String captchaId,
                            HttpServletResponse response) throws IOException {
        // 得到验证码 生成指定验证码
        String code = redisUtil.getKeyValue(captchaId);
        CaptchaGeneratorUtil vCode = new CaptchaGeneratorUtil(116, 36, 4, 10, code);
        response.setContentType("image/png");
        vCode.write(response.getOutputStream());
    }


    @PostMapping("/check-mobil-draw")
    @ApiOperation(httpMethod = "GET",value = "检查手机号或邮箱以及图片验证码是否正确")
    public ResultUtil<String> checkMobileDraw(SecurityCheckReq securityCheckReq) {
        securityCheckReq = AESUtil.decryptToObj(securityCheckReq.getData(),SecurityCheckReq.class);
        int type = MatchDataUtil.matchDataType(securityCheckReq.getMobile());
        if (type == -1) {
            return resultUtil.AesFAILError("非法请求");
        }
        Account account = accountService.getOne(new QueryWrapper<Account>()
                .eq(type == 1,"mobile", securityCheckReq.getMobile())
                .eq(type == 2,"email",securityCheckReq.getMobile()));
        if (account == null) {
            return resultUtil.AesFAILError("用户不存在！");
        }
        //图形验证码
        String codeValue = redisUtil.getKeyValue(securityCheckReq.getCaptchaId());
        if (StrUtil.hasEmpty(codeValue)) {
            return resultUtil.AesFAILError("验证码已过期，请重新获取！");
        }
        return Objects.equals(securityCheckReq.getCaptchaCode(),codeValue) ? resultUtil.AesJSONSuccess("SUCCESS","") : resultUtil.AesFAILError("验证码错误！");
    }


    @PostMapping("/check-mobile-verification-code")
    @ApiOperation(httpMethod = "POST",value = "检查手机号/邮箱 和 短信验证码是否正确")
    public ResultUtil<String> checkMobileVerificationCode(SecurityCheckMobileVerificationCodeReq securityCheckMobileVerificationCodeReq) {
        securityCheckMobileVerificationCodeReq = AESUtil.decryptToObj(securityCheckMobileVerificationCodeReq.getData(),SecurityCheckMobileVerificationCodeReq.class);
        //判断前端传递类型 【是否是手机、或者 邮箱】
        if (MatchDataUtil.matchDataType(securityCheckMobileVerificationCodeReq.getMobile()) == 1) {
            //手机类型
            Boolean flag = redisUtil.checkMobileAuthCode(securityCheckMobileVerificationCodeReq.getMobile(), securityCheckMobileVerificationCodeReq.getYzm());
            return flag ? resultUtil.AesJSONSuccess("SUCCESS","") : resultUtil.AesFAILError("验证码错误！");
        }else {
            //用户传递的验证码
            String codeValue = redisUtil.getKeyValue(securityCheckMobileVerificationCodeReq.getMobile());
            //redis中该邮箱的验证码
            String redisEmailCode = redisUtil.getKeyValue(securityCheckMobileVerificationCodeReq.getMobile());
            return Objects.equals(codeValue,redisEmailCode) ? resultUtil.AesJSONSuccess("SUCCESS","") : resultUtil.AesFAILError("验证码错误");
        }
    }



    @PostMapping("/edit-account-password")
    @ApiOperation(httpMethod = "POST",value = "手机号找回提交修改密码")
    public ResultUtil<String> editAccountPassword(SubmitEditPasswordReq submitEditPasswordReq) {
        submitEditPasswordReq = AESUtil.decryptToObj(submitEditPasswordReq.getData(),SubmitEditPasswordReq.class);
        Boolean flag = false;
        int type = MatchDataUtil.matchDataType(submitEditPasswordReq.getMobile());
        if (type == -1) {
            return resultUtil.AesFAILError("非法请求");
        }
        Account account = accountService.getOne(
                new QueryWrapper<Account>()
                        .eq(type == 1,"mobile", submitEditPasswordReq.getMobile())
                        .eq(type == 2,"email",submitEditPasswordReq.getMobile()));
        if (account != null) {
            account.setPassword(AESUtil.encrypt(submitEditPasswordReq.getAfterPassword()));
            flag = accountService.update(account, new QueryWrapper<Account>().eq("id", account.getId()));
        }
        return flag ? resultUtil.AesJSONSuccess("密码修改成功","") : resultUtil.AesFAILError("密码修改失败");
    }



    @GetMapping("/send-reset-password-email")
    @ApiOperation(httpMethod = "GET",value = "使用邮箱获取验证码")
    public ResultUtil<String> sendResetPasswordEmail(SendEmailVerCodeReq sendEmailVerCodeReq) {
        sendEmailVerCodeReq = AESUtil.decryptToObj(sendEmailVerCodeReq.getData(),SendEmailVerCodeReq.class);
        if (MatchDataUtil.matchDataType(sendEmailVerCodeReq.getEmail()) != 2){
            return resultUtil.AesFAILError("非法请求，请检查邮箱是否正确？");
        }
        try {
            sendEmailUtils.send(sendEmailVerCodeReq.getEmail());
        } catch (Exception e) {
            log.error("【获取邮箱验证码】邮件发送失败 Message ----> " + e.getMessage());
            return resultUtil.AesFAILError("邮件发送失败");
        }
        return resultUtil.AesJSONSuccess("邮件发送成功","");
    }

}
