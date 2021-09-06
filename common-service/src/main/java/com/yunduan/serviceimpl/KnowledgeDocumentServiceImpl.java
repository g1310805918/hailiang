package com.yunduan.serviceimpl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.*;
import com.yunduan.mapper.*;
import com.yunduan.request.front.document.InitDocumentManagerReq;
import com.yunduan.request.front.knowledge.KnowledgeListReq;
import com.yunduan.request.front.knowledge.KnowledgeSearchReq;
import com.yunduan.service.KnowledgeDocumentService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ExtractRichTextUtil;
import com.yunduan.utils.StatusCodeUtil;
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
    @Autowired
    private KnowledgeDocumentNoPassMapper noPassMapper;
    @Autowired
    private CollectionEngineerDocumentMapper engineerDocumentMapper;
    @Autowired
    private EngineerMapper engineerMapper;



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
                        //如果为空或者false标志当前用户未工程师。则查看公司所有文档
                        .eq((knowledgeListReq.getEngineerFlag() == null || knowledgeListReq.getEngineerFlag() == false),"is_show", 1)
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
     * @param nullStr 搜索标志
     * @return list
     */
    @Override
    public List<KnowledgeLazySearchVo> queryKnowledgeLazySearch(String searchContent,String nullStr) {
        return knowledgeDocumentMapper.selectKnowledgeLazySearch(searchContent,nullStr);
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


    /**
     * 工程师初始化文档管理页面
     * @param initDocumentManagerReq 初始化对象
     * @return map
     */
    @Override
    public Map<String, Object> engineerInitPage(InitDocumentManagerReq initDocumentManagerReq) {
        Map<String,Object> map = new HashMap<>();
        Long userId = ContextUtil.getUserId();
        //公司总文档数
        map.put("totalDocumentCount",knowledgeDocumentMapper.selectCount(new QueryWrapper<>()));
        //我发布成功的文档
        map.put("myFbCount",knowledgeDocumentMapper.selectCount(new QueryWrapper<KnowledgeDocument>().eq("engineer_id",userId)));
        //我的待审核文档
        map.put("myNoCheckCount",noPassMapper.selectCount(new QueryWrapper<KnowledgeDocumentNoPass>().eq("engineer_id",userId).eq("doc_status",1)));
        //我收藏的文档
        map.put("myCollectionCount",engineerDocumentMapper.selectCount(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id",userId)));

        //我收藏的文档选中时
        List<Long> docIdList = new ArrayList<>();
        if (initDocumentManagerReq.getMyCollection()) {
            //工程师收藏的文档列表
            List<CollectionEngineerDocument> documentList = engineerDocumentMapper.selectList(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", userId));
            if (documentList.size() > 0 && documentList != null) {
                for (CollectionEngineerDocument collectionEngineerDocument : documentList) {
                    docIdList.add(collectionEngineerDocument.getDocumentId());
                }
            }
        }
        //条件构造器
        QueryWrapper<KnowledgeDocumentNoPass> queryWrapper = new QueryWrapper<KnowledgeDocumentNoPass>()
                //全部创建人
                .eq(StrUtil.isNotEmpty(initDocumentManagerReq.getEngineerId()), "engineer_id", initDocumentManagerReq.getEngineerId())
                //文档所属分类
                .eq(StrUtil.isNotEmpty(initDocumentManagerReq.getCategoryId()), "threeCategory_id", initDocumentManagerReq.getCategoryId())
                //我创建的文档
                .eq(initDocumentManagerReq.getMyCreate(), "engineer_id", userId)
                //我发布成功的文档
                .eq(initDocumentManagerReq.getMyFb(), "doc_status", 2)
                //我收藏的文档
                .in(docIdList.size() > 0 && docIdList != null, "id", docIdList)
                //文档id
                .eq(StrUtil.isNotEmpty(initDocumentManagerReq.getDocumentId()), "id", initDocumentManagerReq.getDocumentId())
                //文档标题
                .eq(StrUtil.isNotEmpty(initDocumentManagerReq.getDocumentTitle()), "doc_title", initDocumentManagerReq.getDocumentTitle());
        //我的初始化文档列表
        List<KnowledgeDocumentNoPass> records = noPassMapper.selectPage(new Page<>(initDocumentManagerReq.getPageNo(), initDocumentManagerReq.getPageSize()),queryWrapper).getRecords();
        //结果封装
        List<InitDocumentListVo> voList = queryInitResult(records);
        map.put("voList",voList);
        map.put("pageTotal",noPassMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 封装工程师文档列表初始化结果
     * @param records 文档列表
     * @return list
     */
    private List<InitDocumentListVo> queryInitResult(List<KnowledgeDocumentNoPass> records) {
        List<InitDocumentListVo> voList = new ArrayList<>();
        if (records.size() > 0 && records != null) {
            InitDocumentListVo vo = null;
            for (KnowledgeDocumentNoPass record : records) {
                Engineer engineer = engineerMapper.selectById(record.getEngineerId());
                if (engineer == null) {
                    continue;
                }
                vo = new InitDocumentListVo();
                vo.setDocumentId(record.getId().toString()).setDocumentTitle(record.getDocTitle());
                vo.setDocumentNum(record.getDocNumber()).setCategoryName(knowledgeDocumentThreeCategoryService.getKnowledgeCategoryName(record.getThreeCategoryId().toString()));
                vo.setDocumentType(record.getDocType()).setCreateBy(StrUtil.hasEmpty(engineer.getUsername()) ? "" : engineer.getUsername()).setCreateTime(record.getCreateTime().substring(0,16));
                vo.setLastUpdateTime(StrUtil.hasEmpty(record.getUpdateTime()) ? "" : record.getUpdateTime().substring(0,16)).setDocumentStatus(record.getDocStatus());
                Integer count = engineerDocumentMapper.selectCount(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", engineer.getId()).eq("document_id", record.getId()));
                vo.setIsCollect(count > 0 ? 1 : 0);
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 工程师删除知识文档
     * @param documentId 文档id
     * @return int
     */
    @Override
    public int engineerRemoveDocument(String documentId) {
        int row = 0;
        KnowledgeDocument document1 = knowledgeDocumentMapper.selectById(documentId);
        KnowledgeDocumentNoPass document2 = noPassMapper.selectById(documentId);
        if (document1 != null && document2 != null) {
            document1.setDelFlag(StatusCodeUtil.DELETE_FLAG);
            document2.setDelFlag(StatusCodeUtil.DELETE_FLAG);
            row += knowledgeDocumentMapper.updateById(document1);
            row += noPassMapper.updateById(document2);
        }
        return row;
    }


}
