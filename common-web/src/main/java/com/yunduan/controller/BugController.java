package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.yunduan.entity.BugManager;
import com.yunduan.request.front.document.BugManagerInitPageReq;
import com.yunduan.request.front.document.CreateBugFeedbackReq;
import com.yunduan.request.front.document.EngineerBugInitPageReq;
import com.yunduan.request.front.review.PassReviewReq;
import com.yunduan.service.BugManagerService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.utils.SendMessageUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.BudDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(tags = {"BUG反馈/BUG反馈审核接口"})
@RequestMapping("/bug/feedback")
public class BugController {

    private static final transient Logger log = LoggerFactory.getLogger(BugController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private BugManagerService bugManagerService;
    @Autowired
    private SendMessageUtil sendMessageUtil;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;



    @GetMapping("/base-info")
    @ApiOperation(httpMethod = "GET",value = "BUG审核管理页面统计")
    public ResultUtil<Map<String, Integer>> baseInfo() {
        Map<String, Integer> map = bugManagerService.queryBaseInfo();
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/init-page")
    @ApiOperation(httpMethod = "GET",value = "BUG审核管理初始化页面")
    public ResultUtil<Map<String, Object>> initPage(BugManagerInitPageReq bugManagerInitPageReq) {
        bugManagerInitPageReq = AESUtil.decryptToObj(bugManagerInitPageReq.getData(),BugManagerInitPageReq.class);
        Map<String, Object> map = bugManagerService.queryInitPage(bugManagerInitPageReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/pass-review/{id}")
    @ApiOperation(httpMethod = "POST",value = "BUG审核管理审核通过bug操作")
    public ResultUtil<String> passReview(@PathVariable String id){
        if (StrUtil.hasEmpty(id)) {
            log.error("审核通过操作 id 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        boolean flag = false;
        BugManager bugManager = bugManagerService.getById(id);
        if (bugManager != null) {
            bugManager.setBugStatus(StatusCodeUtil.BUG_DOC_PASS_REVIEW_STATUS);
            flag = bugManagerService.updateById(bugManager);
            if (flag) {
                //BDE异步发送bug审核结果消息给工程师
                threadPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        sendMessageUtil.sendBUGReviewPassMessage(id);
                    }
                });
            }
        }
        return flag ? resultUtil.AesJSONSuccess("操作成功","") : resultUtil.AesFAILError("操作失败");
    }


    @PostMapping("/refused-bug")
    @ApiOperation(httpMethod = "POST",value = "BUG审核管理拒绝bug")
    public ResultUtil<String> refusedBug(PassReviewReq passReviewReq) {
        passReviewReq = AESUtil.decryptToObj(passReviewReq.getData(),PassReviewReq.class);
        boolean flag = false;
        BugManager bugManager = bugManagerService.getById(passReviewReq.getId());
        if (bugManager != null) {
            bugManager.setBugStatus(StatusCodeUtil.BUG_DOC_NO_PASS_REVIEW_STATUS).setRefusedReason(passReviewReq.getReason());
            flag = bugManagerService.updateById(bugManager);
            if (flag) {
                //BDE异步发送bug审核结果消息给工程师
                threadPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        sendMessageUtil.sendBUGReviewRefusedMessage(bugManager.getId().toString());
                    }
                });
            }
        }
        return flag ? resultUtil.AesJSONSuccess("操作成功","") : resultUtil.AesFAILError("操作失败");
    }


    @GetMapping("/detail-info/{id}")
    @ApiOperation(httpMethod = "POST",value = "BUG审核管理bug反馈详情")
    public ResultUtil<BudDetailVo> detailInfo(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("审核通过操作 id 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        BudDetailVo vo = bugManagerService.queryDetail(id);
        return resultUtil.AesJSONSuccess("SUCCESS",vo);
    }


    @GetMapping("/engineer-base-info")
    @ApiOperation(httpMethod = "GET",value = "工程师BUG反馈页面统计")
    public ResultUtil<Map<String, Integer>> engineerBaseInfo() {
        Map<String, Integer> map = bugManagerService.queryFeedbackBaseInfo();
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/engineer-init-page")
    @ApiOperation(httpMethod = "GET",value = "工程师Bug反馈列表初始化")
    public ResultUtil<Map<String, Object>> engineerInitPage(EngineerBugInitPageReq engineerBugInitPageReq) {
        engineerBugInitPageReq = AESUtil.decryptToObj(engineerBugInitPageReq.getData(),EngineerBugInitPageReq.class);
        Map<String, Object> map = bugManagerService.queryEngineerInitPage(engineerBugInitPageReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/engineer-add")
    @ApiOperation(httpMethod = "POST",value = "工程师添加bug反馈")
    public ResultUtil<String> engineerAdd(CreateBugFeedbackReq createBugFeedbackReq) {
        createBugFeedbackReq = AESUtil.decryptToObj(createBugFeedbackReq.getData(),CreateBugFeedbackReq.class);
        int row = bugManagerService.createBugFeedback(createBugFeedbackReq);
        return row > 0 ? resultUtil.AesJSONSuccess("反馈成功","") : resultUtil.AesFAILError("反馈失败");
    }


    @GetMapping("/engineer-refused-reason/{id}")
    @ApiOperation(httpMethod = "GET",value = "工程师查看BUG文档拒绝原因")
    public ResultUtil<String> engineerRefusedReason(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("工程师查看BUG文档拒绝原因【id】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        BugManager one = bugManagerService.getById(id);
        return resultUtil.AesJSONSuccess("SUCCESS",StrUtil.hasEmpty(one.getRefusedReason()) ? "" : one.getRefusedReason());
    }


    @GetMapping("engineer-tapd-feedback-result/{id}")
    @ApiOperation(httpMethod = "GET",value = "工程师查看tabd反馈结果")
    public ResultUtil<String> engineerTAPDResult(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("工程师查看tabd反馈结果【id】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        BugManager one = bugManagerService.getById(id);
        return resultUtil.AesJSONSuccess("SUCCESS",StrUtil.hasEmpty(one.getTabdFeedback()) ? "" : one.getTabdFeedback());
    }







}
