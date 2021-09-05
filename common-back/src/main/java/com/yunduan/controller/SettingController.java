package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.Setting;
import com.yunduan.service.SettingService;
import com.yunduan.utils.AESUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setting")
@Api(tags = {"系统设置接口"})
public class SettingController {

    private static final transient Logger log = LoggerFactory.getLogger(SettingController.class);

    @Autowired
    private SettingService settingService;


    @GetMapping("/email/init")
    @ApiOperation(httpMethod = "GET",value = "初始化邮箱配置信息")
    public Result<Setting> emailInit() {
        Setting setting = settingService.getOne(new QueryWrapper<Setting>().eq("tem_code", "腾讯邮箱"));
        setting.setEmailPassword("**********");
        return ResultUtil.data(setting);
    }


    @GetMapping("/seeSecret/{settingName}")
    @ApiOperation(httpMethod = "GET",value = "查看邮箱密码")
    public Result<String> seeSecretSettingName(@PathVariable String settingName) {
        if (StrUtil.hasEmpty(settingName)) {
            log.error("查看邮箱密码【settingName】为空");
            return ResultUtil.error("非法请求");
        }
        Setting setting = settingService.getOne(new QueryWrapper<Setting>().eq("tem_code", "腾讯邮箱"));
        return setting != null ? ResultUtil.data(AESUtil.decrypt(setting.getEmailPassword())) : ResultUtil.error("未知错误");
    }


}
