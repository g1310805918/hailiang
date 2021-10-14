package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.AccountMsg;
import com.yunduan.request.front.message.EngineerInitPageReq;
import com.yunduan.request.front.message.InitListReq;
import com.yunduan.service.AccountMsgService;
import com.yunduan.service.EngineerMsgService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.vo.EngineerMsgDetailVo;
import com.yunduan.vo.EngineerMsgInitVo;
import com.yunduan.vo.MsgDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = {"系统、验证 消息接口"})
@RequestMapping("/api/msg")
public class MessageController {

    private static final transient Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private AccountMsgService accountMsgService;
    @Autowired
    private EngineerMsgService engineerMsgService;


    @GetMapping("/account-msg-record")
    @ApiOperation(httpMethod = "GET",value = "用户消息列表")
    public ResultUtil<Map<String, Object>> accountMsgRecord(InitListReq initListReq) {
        initListReq = AESUtil.decryptToObj(initListReq.getData(),InitListReq.class);
        //消息列表
        Map<String, Object> map = accountMsgService.accountMsgListVo(ContextUtil.getUserId(), initListReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/msg-detail/{id}")
    @ApiOperation(httpMethod = "GET",value = "消息详情")
    public ResultUtil<MsgDetailVo> msgDetail(@PathVariable("id") String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("消息详情 id 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        MsgDetailVo detailVo = accountMsgService.queryMsgById(id);
        return detailVo != null ? resultUtil.AesJSONSuccess("SUCCESS",detailVo) : resultUtil.AesFAILError("未知错误");
    }


    @PostMapping("/remove-msg/{id}")
    @ApiOperation(httpMethod = "POST",value = "删除消息")
    public ResultUtil<String> removeMsg(@PathVariable("id") String id){
        if (StrUtil.hasEmpty(id)) {
            log.error("删除消息 id 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        AccountMsg accountMsg = accountMsgService.getOne(new QueryWrapper<AccountMsg>().eq("id", id));
        if (accountMsg == null) {
            return resultUtil.AesFAILError("消息不存在");
        }
        boolean flag = accountMsgService.removeById(id);
        return flag ? resultUtil.AesJSONSuccess("删除成功","") : resultUtil.AesFAILError("删除失败");
    }


    @GetMapping("/engineer-init")
    @ApiOperation(httpMethod = "GET",value = "工程师端-消息列表初始化")
    public ResultUtil<List<EngineerMsgInitVo>> engineerInit(EngineerInitPageReq engineerInitPageReq) {
        engineerInitPageReq = AESUtil.decryptToObj(engineerInitPageReq.getData(),EngineerInitPageReq.class);
        List<EngineerMsgInitVo> voList = engineerMsgService.queryScreenMessageList(engineerInitPageReq);
        return resultUtil.AesJSONSuccess("SUCCESS",voList);
    }


    @GetMapping("/engineer-detail/{id}")
    @ApiOperation(httpMethod = "GET",value = "工程师端消息详情")
    public ResultUtil<EngineerMsgDetailVo> engineerDetail(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("工程师端消息详情【id】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        EngineerMsgDetailVo detailVo = engineerMsgService.queryMsgDetail(id);
        return detailVo != null ? resultUtil.AesJSONSuccess("SUCCESS",detailVo) : resultUtil.AesFAILError("非法请求");
    }


    @PostMapping("/engineer-remove-msg/{id}")
    @ApiOperation(httpMethod = "POST",value = "工程师删除消息")
    public ResultUtil<String> engineerRemoveMsg(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("工程师端消息详情【id】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        boolean flag = engineerMsgService.removeById(id);
        return flag ? resultUtil.AesJSONSuccess("删除成功","") : resultUtil.AesFAILError("删除失败");
    }

}
