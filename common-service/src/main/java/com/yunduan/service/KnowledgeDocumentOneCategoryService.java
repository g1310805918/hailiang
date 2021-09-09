package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.KnowledgeDocumentOneCategory;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.ProductNameVersionVo;

import java.util.List;
import java.util.Map;

public interface KnowledgeDocumentOneCategoryService extends IService<KnowledgeDocumentOneCategory> {


    /**
     * 获取知识文档库一级分类列表
     *
     * @return list
     */
    List<KnowledgeOneCategoryVo> getKnowledgeDocOneCategoryList();


    /**
     * 获取前两级分类
     *
     * @return list
     */
    List<ProductNameVersionVo> queryBeginOneTwoLevelCategoryList();


    /**
     * 分页初始化一级分类列表
     *
     * @param categoryName 分类名称
     * @param pageNo       页号
     * @param pageSize     页面大小
     * @return map
     */
    Map<String, Object> initOneCategoryData(String categoryName, Integer pageNo, Integer pageSize);


    /**
     * 添加分类
     * @param categoryName 分类名称
     * @param parentId 上级id
     * @param level 等级
     * @return int
     */
    int createOneLevel(String categoryName, String parentId, String level);
}
