package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocument;
import com.yunduan.request.front.knowledge.KnowledgeListReq;
import com.yunduan.vo.KnowledgeDetailVo;

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
}
