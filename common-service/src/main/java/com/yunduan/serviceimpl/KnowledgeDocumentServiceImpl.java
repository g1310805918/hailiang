package com.yunduan.serviceimpl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionAccountDocument;
import com.yunduan.entity.KnowledgeDocument;
import com.yunduan.mapper.CollectionAccountDocumentMapper;
import com.yunduan.mapper.KnowledgeDocumentMapper;
import com.yunduan.request.front.knowledge.KnowledgeListReq;
import com.yunduan.service.KnowledgeDocumentService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ExtractRichTextUtil;
import com.yunduan.vo.KnowledgeDetailVo;
import com.yunduan.vo.KnowledgeListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocument> implements KnowledgeDocumentService {

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    @Autowired
    private CollectionAccountDocumentMapper collectionAccountDocumentMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService knowledgeDocumentThreeCategoryService;


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



}
