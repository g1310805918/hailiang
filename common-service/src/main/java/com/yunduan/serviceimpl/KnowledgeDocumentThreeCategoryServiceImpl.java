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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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


}
