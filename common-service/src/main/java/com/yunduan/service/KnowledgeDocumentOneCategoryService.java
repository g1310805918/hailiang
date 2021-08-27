package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentOneCategory;
import com.yunduan.vo.KnowledgeOneCategoryVo;

import java.util.List;

public interface KnowledgeDocumentOneCategoryService extends IService<KnowledgeDocumentOneCategory> {


    /**
     * 获取知识文档库一级分类列表
     * @return list
     */
    List<KnowledgeOneCategoryVo> getKnowledgeDocOneCategoryList();
}
