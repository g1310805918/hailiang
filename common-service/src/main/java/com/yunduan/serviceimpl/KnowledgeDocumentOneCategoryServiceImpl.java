package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.KnowledgeDocumentOneCategory;
import com.yunduan.mapper.KnowledgeDocumentOneCategoryMapper;
import com.yunduan.service.KnowledgeDocumentOneCategoryService;
import com.yunduan.vo.KnowledgeOneCategoryVo;
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





}
