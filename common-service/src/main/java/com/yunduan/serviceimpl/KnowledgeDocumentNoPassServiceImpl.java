package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionEngineerDocument;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.KnowledgeDocument;
import com.yunduan.entity.KnowledgeDocumentNoPass;
import com.yunduan.mapper.CollectionEngineerDocumentMapper;
import com.yunduan.mapper.EngineerMapper;
import com.yunduan.mapper.KnowledgeDocumentMapper;
import com.yunduan.mapper.KnowledgeDocumentNoPassMapper;
import com.yunduan.request.front.document.CreateDocumentReq;
import com.yunduan.request.front.review.ReviewInitReq;
import com.yunduan.service.KnowledgeDocumentNoPassService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.SendMessageUtil;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.DocumentDetailVo;
import com.yunduan.vo.InitDocumentListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class KnowledgeDocumentNoPassServiceImpl extends ServiceImpl<KnowledgeDocumentNoPassMapper, KnowledgeDocumentNoPass> implements KnowledgeDocumentNoPassService {

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    @Autowired
    private KnowledgeDocumentNoPassMapper knowledgeDocumentNoPassMapper;
    @Autowired
    private CollectionEngineerDocumentMapper collectionEngineerDocumentMapper;
    @Autowired
    private EngineerMapper engineerMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService knowledgeDocumentThreeCategoryService;
    @Autowired
    private CollectionEngineerDocumentMapper engineerDocumentMapper;
    @Autowired
    private SendMessageUtil sendMessageUtil;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;




    /**
     * ??????????????????
     * @param engineerId ?????????id
     * @param documentId ??????id
     * @return DocumentDetailVo
     */
    @Override
    public DocumentDetailVo engineerDocumentDetail(String engineerId,String documentId) {
        //?????????????????????
        Engineer engineer = engineerMapper.selectById(engineerId);
        //????????????
        KnowledgeDocumentNoPass record = knowledgeDocumentNoPassMapper.selectById(documentId);
        if (record != null) {
            DocumentDetailVo vo = new DocumentDetailVo();
            vo.setDocumentId(record.getId().toString()).setDocumentTitle(record.getDocTitle());
            vo.setDocumentNum(record.getDocNumber()).setCategoryName(knowledgeDocumentThreeCategoryService.getKnowledgeCategoryName(record.getThreeCategoryId().toString()));
            vo.setDocumentType(record.getDocType()).setCreateBy(StrUtil.hasEmpty(engineer.getUsername()) ? "" : engineer.getUsername()).setCreateTime(record.getCreateTime().substring(0,16));
            vo.setLastUpdateTime(StrUtil.hasEmpty(record.getUpdateTime()) ? "" : record.getUpdateTime().substring(0,16)).setDocumentStatus(record.getDocStatus());
            Integer count = collectionEngineerDocumentMapper.selectCount(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", engineer.getId()).eq("document_id", record.getId()));
            vo.setIsCollect(count > 0 ? 1 : 0);
            vo.setDocumentContent(record.getDocContent());
            return vo;
        }
        return null;
    }


    /**
     * ???????????????????????????
     * @param engineerId ?????????id
     * @param createDocumentReq ????????????
     * @return int
     */
    @Override
    public int engineerCreateDocument(String engineerId, CreateDocumentReq createDocumentReq) {
        KnowledgeDocumentNoPass noPass = new KnowledgeDocumentNoPass();
        //id????????????id???????????????
        noPass.setId(SnowFlakeUtil.getPrimaryKeyId()).setEngineerId(Convert.toLong(engineerId)).setDocNumber("D" + RandomUtil.randomNumbers(10));
        //???????????????????????????id???????????????
        noPass.setDocType(createDocumentReq.getDocumentType()).setThreeCategoryId(Convert.toLong(createDocumentReq.getThreeCategoryId())).setDocContent(createDocumentReq.getDocumentContent());
        //??????????????????????????????????????????????????????????????????????????????
        noPass.setUpdateTime(DateUtil.now()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setIsShow(createDocumentReq.getIsShow()).setDocTitle(createDocumentReq.getDocumentTitle());
        //???????????????
        noPass.setDocStatus(1);
        int row = knowledgeDocumentNoPassMapper.insert(noPass);
        if (row > 0) {
            threadPoolTaskExecutor.execute(() -> sendMessageUtil.engineerSendNormalDocumentReviewApplyToCOE(noPass.getId().toString()));
        }
        return row;
    }


    /**
     * COE???????????????????????????
     * @return map
     */
    @Override
    public Map<String, Integer> queryDocumentReviewStatistical() {
        Map<String, Integer> map = new HashMap<>();
        //??????????????????
        map.put("noPassCount",knowledgeDocumentNoPassMapper.selectCount(new QueryWrapper<KnowledgeDocumentNoPass>().eq("doc_status",1)));
        //????????????????????????
        map.put("passCount",knowledgeDocumentNoPassMapper.selectCount(new QueryWrapper<KnowledgeDocumentNoPass>().eq("doc_status",2)));
        //????????????????????????
        map.put("refusedCount",knowledgeDocumentNoPassMapper.selectCount(new QueryWrapper<KnowledgeDocumentNoPass>().eq("doc_status",3)));
        //??????????????????
        map.put("collectCount",collectionEngineerDocumentMapper.selectCount(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", ContextUtil.getUserId())));
        return map;
    }


    /**
     * COE???????????????????????????
     * @param reviewInitReq ???????????????
     * @return map
     */
    @Override
    public Map<String, Object> queryReviewInitPage(ReviewInitReq reviewInitReq) {
        Map<String,Object> map = new HashMap<>();
        //???????????????
        QueryWrapper<KnowledgeDocumentNoPass> queryWrapper = new QueryWrapper<KnowledgeDocumentNoPass>()
                //?????????
                .eq(StrUtil.isNotEmpty(reviewInitReq.getEngineerId()), "engineer_id", reviewInitReq.getEngineerId())
                //??????????????????
                .eq(StrUtil.isNotEmpty(reviewInitReq.getThreeCategoryId()), "three_category_id", reviewInitReq.getThreeCategoryId())
                //?????????
                .eq((reviewInitReq.getNoPassReview() != null && reviewInitReq.getNoPassReview()), "doc_status", 1)
                //????????????
                .eq((reviewInitReq.getPassReview() != null && reviewInitReq.getPassReview()), "doc_status", 2)
                //????????????
                .eq((reviewInitReq.getRefusedReview() != null && reviewInitReq.getRefusedReview()), "doc_status", 3)
                //????????????
                .eq(StrUtil.isNotEmpty(reviewInitReq.getDocumentId()), "doc_number", reviewInitReq.getDocumentId())
                //????????????
                .like(StrUtil.isNotEmpty(reviewInitReq.getDocumentTitle()), "doc_title", reviewInitReq.getDocumentTitle())
                .orderByDesc("create_time");
        //??????????????????
        Page<KnowledgeDocumentNoPass> page = knowledgeDocumentNoPassMapper.selectPage(new Page<>(reviewInitReq.getPageNo(), reviewInitReq.getPageSize()), queryWrapper);
        map.put("voList",getInitPageData(page.getRecords()));
        map.put("total",page.getTotal());
        return map;
    }


    /**
     * ????????????????????????
     * @param documentNoPassList ??????????????????
     * @return list
     */
    private List<InitDocumentListVo> getInitPageData(List<KnowledgeDocumentNoPass> documentNoPassList){
        List<InitDocumentListVo> voList = new ArrayList<>();
        if (documentNoPassList != null && documentNoPassList.size() > 0) {
            InitDocumentListVo vo = null;
            for (KnowledgeDocumentNoPass noPass : documentNoPassList) {
                Engineer engineer = engineerMapper.selectById(noPass.getEngineerId());
                if (engineer == null) {
                    continue;
                }
                vo = new InitDocumentListVo();
                vo.setDocumentId(noPass.getId().toString()).setDocumentTitle(noPass.getDocTitle());
                vo.setDocumentNum(noPass.getDocNumber()).setCategoryName(knowledgeDocumentThreeCategoryService.getKnowledgeCategoryName(noPass.getThreeCategoryId().toString()));
                vo.setDocumentType(noPass.getDocType()).setCreateBy(StrUtil.hasEmpty(engineer.getUsername()) ? "" : engineer.getUsername()).setCreateTime(noPass.getCreateTime().substring(0,16));
                vo.setLastUpdateTime(StrUtil.hasEmpty(noPass.getUpdateTime()) ? "" : noPass.getUpdateTime().substring(0,16)).setDocumentStatus(noPass.getDocStatus());
                Integer count = engineerDocumentMapper.selectCount(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", engineer.getId()).eq("document_id", noPass.getId()));
                vo.setIsCollect(count > 0 ? 1 : 0);
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * COE?????????????????????????????????????????????
     * @param documentId ??????id
     * @param docStatus ????????????
     * @return int
     */
    @Override
    public int changeDocumentStatus(String documentId, Integer docStatus) {
        KnowledgeDocumentNoPass noPass = knowledgeDocumentNoPassMapper.selectById(documentId);
        if (noPass != null) {
            noPass.setDocStatus(docStatus);
            int row = knowledgeDocumentNoPassMapper.updateById(noPass);
            if (row > 0) {
                //????????????????????? ????????? ??????
                KnowledgeDocument document = JSONObject.parseObject(JSONObject.toJSONString(noPass), KnowledgeDocument.class);
                knowledgeDocumentMapper.insert(document);
            }
            return row;
        }
        return 0;
    }


    /**
     * ????????????
     * @param documentId ??????id
     * @return DocumentDetailVo
     */
    @Override
    public DocumentDetailVo documentDetail(String documentId) {
        KnowledgeDocumentNoPass noPass = knowledgeDocumentNoPassMapper.selectById(documentId);
        if (noPass != null) {
            return engineerDocumentDetail(noPass.getEngineerId().toString(),documentId);
        }
        return null;
    }


}
