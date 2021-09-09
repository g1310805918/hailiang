package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.KnowledgeDocumentOneCategory;
import com.yunduan.entity.KnowledgeDocumentThreeCategory;
import com.yunduan.entity.KnowledgeDocumentTwoCategory;
import com.yunduan.mapper.KnowledgeDocumentOneCategoryMapper;
import com.yunduan.mapper.KnowledgeDocumentThreeCategoryMapper;
import com.yunduan.mapper.KnowledgeDocumentTwoCategoryMapper;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.ProductNameVersionThreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class KnowledgeDocumentThreeCategoryServiceImpl extends ServiceImpl<KnowledgeDocumentThreeCategoryMapper, KnowledgeDocumentThreeCategory> implements KnowledgeDocumentThreeCategoryService {

    @Autowired
    private KnowledgeDocumentThreeCategoryMapper knowledgeDocumentThreeCategoryMapper;
    @Autowired
    private KnowledgeDocumentTwoCategoryMapper knowledgeDocumentTwoCategoryMapper;
    @Autowired
    private KnowledgeDocumentOneCategoryMapper knowledgeDocumentOneCategoryMapper;


    /**
     * 获取分类名称
     * @param threeCategoryId 三级分类id
     * @return 分类名称
     */
    @Override
    public String getKnowledgeCategoryName(String threeCategoryId) {
        String resultCategoryName = "";
        KnowledgeDocumentThreeCategory three = knowledgeDocumentThreeCategoryMapper.selectOne(new QueryWrapper<KnowledgeDocumentThreeCategory>().eq("id", threeCategoryId));
        if (three != null) {
            KnowledgeDocumentTwoCategory two = knowledgeDocumentTwoCategoryMapper.selectOne(new QueryWrapper<KnowledgeDocumentTwoCategory>().eq("id", three.getTwoCategoryId()));
            if (two != null) {
                KnowledgeDocumentOneCategory one = knowledgeDocumentOneCategoryMapper.selectOne(new QueryWrapper<KnowledgeDocumentOneCategory>().eq("id", two.getOneCategoryId()));
                if (one != null) {
                    resultCategoryName += one.getCategoryTitle() + " > " + two.getCategoryTitle() + " > " + three.getCategoryTitle();
                }
            }
        }
        return resultCategoryName;
    }


    /**
     * 根据二级分类id查询下级三级分类列表
     * @param twoCategoryId 二级分类id
     * @return list
     */
    @Override
    public List<ProductNameVersionThreeVo> queryThreeLevelCategoryVo(String twoCategoryId) {
        List<ProductNameVersionThreeVo> voList = new ArrayList<>();
        //三级分类列表
        List<KnowledgeDocumentThreeCategory> threeCategories = knowledgeDocumentThreeCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentThreeCategory>().eq("two_category_id", twoCategoryId));
        if (threeCategories.size() > 0 && threeCategories != null) {
            ProductNameVersionThreeVo vo = null;
            for (KnowledgeDocumentThreeCategory threeCategory : threeCategories) {
                vo = new ProductNameVersionThreeVo();
                vo.setCategoryName(threeCategory.getCategoryTitle()).setId(threeCategory.getId().toString());
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 查询二级分类下的三级分类id
     * @param twoCategoryId 二级分类id
     * @return list
     */
    @Override
    public List<KnowledgeOneCategoryVo> queryThreeCategory(String twoCategoryId) {
        List<KnowledgeOneCategoryVo> voList = new ArrayList<>();
        //二级下的三级分类列表
        List<KnowledgeDocumentThreeCategory> threeCategoryList = knowledgeDocumentThreeCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentThreeCategory>().eq("two_category_id", twoCategoryId));
        if (!CollectionUtils.isEmpty(threeCategoryList)) {
            threeCategoryList.forEach(threeCategory -> {
                KnowledgeOneCategoryVo vo = new KnowledgeOneCategoryVo().setId(threeCategory.getId().toString()).setTitle(threeCategory.getCategoryTitle());
                voList.add(vo);
            });
        }
        return voList;
    }


}
