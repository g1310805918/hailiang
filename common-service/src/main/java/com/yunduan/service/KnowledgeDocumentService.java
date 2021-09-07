package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocument;
import com.yunduan.request.front.document.InitDocumentManagerReq;
import com.yunduan.request.front.knowledge.KnowledgeListReq;
import com.yunduan.request.front.knowledge.KnowledgeSearchReq;
import com.yunduan.request.front.servicerequest.DynamicSearchDocumentReq;
import com.yunduan.vo.DocumentListVo;
import com.yunduan.vo.KnowledgeDetailVo;
import com.yunduan.vo.KnowledgeLazySearchVo;
import com.yunduan.vo.KnowledgeLevel3CategoryList;

import java.util.List;
import java.util.Map;

public interface KnowledgeDocumentService extends IService<KnowledgeDocument> {


    /**
     * 查询三级分类下的知识文档
     * @param threeCategoryId 三级分类id
     * @param docType 文档类型
     * @return map
     */
    Map<String,Object> queryKnowledgeListVo(KnowledgeListReq knowledgeListReq);


    /**
     * 知识文档详情
     * @param id 文档id
     * @return KnowledgeDetailVo
     */
    KnowledgeDetailVo queryDocDetailInfo(String id);


    /**
     * 模糊搜索知识文档
     * @param searchContent 搜索内容
     * @param nullStr 搜索标志
     * @return list
     */
    List<KnowledgeLazySearchVo> queryKnowledgeLazySearch(String searchContent,String nullStr);


    /**
     * 模糊搜索知识文档列表数据
     * @param knowledgeSearchReq 搜索对象
     * @return map
     */
    Map<String,Object> queryKnowledgeResult(KnowledgeSearchReq knowledgeSearchReq);


    /**
     * 搜索内容所属文档分类
     * @param searchContent 搜索内容
     * @return list
     */
    List<KnowledgeLevel3CategoryList> querySearchContentInCategoryList(String searchContent);


    /**
     * 工程师初始化文档管理页面
     * @param initDocumentManagerReq 初始化对象
     * @return map
     */
    Map<String,Object> engineerInitPage(InitDocumentManagerReq initDocumentManagerReq);


    /**
     * 工程师删除知识文档
     * @param documentId 文档id
     * @return int
     */
    int engineerRemoveDocument(String documentId);


    /**
     * 动态搜索文档列表
     * @param dynamicSearchDocumentReq 搜索条件
     * @return list
     */
    List<DocumentListVo> dynamicDocumentList(DynamicSearchDocumentReq dynamicSearchDocumentReq);
}
