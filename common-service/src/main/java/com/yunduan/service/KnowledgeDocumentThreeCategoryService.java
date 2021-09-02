package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentThreeCategory;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.ProductNameVersionThreeVo;

import java.util.List;

public interface KnowledgeDocumentThreeCategoryService extends IService<KnowledgeDocumentThreeCategory> {


    /**
     * 获取分类名称
     * @param threeCategoryId 三级分类id
     * @return 分类名称
     */
    String getKnowledgeCategoryName(String threeCategoryId);


    /**
     * 根据二级分类id查询下级三级分类列表
     * @param twoCategoryId 二级分类id
     * @return list
     */
    List<ProductNameVersionThreeVo> queryThreeLevelCategoryVo(String twoCategoryId);


    /**
     * 查询二级分类下的三级分类id
     * @param twoCategoryId 二级分类id
     * @return list
     */
    List<KnowledgeOneCategoryVo> queryThreeCategory(String twoCategoryId);
}
