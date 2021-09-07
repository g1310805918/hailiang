package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.BugManager;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.WorkOrder;
import com.yunduan.mapper.BugManagerMapper;
import com.yunduan.mapper.EngineerMapper;
import com.yunduan.mapper.WorkOrderMapper;
import com.yunduan.request.front.document.BugManagerInitPageReq;
import com.yunduan.request.front.document.CreateBugFeedbackReq;
import com.yunduan.request.front.document.EngineerBugInitPageReq;
import com.yunduan.service.BugManagerService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ExtractRichTextUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.BudDetailVo;
import com.yunduan.vo.BugInitPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class BugManagerServiceImpl extends ServiceImpl<BugManagerMapper, BugManager> implements BugManagerService {

    @Autowired
    private BugManagerMapper bugManagerMapper;
    @Autowired
    private EngineerMapper engineerMapper;
    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService threeCategoryService;


    /**
     * BUG审核管理页面统计
     * @return map
     */
    @Override
    public Map<String, Integer> queryBaseInfo() {
        Map<String, Integer> map = new HashMap<>();
        //总反馈bug数
        map.put("totalBugCount",bugManagerMapper.selectCount(new QueryWrapper<>()));
        //带我审核的反馈bug数
        map.put("passReviewCount",bugManagerMapper.selectCount(new QueryWrapper<BugManager>().eq("bug_status", StatusCodeUtil.BUG_DOC_NO_REVIEW_STATUS)));
        //审核通过的bug数
        map.put("noPassReviewCount",bugManagerMapper.selectCount(new QueryWrapper<BugManager>().eq("bug_status", StatusCodeUtil.BUG_DOC_PASS_REVIEW_STATUS)));
        return map;
    }


    /**
     * bug页面初始化
     * @param bugManagerInitPageReq 筛选对象
     * @return map
     */
    @Override
    public Map<String, Object> queryInitPage(BugManagerInitPageReq bugManagerInitPageReq) {
        Map<String, Object> map = new HashMap<>();
        //条件构造器
        QueryWrapper<BugManager> queryWrapper = new QueryWrapper<BugManager>()
                //创建人id
                .eq(StrUtil.isNotEmpty(bugManagerInitPageReq.getEngineerId()), "engineer_id", bugManagerInitPageReq.getEngineerId())
                //三级分类id
                .eq(StrUtil.isNotEmpty(bugManagerInitPageReq.getCategoryId()), "category_id", bugManagerInitPageReq.getCategoryId())
                //带我审核
                .eq(bugManagerInitPageReq.getStayMyReview(), "bug_status", StatusCodeUtil.BUG_DOC_NO_REVIEW_STATUS)
                //审核通过
                .eq(bugManagerInitPageReq.getReviewPass(), "bug_status", StatusCodeUtil.BUG_DOC_PASS_REVIEW_STATUS).orderByDesc("create_time");
        //查询出的结果集
        List<BugManager> records = bugManagerMapper.selectPage(new Page<>(bugManagerInitPageReq.getPageNo(), bugManagerInitPageReq.getPageSize()), queryWrapper).getRecords();
        map.put("voList",getResultBugRecordList(records));
        map.put("total",bugManagerMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 封装bug反馈列表结果数据集合
     * @param records 原数据集合
     * @return list
     */
    private List<BugInitPageVo> getResultBugRecordList(List<BugManager> records) {
        List<BugInitPageVo> voList = new ArrayList<>();
        if (records.size() > 0 && records != null) {
            BugInitPageVo vo = null;
            for (BugManager record : records) {
                Engineer engineer = engineerMapper.selectById(record.getEngineerId());
                if (engineer == null) {
                    continue;
                }
                vo = new BugInitPageVo().setId(record.getId().toString()).setBugTitle(record.getBugTitle()).setCategoryName(record.getCategoryName()).setDocStatus(record.getBugStatus()).setOutTradeNo(record.getOutTradeNo());
                vo.setCreateBy(engineer.getUsername());
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * bug反馈详情
     * @param id id
     * @return BudDetailVo
     */
    @Override
    public BudDetailVo queryDetail(String id) {
        BugManager bugManager = bugManagerMapper.selectById(id);
        if (bugManager != null){
            BudDetailVo vo = new BudDetailVo();
            vo.setId(bugManager.getId().toString()).setContent(bugManager.getBugContent()).setCategoryName(bugManager.getCategoryName());
            vo.setOutTradeNo(bugManager.getOutTradeNo()).setStatus(bugManager.getBugStatus()).setCreateTime(bugManager.getCreateTime().substring(0,16));
            vo.setCreateBy(engineerMapper.selectById(bugManager.getEngineerId()).getUsername()).setTitle(bugManager.getBugTitle());
            return vo;
        }
        return null;
    }

    /**
     * 查询普通工程师BUG反馈页面统计
     * @return map
     */
    @Override
    public Map<String, Integer> queryFeedbackBaseInfo() {
        Map<String, Integer> map = new HashMap<>();
        Long userId = ContextUtil.getUserId();
        //待审核的反馈bug
        map.put("noReviewBugCount",bugManagerMapper.selectCount(new QueryWrapper<BugManager>().eq("bug_status",StatusCodeUtil.BUG_DOC_NO_REVIEW_STATUS).eq("engineer_id",userId)));
        //审核通过的反馈bug
        map.put("passReviewCount",bugManagerMapper.selectCount(new QueryWrapper<BugManager>().eq("bug_status", StatusCodeUtil.BUG_DOC_PASS_REVIEW_STATUS).eq("engineer_id",userId)));
        //审核失败的反馈bug
        map.put("noPassReviewCount",bugManagerMapper.selectCount(new QueryWrapper<BugManager>().eq("bug_status", StatusCodeUtil.BUG_DOC_NO_PASS_REVIEW_STATUS).eq("engineer_id",userId)));
        return map;
    }


    /**
     * 工程师bug反馈页面初始化
     * @param engineerBugInitPageReq 筛选对象
     * @return map
     */
    @Override
    public Map<String, Object> queryEngineerInitPage(EngineerBugInitPageReq engineerBugInitPageReq) {
        Map<String, Object> map = new HashMap<>();
        //条件构造器
        QueryWrapper<BugManager> queryWrapper = new QueryWrapper<BugManager>()
                .eq("engineer_id", ContextUtil.getUserId())
                .eq(engineerBugInitPageReq.getNoReview(), "doc_status", StatusCodeUtil.BUG_DOC_NO_REVIEW_STATUS)
                .eq(engineerBugInitPageReq.getPassReview(), "doc_status", StatusCodeUtil.BUG_DOC_PASS_REVIEW_STATUS)
                .eq(engineerBugInitPageReq.getRefusedReview(), "doc_status", StatusCodeUtil.BUG_DOC_NO_PASS_REVIEW_STATUS)
                .eq(StrUtil.isNotEmpty(engineerBugInitPageReq.getCategoryId()), "category_id", engineerBugInitPageReq.getCategoryId())
                .eq(StrUtil.isNotEmpty(engineerBugInitPageReq.getOutTradeNo()), "out_trade_no", engineerBugInitPageReq.getOutTradeNo())
                .like(StrUtil.isNotEmpty(engineerBugInitPageReq.getBugTitle()), "bug_title", engineerBugInitPageReq.getBugTitle())
                .orderByDesc("create_time");
        //bug反馈记录列表
        List<BugManager> records = bugManagerMapper.selectPage(new Page<>(engineerBugInitPageReq.getPageNo(), engineerBugInitPageReq.getPageSize()), queryWrapper).getRecords();
        map.put("voList",getResultBugRecordList(records));
        map.put("total",bugManagerMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 添加bug反馈
     * @param createBugFeedbackReq 添加参数
     * @return int
     */
    @Override
    public int createBugFeedback(CreateBugFeedbackReq createBugFeedbackReq) {
        BugManager entity = new BugManager();
        WorkOrder workOrder = workOrderMapper.selectOne(new QueryWrapper<WorkOrder>().eq("out_trade_no", createBugFeedbackReq.getOutTradeNo()));
        entity.setOutTradeNo(createBugFeedbackReq.getOutTradeNo()).setWorkOrderId(workOrder == null ? 0L : workOrder.getId()).setEngineerId(ContextUtil.getUserId());
        entity.setCategoryId(Convert.toLong(createBugFeedbackReq.getCategoryId())).setCategoryName(threeCategoryService.getKnowledgeCategoryName(createBugFeedbackReq.getCategoryId()));
        entity.setBugTitle(createBugFeedbackReq.getBugTitle()).setBugContent(createBugFeedbackReq.getContent());
        int row = bugManagerMapper.insert(entity);
        if (row > 0) {
            // todo 发送BUG审核文档消息给BDE工程师

        }
        return row;
    }


}
