package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentThreeCategory;

public interface KnowledgeDocumentThreeCategoryService extends IService<KnowledgeDocumentThreeCategory> {


    /**
     * 获取分类名称
     * @param threeCategoryId 三级分类id
     * @return 分类名称
     */
    String getKnowledgeCategoryName(String threeCategoryId);
}
