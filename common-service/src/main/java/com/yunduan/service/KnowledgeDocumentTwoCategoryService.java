package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentTwoCategory;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.KnowledgeTwoThreeCategoryVo;

import java.util.List;

public interface KnowledgeDocumentTwoCategoryService extends IService<KnowledgeDocumentTwoCategory> {


    /**
     * 查询一级分类下的二、三级分类
     * @param oneCategoryId 一级分类id
     * @return list
     */
    List<KnowledgeTwoThreeCategoryVo> queryKnowLedgeCategoryList(String oneCategoryId);


    /**
     * 获取一级分类下的二级列表
     * @param oneCategoryId 一级id
     * @return list
     */
    List<KnowledgeOneCategoryVo> queryTwoCategoryList(String oneCategoryId);
}
