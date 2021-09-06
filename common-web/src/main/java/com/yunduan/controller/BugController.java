package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.yunduan.entity.BugManager;
import com.yunduan.request.front.document.BugManagerInitPageReq;
import com.yunduan.request.front.review.PassReviewReq;
import com.yunduan.service.BugManagerService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.BudDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(tags = {"BUG反馈接口"})
@RequestMapping("/bug/feedback")
public class BugController {

    private static final transient Logger log = LoggerFactory.getLogger(BugController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private BugManagerService bugManagerService;


    @GetMapping("/base-info")
    @ApiOperation(httpMethod = "GET",value = "BUG审核管理页面统计")
    public ResultUtil<Map<String, Integer>> baseInfo() {
        Map<String, Integer> map = bugManagerService.queryBaseInfo();
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/init-page")
    @ApiOperation(httpMethod = "GET",value = "初始化页面")
    public ResultUtil<Map<String, Object>> initPage(BugManagerInitPageReq bugManagerInitPageReq) {
        bugManagerInitPageReq = AESUtil.decryptToObj(bugManagerInitPageReq.getData(),BugManagerInitPageReq.class);
        Map<String, Object> map = bugManagerService.queryInitPage(bugManagerInitPageReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/pass-review/{id}")
    @ApiOperation(httpMethod = "POST",value = "审核通过操作")
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
        }
        return flag ? resultUtil.AesJSONSuccess("操作成功","") : resultUtil.AesFAILError("操作失败");
    }


    @PostMapping("/refused-bug")
    @ApiOperation(httpMethod = "POST",value = "拒绝bug")
    public ResultUtil<String> refusedBug(PassReviewReq passReviewReq) {
        passReviewReq = AESUtil.decryptToObj(passReviewReq.getData(),PassReviewReq.class);
        boolean flag = false;
        BugManager bugManager = bugManagerService.getById(passReviewReq.getId());
        if (bugManager != null) {
            bugManager.setBugStatus(StatusCodeUtil.BUG_DOC_NO_PASS_REVIEW_STATUS).setRefusedReason(passReviewReq.getReason());
            flag = bugManagerService.updateById(bugManager);
        }
        return flag ? resultUtil.AesJSONSuccess("操作成功","") : resultUtil.AesFAILError("操作失败");
    }


    @GetMapping("/detail-info/{id}")
    @ApiOperation(httpMethod = "POST",value = "bug反馈详情")
    public ResultUtil<BudDetailVo> detailInfo(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("审核通过操作 id 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        BudDetailVo vo = bugManagerService.queryDetail(id);
        return resultUtil.AesJSONSuccess("SUCCESS",vo);
    }


}
