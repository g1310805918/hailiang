package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.request.front.knowledge.KnowledgeListReq;
import com.yunduan.service.*;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.vo.KnowledgeDetailVo;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.KnowledgeTwoThreeCategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/knowledge")
@Api(tags = {"知识文档接口"})
public class KnowledgeController {

    private static final transient Logger log = LoggerFactory.getLogger(KnowledgeController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private KnowledgeDocumentOneCategoryService oneCategoryService;
    @Autowired
    private KnowledgeDocumentTwoCategoryService twoCategoryService;
    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;
    @Autowired
    private CollectionFavoritesService collectionFavoritesService;


    @GetMapping("/category-list")
    @ApiOperation(httpMethod = "GET",value = "知识文档一级分类")
    public ResultUtil<List<KnowledgeOneCategoryVo>> categoryList() {
        List<KnowledgeOneCategoryVo> oneCategoryList = oneCategoryService.getKnowledgeDocOneCategoryList();
        return resultUtil.AesJSONSuccess("SUCCESS",oneCategoryList);
    }


    @GetMapping("/two-three-category-list/{oneCategoryId}")
    @ApiOperation(httpMethod = "GET",value = "一级分类下的二三级分类")
    public ResultUtil<List<KnowledgeTwoThreeCategoryVo>> twoThreeCategoryList(@PathVariable("oneCategoryId") String oneCategoryId) {
        if (StrUtil.hasEmpty(oneCategoryId)) {
            log.error("一级分类下的二三级分类  oneCategoryId  为空");
            return resultUtil.AesFAILError("非法请求");
        }
        List<KnowledgeTwoThreeCategoryVo> voList = twoCategoryService.queryKnowLedgeCategoryList(oneCategoryId);
        return resultUtil.AesJSONSuccess("SUCCESS",voList);
    }


    @GetMapping("/three-category-knowledge-list")
    @ApiOperation(httpMethod = "GET",value = "三级分类下的知识文档列表")
    public ResultUtil<Map<String,Object>> threeCategoryKnowledgeList(KnowledgeListReq knowledgeListReq) {
        knowledgeListReq = AESUtil.decryptToObj(knowledgeListReq.getData(),KnowledgeListReq.class);
        Map<String, Object> map = knowledgeDocumentService.queryKnowledgeListVo(knowledgeListReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/detail-page-info/{knowledgeId}")
    @ApiOperation(httpMethod = "GET",value = "知识文档详情")
    public ResultUtil<KnowledgeDetailVo> detailPageInfo(@PathVariable("knowledgeId") String knowledgeId) {
        if (StrUtil.hasEmpty(knowledgeId)) {
            log.error("知识文档详情 knowledgeId 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        KnowledgeDetailVo vo = knowledgeDocumentService.queryDocDetailInfo(knowledgeId);
        if (vo == null) {
            return resultUtil.AesFAILError("文档不存在！");
        }
        return resultUtil.AesJSONSuccess("SUCCESS",vo);
    }


    @GetMapping("/favorites-list/{documentId}")
    @ApiOperation(httpMethod = "GET",value = "用户收藏夹列表")
    public ResultUtil<List<CollectionFavorites>> favoritesList(@PathVariable("documentId") String documentId) {
        List<CollectionFavorites> favorites = collectionFavoritesService.queryFavoritesCollectionCount(ContextUtil.getUserId().toString(),documentId);
        return resultUtil.AesJSONSuccess("SUCCESS",favorites);
    }





}
