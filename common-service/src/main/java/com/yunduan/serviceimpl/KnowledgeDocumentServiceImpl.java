package com.yunduan.serviceimpl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.*;
import com.yunduan.mapper.*;
import com.yunduan.request.front.knowledge.KnowledgeListReq;
import com.yunduan.request.front.knowledge.KnowledgeSearchReq;
import com.yunduan.service.KnowledgeDocumentService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ExtractRichTextUtil;
import com.yunduan.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocument> implements KnowledgeDocumentService {

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    @Autowired
    private CollectionAccountDocumentMapper collectionAccountDocumentMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService knowledgeDocumentThreeCategoryService;
    @Autowired
    private KnowledgeDocumentThreeCategoryMapper threeCategoryMapper;
    @Autowired
    private KnowledgeDocumentTwoCategoryMapper twoCategoryMapper;
    @Autowired
    private KnowledgeDocumentOneCategoryMapper oneCategoryMapper;


    /**
     * 查询三级分类下的知识文档
     * @param knowledgeListReq 知识文档
     * @return map
     */
    @Override
    public Map<String, Object> queryKnowledgeListVo(KnowledgeListReq knowledgeListReq) {
        Map<String, Object> map = new HashMap<>();
        //文档列表
        List<KnowledgeDocument> documentList = knowledgeDocumentMapper.selectPage(
                new Page<>(knowledgeListReq.getPageNo(), knowledgeListReq.getPageSize()),
                new QueryWrapper<KnowledgeDocument>()
                        .eq("three_category_id", knowledgeListReq.getThreeCategoryId())
                        .eq("is_show", 1)
                        .eq(knowledgeListReq.getDocType() != null, "doc_type", knowledgeListReq.getDocType())
        ).getRecords();
        //结果封装
        List<KnowledgeListVo> voList = new ArrayList<>();
        if (documentList.size() > 0 && documentList != null) {
            KnowledgeListVo vo = null;
            for (KnowledgeDocument document : documentList) {
                vo = new KnowledgeListVo();
                //文档id、文档概要、文档标题
                vo.setId(document.getId().toString()).setDocProfile(ExtractRichTextUtil.dealContent(document.getDocContent())).setDocTitle(document.getDocTitle());
                //分类名称
                String categoryName = knowledgeDocumentThreeCategoryService.getKnowledgeCategoryName(document.getThreeCategoryId().toString());
                vo.setCategoryName(categoryName);
                voList.add(vo);
            }
        }

        //筛选总条数
        Integer total = knowledgeDocumentMapper.selectCount(new QueryWrapper<KnowledgeDocument>()
                .eq("three_category_id", knowledgeListReq.getThreeCategoryId())
                .eq("is_show", 1)
                .eq(knowledgeListReq.getDocType() != null, "doc_type", knowledgeListReq.getDocType()));

        map.put("voList",voList);
        map.put("total",total);

        return map;
    }


    /**
     * 知识文档详情
     * @param id 文档id
     * @return KnowledgeDetailVo
     */
    @Override
    public KnowledgeDetailVo queryDocDetailInfo(String id) {
        //知识文档
        KnowledgeDocument document = knowledgeDocumentMapper.selectOne(new QueryWrapper<KnowledgeDocument>().eq("id", id));
        if (document != null) {
            KnowledgeDetailVo vo = new KnowledgeDetailVo();
            //文档id、文档标题、分类名称
            vo.setId(document.getId().toString()).setDocTitle(document.getDocTitle()).setCategoryName(knowledgeDocumentThreeCategoryService.getKnowledgeCategoryName(document.getThreeCategoryId().toString()));
            //文档编号、文档发布时间、更新时间、
            vo.setDocNumber(document.getDocNumber()).setCreateTime(document.getCreateTime()).setUpdateTime(StrUtil.hasEmpty(document.getUpdateTime()) ? "" : document.getUpdateTime());
            //用户是否收藏当前文档
            Integer count = collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", ContextUtil.getUserId()).eq("document_id", id));
            //是否收藏、文档标题、文档类型、文档内容
            vo.setIsCollect(count > 0 ? 1 : 0).setDocTitle(document.getDocTitle()).setDocType(document.getDocType()).setDocContent(document.getDocContent());
            return vo;
        }
        return null;
    }


    /**
     * 模糊搜索知识文档
     * @param searchContent 搜索内容
     * @return list
     */
    @Override
    public List<KnowledgeLazySearchVo> queryKnowledgeLazySearch(String searchContent) {
        return knowledgeDocumentMapper.selectKnowledgeLazySearch(searchContent);
    }


    /**
     * 模糊搜索知识文档列表数据
     * @param knowledgeSearchReq 搜索对象
     * @return map
     */
    @Override
    public Map<String, Object> queryKnowledgeResult(KnowledgeSearchReq knowledgeSearchReq) {
        Map<String, Object> map = new HashMap<>();
        //筛选后分页的知识文档记录列表【对外可见的】
        List<KnowledgeDocument> records = knowledgeDocumentMapper.selectPage(
                new Page<>(knowledgeSearchReq.getPageNo(), knowledgeSearchReq.getPageSize()),
                new QueryWrapper<KnowledgeDocument>()
                        .eq("is_show", 1)
                        .like("doc_content", knowledgeSearchReq.getSearchContent())
                        .eq(StrUtil.isNotEmpty(knowledgeSearchReq.getThreeCategoryId()), "three_category_id", knowledgeSearchReq.getThreeCategoryId())
                        .eq(StrUtil.isNotEmpty(knowledgeSearchReq.getKnowledgeType()), "doc_type", knowledgeSearchReq.getKnowledgeType())
        ).getRecords();
        //知识文档结果封装集合
        List<KnowledgeListVo> voList = new ArrayList<>();
        if (records.size() > 0 && records != null) {
            KnowledgeListVo vo = null;
            for (KnowledgeDocument record : records) {
                vo = new KnowledgeListVo();
                //文档id、文档标题、文档概要
                vo.setId(record.getId().toString()).setDocTitle(record.getDocTitle()).setDocProfile(ExtractRichTextUtil.dealContent(record.getDocContent()));
                //文档所属分类
                String categoryName = knowledgeDocumentThreeCategoryService.getKnowledgeCategoryName(record.getThreeCategoryId() + "");
                vo.setCategoryName(categoryName);
                voList.add(vo);
            }
        }
        //筛选结果下的总数
        Integer total = knowledgeDocumentMapper.selectCount(new QueryWrapper<KnowledgeDocument>()
                .eq("is_show", 1)
                .like("doc_content", knowledgeSearchReq.getSearchContent())
                .eq(StrUtil.isNotEmpty(knowledgeSearchReq.getThreeCategoryId()), "three_category_id", knowledgeSearchReq.getThreeCategoryId())
                .eq(StrUtil.isNotEmpty(knowledgeSearchReq.getKnowledgeType()), "doc_type", knowledgeSearchReq.getKnowledgeType()));

        map.put("voList",voList);
        map.put("total",total);
        return map;
    }


    /**
     * 搜索内容所属文档分类
     * @param searchContent 搜索内容
     * @return list
     */
    @Override
    public List<KnowledgeLevel3CategoryList> querySearchContentInCategoryList(String searchContent) {
        List<KnowledgeLevel3CategoryList> resultList = new ArrayList<>();
        //知识文档包含xx内容的id集合
        List<Long> threeCategoryIdList = knowledgeDocumentMapper.selectDocumentIdList(searchContent);
        if (threeCategoryIdList.size() > 0 && threeCategoryIdList != null) {
            //查询三级分类列表
            List<KnowledgeDocumentThreeCategory> threeCategoryList = threeCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentThreeCategory>().in("id", threeCategoryIdList));
            //一级分类id集合
            Set<Long> oneCategorySet = new TreeSet<>();
            if (threeCategoryIdList.size() > 0 && threeCategoryIdList != null) {
                for (KnowledgeDocumentThreeCategory threeCategory : threeCategoryList) {
                    //向一级set集合中添加所属二级分类id
                    oneCategorySet.add(threeCategory.getOneCategoryId());
                }
            }
            //遍历一级set集合获取所有一级分类
            if (oneCategorySet.size() > 0 && oneCategorySet != null) {
                //一级分类列表
                List<KnowledgeDocumentOneCategory> oneCategoryList = oneCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentOneCategory>().in("id", oneCategorySet));
                if (oneCategoryList.size() > 0 && oneCategoryList != null) {
                    KnowledgeLevel3CategoryList oneLevel = null;
                    for (KnowledgeDocumentOneCategory oneCategory : oneCategoryList) {
                        oneLevel = new KnowledgeLevel3CategoryList();
                        //一级分类标题
                        oneLevel.setOneTitle(oneCategory.getCategoryTitle());
                        //根据一级查询所有二级列表
                        List<KnowledgeDocumentTwoCategory> twoCategoryList = twoCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentTwoCategory>().eq("one_category_id", oneCategory.getId()));

                        //二、三级分类集合
                        List<KnowledgeTwoThreeCategoryVo> twoThreeCategoryVoList = new ArrayList<>();
                        if (twoCategoryList.size() > 0 && twoCategoryList != null) {
                            for (KnowledgeDocumentTwoCategory twoCategory : twoCategoryList) {
                                KnowledgeTwoThreeCategoryVo twoLevel = new KnowledgeTwoThreeCategoryVo();
                                twoLevel.setId(twoCategory.getId().toString()).setTitle(twoCategory.getCategoryTitle());
                                //二级分类下的三级分类列表
                                List<KnowledgeDocumentThreeCategory> threeCategories = threeCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentThreeCategory>().eq("two_category_id", twoCategory.getId()));
                                //三级分类结果集合
                                List<KnowledgeOneCategoryVo> threeCategoryVoList = new ArrayList<>();
                                if (threeCategories.size() > 0 && threeCategories != null) {
                                    for (KnowledgeOneCategoryVo knowledgeOneCategoryVo : threeCategoryVoList) {
                                        KnowledgeOneCategoryVo vo = new KnowledgeOneCategoryVo();
                                        vo.setId(knowledgeOneCategoryVo.getId().toString()).setTitle(knowledgeOneCategoryVo.getTitle());
                                        //添加至三级集合
                                        threeCategoryVoList.add(vo);
                                    }
                                }
                                //设置三级集合至二级集合
                                twoLevel.setThreeCategoryList(threeCategoryVoList);
                                //设置二级集合
                                twoThreeCategoryVoList.add(twoLevel);
                            }
                        }
                        //设置一级集合
                        oneLevel.setVoList(twoThreeCategoryVoList);
                        resultList.add(oneLevel);
                    }
                }
            }
        }
        return resultList;
    }


}
