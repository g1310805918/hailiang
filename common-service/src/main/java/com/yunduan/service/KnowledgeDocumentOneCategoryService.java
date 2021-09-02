package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentOneCategory;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.ProductNameVersionVo;

import java.util.List;

public interface KnowledgeDocumentOneCategoryService extends IService<KnowledgeDocumentOneCategory> {


    /**
     * 获取知识文档库一级分类列表
     * @return list
     */
    List<KnowledgeOneCategoryVo> getKnowledgeDocOneCategoryList();


    /**
     * 获取前两级分类
     * @return list
     */
    List<ProductNameVersionVo> queryBeginOneTwoLevelCategoryList();
}
