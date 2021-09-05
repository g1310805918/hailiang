package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.yunduan.request.front.document.CreateDocumentReq;
import com.yunduan.request.front.document.InitDocumentManagerReq;
import com.yunduan.service.CollectionEngineerDocumentService;
import com.yunduan.service.KnowledgeDocumentNoPassService;
import com.yunduan.service.KnowledgeDocumentService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.vo.DocumentDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.Context;
import java.util.Map;

@RestController
@RequestMapping("/doc/manager")
@Api(tags = {"文档管理"})
public class DocManagerController {

    private static final transient Logger log = LoggerFactory.getLogger(DocManagerController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;
    @Autowired
    private CollectionEngineerDocumentService collectionEngineerDocumentService;
    @Autowired
    private KnowledgeDocumentNoPassService knowledgeDocumentNoPassService;


    @GetMapping("/init-page")
    @ApiOperation(httpMethod = "GET",value = "工程师-初始化我的文档列表")
    public ResultUtil<Map<String, Object>> initPage(InitDocumentManagerReq initDocumentManagerReq) {
        initDocumentManagerReq = AESUtil.decryptToObj(initDocumentManagerReq.getData(),InitDocumentManagerReq.class);
        Map<String, Object> map = knowledgeDocumentService.engineerInitPage(initDocumentManagerReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/engineer-create-collect/{documentId}/{favoritesId}")
    @ApiOperation(httpMethod = "POST",value = "工程师收藏或取消收藏文档【文档id、收藏夹id（可以为空）】")
    public ResultUtil<String> engineerCreateCollect(@PathVariable("documentId") String documentId,@PathVariable("favoritesId") String favoritesId) {
        if (StrUtil.hasEmpty(documentId)) {
            log.error("工程师收藏文档【documentId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = collectionEngineerDocumentService.engineerCollectionDocument(ContextUtil.getUserId().toString(), documentId, favoritesId);
        return row == 1 ? resultUtil.AesJSONSuccess("收藏成功","") : row == -1 ? resultUtil.AesFAILError("收藏失败") : row == 2 ? resultUtil.AesJSONSuccess("取消收藏成功","") : resultUtil.AesFAILError("取消收藏失败");
    }


    @GetMapping("/engineer-document-detail/{documentId}")
    @ApiOperation(httpMethod = "GET",value = "文档详情")
    public ResultUtil<DocumentDetailVo> engineerDocumentDetail(@PathVariable String documentId) {
        if (StrUtil.hasEmpty(documentId)) {
            log.error("文档详情【documentId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        DocumentDetailVo detailVo = knowledgeDocumentNoPassService.engineerDocumentDetail(ContextUtil.getUserId().toString(), documentId);
        return resultUtil.AesJSONSuccess("SUCCESS",detailVo);
    }


    @PostMapping("/engineer-remove-document/{documentId}")
    @ApiOperation(httpMethod = "POST",value = "工程师删除文档")
    public ResultUtil<String> engineerRemoveDocument(@PathVariable("documentId") String documentId) {
        if (StrUtil.hasEmpty(documentId)) {
            log.error("工程师删除文档【documentId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = knowledgeDocumentService.engineerRemoveDocument(documentId);
        return row > 0 ? resultUtil.AesJSONSuccess("删除成功","") : resultUtil.AesFAILError("删除失败");
    }


    @PostMapping("/engineer-create-document")
    @ApiOperation(httpMethod = "POST",value = "添加知识文档")
    public ResultUtil<String> engineerCreateDocument(CreateDocumentReq createDocumentReq) {
        createDocumentReq = AESUtil.decryptToObj(createDocumentReq.getData(),CreateDocumentReq.class);
        int row = knowledgeDocumentNoPassService.engineerCreateDocument(ContextUtil.getUserId().toString(), createDocumentReq);
        return row > 0 ? resultUtil.AesJSONSuccess("文档审核申请已提交","") : resultUtil.AesFAILError("提交审核失败");
    }



}
