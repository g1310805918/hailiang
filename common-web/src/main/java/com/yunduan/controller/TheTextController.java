package com.yunduan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.TheText;
import com.yunduan.service.TheTextService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/th-txt")
@Api(value = "协议、文本控制器",tags = {"协议文本接口"})
public class TheTextController {

    @Autowired
    private TheTextService theTextService;


    @GetMapping("/privacy-policy")
    @ApiOperation(httpMethod = "GET",value = "隐私政策")
    public String toPrivacyPolicyPage(HttpServletRequest request) {
        TheText text = theTextService.getOne(new QueryWrapper<TheText>().eq("id", "442841936986902528"));
        if (text != null) {
            request.setAttribute("title",text.getCodeTitle());
            request.setAttribute("content",text.getContent());
            return "PrivacyPolicy";
        }
        return "";
    }


    @GetMapping("/agreement")
    @ApiOperation(httpMethod = "GET",value = "海量数据服务协议")
    public String toAgreementPage(HttpServletRequest request) {
        TheText text = theTextService.getOne(new QueryWrapper<TheText>().eq("id", "442841936986902529"));
        if (text != null) {
            request.setAttribute("title",text.getCodeTitle());
            request.setAttribute("content",text.getContent());
            return "Agreement";
        }
        return "";
    }


}
