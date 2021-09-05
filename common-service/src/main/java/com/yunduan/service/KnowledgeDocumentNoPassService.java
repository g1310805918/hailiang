package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentNoPass;
import com.yunduan.request.front.document.CreateDocumentReq;
import com.yunduan.vo.DocumentDetailVo;

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
}
