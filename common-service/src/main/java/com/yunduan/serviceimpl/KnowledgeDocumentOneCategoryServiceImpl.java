package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.KnowledgeDocumentOneCategory;
import com.yunduan.entity.KnowledgeDocumentTwoCategory;
import com.yunduan.mapper.KnowledgeDocumentOneCategoryMapper;
import com.yunduan.mapper.KnowledgeDocumentTwoCategoryMapper;
import com.yunduan.service.KnowledgeDocumentOneCategoryService;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.ProductNameVersionTwoVo;
import com.yunduan.vo.ProductNameVersionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class KnowledgeDocumentOneCategoryServiceImpl extends ServiceImpl<KnowledgeDocumentOneCategoryMapper, KnowledgeDocumentOneCategory> implements KnowledgeDocumentOneCategoryService {

    @Autowired
    private KnowledgeDocumentOneCategoryMapper knowledgeDocumentOneCategoryMapper;
    @Autowired
    private KnowledgeDocumentTwoCategoryMapper knowledgeDocumentTwoCategoryMapper;


    /**
     * 获取知识文档库一级分类列表
     * @return list
     */
    @Override
    public List<KnowledgeOneCategoryVo> getKnowledgeDocOneCategoryList() {
        List<KnowledgeOneCategoryVo> voList = new ArrayList<>();
        //所有1级分类列表
        List<KnowledgeDocumentOneCategory> oneCategories = knowledgeDocumentOneCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentOneCategory>().orderByDesc("id"));
        if (oneCategories.size() > 0 && oneCategories != null) {
            KnowledgeOneCategoryVo vo = null;
            for (KnowledgeDocumentOneCategory oneCategory : oneCategories) {
                vo = new KnowledgeOneCategoryVo();
                vo.setId(oneCategory.getId().toString()).setTitle(oneCategory.getCategoryTitle());
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 获取前两级分类
     * @return list
     */
    @Override
    public List<ProductNameVersionVo> queryBeginOneTwoLevelCategoryList() {
        List<ProductNameVersionVo> voList = new ArrayList<>();
        //所有一级分类列表
        List<KnowledgeDocumentOneCategory> oneCategories = knowledgeDocumentOneCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentOneCategory>().orderByDesc("id"));
        if (oneCategories.size() > 0 && oneCategories != null) {
            ProductNameVersionVo oneVo = null;
            for (KnowledgeDocumentOneCategory oneCategory : oneCategories) {
                oneVo = new ProductNameVersionVo();
                oneVo.setOneCategoryName(oneCategory.getCategoryTitle());
                //二级分类结果封装
                List<ProductNameVersionTwoVo> twoVoList = new ArrayList<>();
                //二级分类列表
                List<KnowledgeDocumentTwoCategory> twoCategories = knowledgeDocumentTwoCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentTwoCategory>().eq("one_category_id", oneCategory.getId()));
                if (twoCategories.size() > 0 && twoCategories != null) {
                    ProductNameVersionTwoVo twoVo = null;
                    for (KnowledgeDocumentTwoCategory twoCategory : twoCategories) {
                        twoVo = new ProductNameVersionTwoVo();
                        twoVo.setTwoCategoryId(twoCategory.getId().toString()).setTwoCategoryName(twoCategory.getCategoryTitle());
                        twoVoList.add(twoVo);
                    }
                }
                //设置一级分类下的二级分类
                oneVo.setTwoCategoryList(twoVoList);
                voList.add(oneVo);
            }
        }
        return voList;
    }


}
