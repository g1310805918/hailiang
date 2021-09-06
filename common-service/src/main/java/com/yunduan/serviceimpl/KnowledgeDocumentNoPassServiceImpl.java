package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionEngineerDocument;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.KnowledgeDocumentNoPass;
import com.yunduan.mapper.CollectionEngineerDocumentMapper;
import com.yunduan.mapper.EngineerMapper;
import com.yunduan.mapper.KnowledgeDocumentNoPassMapper;
import com.yunduan.mapper.KnowledgeDocumentThreeCategoryMapper;
import com.yunduan.request.front.document.CreateDocumentReq;
import com.yunduan.service.KnowledgeDocumentNoPassService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.DocumentDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KnowledgeDocumentNoPassServiceImpl extends ServiceImpl<KnowledgeDocumentNoPassMapper, KnowledgeDocumentNoPass> implements KnowledgeDocumentNoPassService {


    @Autowired
    private KnowledgeDocumentNoPassMapper knowledgeDocumentNoPassMapper;
    @Autowired
    private CollectionEngineerDocumentMapper collectionEngineerDocumentMapper;
    @Autowired
    private EngineerMapper engineerMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService knowledgeDocumentThreeCategoryService;


    /**
     * 知识文档详情
     * @param engineerId 工程师id
     * @param documentId 文档id
     * @return DocumentDetailVo
     */
    @Override
    public DocumentDetailVo engineerDocumentDetail(String engineerId,String documentId) {
        //当前工程师用户
        Engineer engineer = engineerMapper.selectById(engineerId);
        //文档记录
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
     * 工程师发布知识文档
     * @param engineerId 工程师id
     * @param createDocumentReq 添加对象
     * @return int
     */
    @Override
    public int engineerCreateDocument(String engineerId, CreateDocumentReq createDocumentReq) {
        KnowledgeDocumentNoPass noPass = new KnowledgeDocumentNoPass();
        //id、工程师id、文档编号
        noPass.setId(SnowFlakeUtil.getPrimaryKeyId()).setEngineerId(Convert.toLong(engineerId)).setDocNumber(RandomUtil.randomNumbers(10));
        //文档类型、三级分类id、文档内容
        noPass.setDocType(createDocumentReq.getDocumentType()).setThreeCategoryId(Convert.toLong(createDocumentReq.getThreeCategoryId())).setDocContent(createDocumentReq.getDocumentContent());
        //更新时间、添加时间、删除标志、对内对外显示、文档标题
        noPass.setUpdateTime(DateUtil.now()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setIsShow(createDocumentReq.getIsShow()).setDocTitle(createDocumentReq.getDocumentTitle());
        //待审核状态
        noPass.setDocStatus(1);
        int row = knowledgeDocumentNoPassMapper.insert(noPass);
        if (row > 0) {
            //todo 异步执行剩余任务（1、发送文档审核消息至COE工程师。）

        }
        return row;
    }



}
