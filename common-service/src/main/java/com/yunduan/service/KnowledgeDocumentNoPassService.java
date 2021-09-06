package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentNoPass;
import com.yunduan.request.front.document.CreateDocumentReq;
import com.yunduan.request.front.review.ReviewInitReq;
import com.yunduan.vo.DocumentDetailVo;

import java.util.Map;

public interface KnowledgeDocumentNoPassService extends IService<KnowledgeDocumentNoPass> {


    /**
     * 知识文档详情
     * @param engineerId 工程师id
     * @param documentId 文档id
     * @return DocumentDetailVo
     */
    DocumentDetailVo engineerDocumentDetail(String engineerId,String documentId);


    /**
     * 工程师发布知识文档
     * @param engineerId 工程师id
     * @param createDocumentReq 添加对象
     * @return int
     */
    int engineerCreateDocument(String engineerId,CreateDocumentReq createDocumentReq);


    /**
     * COE文档审核页面初始化
     * @return map
     */
    Map<String,Integer> queryDocumentReviewStatistical();


    /**
     * COE文档审核分页初始化
     * @param reviewInitReq 初始化对象
     * @return map
     */
    Map<String,Object> queryReviewInitPage(ReviewInitReq reviewInitReq);


    /**
     * COE管理员审核文档通过或者拒绝操作
     * @param documentId 文档id
     * @param docStatus 文档状态
     * @return int
     */
    int changeDocumentStatus(String documentId,Integer docStatus);


    /**
     * 文档详情
     * @param documentId 文档id
     * @return DocumentDetailVo
     */
    DocumentDetailVo documentDetail(String documentId);
}
