package com.yunduan.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Account;
import com.yunduan.entity.Engineer;
import com.yunduan.request.front.account.*;
import com.yunduan.service.*;
import com.yunduan.utils.*;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private SendEmailUtil sendEmailUtil;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private KnowledgeDocumentOneCategoryService oneCategoryService;
    @Autowired
    private KnowledgeDocumentTwoCategoryService twoCategoryService;
    @Autowired
    private KnowledgeDocumentThreeCategoryService threeCategoryService;

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
        if (!Objects.equals(account.getPassword(),AESUtil.encrypt(accountReq.getPassword()))) {
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
            String code = SendVerificationCodeUtil.send(sendVerificationCodeReq.getMobile());
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
            log.error("【检查手机号或邮箱以及图片验证码是否正确】手机号或邮箱格式错误！");
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
            String randomCode = sendEmailUtil.sendAuthEmail(sendEmailVerCodeReq.getEmail());
            redisUtil.setStringKeyValue(sendEmailVerCodeReq.getEmail(),randomCode,3,TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("【获取邮箱验证码】邮件发送失败 Message ----> " + e.getMessage());
            return resultUtil.AesFAILError("邮件发送失败");
        }
        return resultUtil.AesJSONSuccess("邮件发送成功","");
    }


    @PostMapping("/pic-upload")
    @ApiOperation(httpMethod = "POST",value = "图片上传")
    public ResultUtil<String> picUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("图片上传 file 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        String savePath = QNiuUtil.uploadMultipartFile(file);
        if (StrUtil.hasEmpty(savePath)) {
            return resultUtil.AesFAILError("上传失败");
        }
        return resultUtil.AesJSONSuccess("SUCCESS",savePath);
    }


    @PostMapping("/engineer-login")
    @ApiOperation(httpMethod = "POST",value = "工程师登录")
    public ResultUtil<Engineer> engineerLogin(AccountReq accountReq) {
        accountReq = AESUtil.decryptToObj(accountReq.getData(),AccountReq.class);
        //工程师账号
        Engineer engineer = engineerService.findByEmail(accountReq.getMobileOrEmail());
        if (!Objects.equals(engineer.getPassword(),AESUtil.encrypt(accountReq.getPassword()))) {
            return resultUtil.AesFAILError("您输入的账号或密码错误");
        }
        if (engineer.getAccountStatus() == StatusCodeUtil.ENGINEER_ACCOUNT_DISABLE_STATUS) {
            return resultUtil.AesFAILError("该账号已被冻结，请联系管理员！");
        }
        engineer = engineerService.engineerLoginUpdateToken(engineer);
        return engineer != null ? resultUtil.AesJSONSuccess("登录成功",engineer) : resultUtil.AesFAILError("登录失败");
    }


    @PostMapping("/engineer-change-password")
    @ApiOperation(httpMethod = "POST",value = "工程师修改密码")
    public ResultUtil<String> engineerChangePassword(SubmitEditPasswordReq submitEditPasswordReq) {
        submitEditPasswordReq = AESUtil.decryptToObj(submitEditPasswordReq.getData(),SubmitEditPasswordReq.class);
        Boolean flag = false;
        int type = MatchDataUtil.matchDataType(submitEditPasswordReq.getMobile());
        Engineer one = engineerService.getOne(new QueryWrapper<Engineer>().eq(type == 1, "mobile", submitEditPasswordReq.getMobile()).eq(type == 2, "email", submitEditPasswordReq.getMobile()));
        if (one != null) {
            one.setPassword(AESUtil.encrypt(submitEditPasswordReq.getAfterPassword()));
            flag = engineerService.updateById(one);
        }
        return flag ? resultUtil.AesJSONSuccess("修改密码成功","") : resultUtil.AesFAILError("修改密码失败");
    }


    @GetMapping("/get-one-categoty")
    @ApiOperation(httpMethod = "GET",value = "获取所有一级分类")
    public ResultUtil<List<KnowledgeOneCategoryVo>> getOneCategory() {
        //一级分类列表
        List<KnowledgeOneCategoryVo> oneCategoryList = oneCategoryService.getKnowledgeDocOneCategoryList();
        return resultUtil.AesJSONSuccess("SUCCESS",oneCategoryList);
    }


    @GetMapping("/get-two-category/{oneCategoryId}")
    @ApiOperation(httpMethod = "GET",value = "获取一级分类下的二级分类列表")
    public ResultUtil<List<KnowledgeOneCategoryVo>> getTwoCategory(@PathVariable("oneCategoryId") String oneCategoryId) {
        if (StrUtil.hasEmpty(oneCategoryId)) {
            log.error("获取一级分类下的二级分类列表【oneCategoryId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        List<KnowledgeOneCategoryVo> voList = twoCategoryService.queryTwoCategoryList(oneCategoryId);
        return resultUtil.AesJSONSuccess("SUCCESS",voList);
    }


    @GetMapping("/get-three-category/{twoCategoryId}")
    @ApiOperation(httpMethod = "GET",value = "获取二级分类下的三级分类列表")
    public ResultUtil<List<KnowledgeOneCategoryVo>> getThreeCategory(@PathVariable("twoCategoryId") String twoCategoryId) {
        if (StrUtil.hasEmpty(twoCategoryId)) {
            log.error("获取二级分类下的三级分类列表【twoCategoryId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        List<KnowledgeOneCategoryVo> voList = threeCategoryService.queryThreeCategory(twoCategoryId);
        return resultUtil.AesJSONSuccess("SUCCESS",voList);
    }









}
