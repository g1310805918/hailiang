package com.yunduan.controller;

import cn.hutool.core.util.StrUtil;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.TheText;
import com.yunduan.service.TheTextService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(tags = {"文本协议接口"})
@RequestMapping("/the-text")
public class TheTextController {

    private static final transient Logger log = LoggerFactory.getLogger(TheTextController.class);

    @Autowired
    private TheTextService theTextService;


    @RequestMapping(value = "/agreement-info/{id}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "根据id获取协议内容")
    public Result<TheText> agreementInfo(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("根据id获取协议内容【id】为空");
            return ResultUtil.error("非法请求");
        }
        TheText theText = theTextService.getById(id);
        return ResultUtil.data(theText);
    }


    @RequestMapping(value = "/submit-edit",method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST",value = "提交修改")
    public Result<String> submitEdit(String id,String content) {
        if (StrUtil.hasEmpty(id) || StrUtil.hasEmpty(content)) {
            log.error("提交修改文本协议【id、content】为空");
            return ResultUtil.error("非法请求");
        }
        TheText text = theTextService.getById(id);
        if (text != null) {
            text.setContent(content);
            theTextService.updateById(text);
        }
        return ResultUtil.success("修改成功");
    }

}
