package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.KnowledgeDocumentThreeCategory;
import com.yunduan.entity.KnowledgeDocumentTwoCategory;
import com.yunduan.mapper.KnowledgeDocumentThreeCategoryMapper;
import com.yunduan.mapper.KnowledgeDocumentTwoCategoryMapper;
import com.yunduan.service.KnowledgeDocumentTwoCategoryService;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.KnowledgeTwoThreeCategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class KnowledgeDocumentTwoCategoryServiceImpl extends ServiceImpl<KnowledgeDocumentTwoCategoryMapper, KnowledgeDocumentTwoCategory> implements KnowledgeDocumentTwoCategoryService {

    @Autowired
    private KnowledgeDocumentTwoCategoryMapper knowledgeDocumentTwoCategoryMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryMapper knowledgeDocumentThreeCategoryMapper;


    /**
     * 查询一级分类下的二、三级分类
     * @param oneCategoryId 一级分类id
     * @return list
     */
    @Override
    public List<KnowledgeTwoThreeCategoryVo> queryKnowLedgeCategoryList(String oneCategoryId) {
        List<KnowledgeTwoThreeCategoryVo> voList = new ArrayList<>();
        //一级分类下的二级分类
        List<KnowledgeDocumentTwoCategory> twoCategoryList = knowledgeDocumentTwoCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentTwoCategory>().eq("one_category_id", oneCategoryId));
        if (twoCategoryList.size() > 0 && twoCategoryList != null) {
            //循环二级分类集合、
            KnowledgeTwoThreeCategoryVo vo = null;
            for (KnowledgeDocumentTwoCategory twoCategory : twoCategoryList) {
                vo = new KnowledgeTwoThreeCategoryVo();
                //设置二级分类id、二级分类名称
                vo.setId(twoCategory.getId().toString()).setTitle(twoCategory.getCategoryTitle());
                //二级分类下的三级分类
                List<KnowledgeDocumentThreeCategory> threeCategoryList = knowledgeDocumentThreeCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentThreeCategory>().eq("two_category_id", twoCategory.getId()));

                List<KnowledgeOneCategoryVo> threeVoList = new ArrayList<>();
                if (threeCategoryList.size() > 0 && threeCategoryList != null) {
                    //循环三级分类集合、
                    KnowledgeOneCategoryVo threeVo = null;
                    for (KnowledgeDocumentThreeCategory threeCategory : threeCategoryList) {
                        threeVo = new KnowledgeOneCategoryVo();
                        threeVo.setId(threeCategory.getId().toString()).setTitle(threeCategory.getCategoryTitle());
                        threeVoList.add(threeVo);
                    }
                }
                vo.setThreeCategoryList(threeVoList);
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 获取一级分类下的二级列表
     * @param oneCategoryId 一级id
     * @return list
     */
    @Override
    public List<KnowledgeOneCategoryVo> queryTwoCategoryList(String oneCategoryId) {
        List<KnowledgeOneCategoryVo> voList = new ArrayList<>();
        //二级分裂列表
        List<KnowledgeDocumentTwoCategory> twoCategoryList = knowledgeDocumentTwoCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentTwoCategory>().eq("one_category_id", oneCategoryId));
        if (!CollectionUtils.isEmpty(twoCategoryList)) {
            twoCategoryList.forEach(twoCategory -> {
                KnowledgeOneCategoryVo vo = new KnowledgeOneCategoryVo();
                vo.setId(twoCategory.getId().toString()).setTitle(twoCategory.getCategoryTitle());
                voList.add(vo);
            });
        }
        return voList;
    }


}
