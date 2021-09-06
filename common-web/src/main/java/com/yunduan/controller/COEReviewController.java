package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.yunduan.request.front.review.ReviewInitReq;
import com.yunduan.service.KnowledgeDocumentNoPassService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.vo.DocumentDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(tags = {"COE文档审核管理接口"})
@RequestMapping("/document/review")
public class COEReviewController {

    private static final transient Logger log = LoggerFactory.getLogger(COEReviewController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private KnowledgeDocumentNoPassService knowledgeDocumentNoPassService;


    @GetMapping("/base-info")
    @ApiOperation(httpMethod = "GET",value = "顶部统计基本信息")
    public ResultUtil<Map<String, Integer>> baseInfo() {
        Map<String, Integer> map = knowledgeDocumentNoPassService.queryDocumentReviewStatistical();
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/init-page")
    @ApiOperation(httpMethod = "GET",value = "COE文档审核分页初始化")
    public ResultUtil<Map<String, Object>> initPage(ReviewInitReq reviewInitReq) {
        reviewInitReq = AESUtil.decryptToObj(reviewInitReq.getData(),ReviewInitReq.class);
        Map<String, Object> map = knowledgeDocumentNoPassService.queryReviewInitPage(reviewInitReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/pass-document/{documentId}")
    @ApiOperation(httpMethod = "POST",value = "通过审核文档")
    public ResultUtil<String> passDocument(@PathVariable String documentId) {
        if (StrUtil.hasEmpty(documentId)) {
            log.error("通过审核文档【documentId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = knowledgeDocumentNoPassService.changeDocumentStatus(documentId, 2);
        return row > 0 ? resultUtil.AesJSONSuccess("操作成功","") : resultUtil.AesFAILError("操作失败");
    }


    @PostMapping("/no-pass-document/{documentId}")
    @ApiOperation(httpMethod = "POST",value = "拒绝通过")
    public ResultUtil<String> noPassDocument(@PathVariable String documentId) {
        if (StrUtil.hasEmpty(documentId)) {
            log.error("拒绝通过【documentId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = knowledgeDocumentNoPassService.changeDocumentStatus(documentId, 3);
        return row > 0 ? resultUtil.AesJSONSuccess("操作成功","") : resultUtil.AesFAILError("操作失败");
    }


    @GetMapping("/document-detail/{documentId}")
    @ApiOperation(httpMethod = "GET",value = "审核文档详情")
    public ResultUtil<DocumentDetailVo> documentDetail(@PathVariable String documentId) {
        if (StrUtil.hasEmpty(documentId)) {
            log.error("审核文档详情【documentId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        DocumentDetailVo documentDetailVo = knowledgeDocumentNoPassService.documentDetail(documentId);
        return resultUtil.AesJSONSuccess("SUCCESS",documentDetailVo);
    }
    

}
