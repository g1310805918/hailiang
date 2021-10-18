package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Account;
import com.yunduan.entity.CommunicationRecord;
import com.yunduan.entity.WorkOrder;
import com.yunduan.mapper.AccountMapper;
import com.yunduan.mapper.CommunicationRecordMapper;
import com.yunduan.mapper.WorkOrderMapper;
import com.yunduan.request.front.servicerequest.*;
import com.yunduan.service.CommunicationRecordService;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.CommunicationResult;
import com.yunduan.vo.WorkOrderProblemProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional
public class CommunicationRecordServiceImpl extends ServiceImpl<CommunicationRecordMapper, CommunicationRecord> implements CommunicationRecordService {

    @Autowired
    private CommunicationRecordMapper communicationRecordMapper;
    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;



    /**
     * 查询工单沟通记录
     * @param workOrderCommunicationReq 筛选条件
     * @return list
     */
    @Override
    public Map<String,Object> queryWorkOrderCommunicationRecord(WorkOrderCommunicationReq workOrderCommunicationReq) {
        Map<String,Object> map = new HashMap<>();
        //问答记录列表
        List<CommunicationResult> resultList = new ArrayList<>();
        //当前工单
        WorkOrder workOrder = workOrderMapper.selectById(workOrderCommunicationReq.getWorkOrderId());
        if (workOrder == null) {
            map.put("problem","{}");
            map.put("recordList",resultList);
            return map;
        }
        String vdmCode = workOrderCommunicationReq.getVDMCode();  //vdm标签
        String sortWay = workOrderCommunicationReq.getSortWay();  //排序
        //用户可见的沟通记录列表
        List<CommunicationRecord> recordList = communicationRecordMapper.selectList(
                new QueryWrapper<CommunicationRecord>()
                        .eq("work_order_id", workOrderCommunicationReq.getWorkOrderId())
                        .eq("is_show", 1)
                        .eq(StrUtil.isNotEmpty(vdmCode),"code_flag",vdmCode)
                        .orderByAsc(StrUtil.isNotEmpty(sortWay) && Objects.equals("asc",sortWay),"create_time")
                        .orderByDesc(StrUtil.isNotEmpty(sortWay) && Objects.equals("desc",sortWay),"create_time")
        );
        //封装沟通记录集合
        resultList = initWorkOrderCommunicationRecords(recordList, workOrder);
        //初始化沟通记录
        WorkOrderProblemProfile profile = initWorkOrderRecord(workOrder);
        //沟通记录列表
        map.put("recordList",resultList);
        map.put("problem",profile == null ? "{}" : profile);
        return map;
    }


    /**
     * 初始化工单初始问题描述
     * @param workOrder 工单
     * @return WorkOrderProblemProfile
     */
    public WorkOrderProblemProfile initWorkOrderRecord(WorkOrder workOrder) {
        Account account = accountMapper.selectById(workOrder.getAccountId());
        if (account != null) {
            List<String> list = new ArrayList<>();
            WorkOrderProblemProfile profile = new WorkOrderProblemProfile();
            //头像、昵称
            profile.setHeadPic(account.getHeadPic()).setUsername(account.getUsername());
            //问题概要、问题描述
            profile.setProblemProfile(workOrder.getProblemProfile()).setProblemDescription(workOrder.getProblemDescription());
            //问题截图
            profile.setProblemImages(StrUtil.hasEmpty(workOrder.getProblemDesImage()) ? list : Arrays.asList(workOrder.getProblemDesImage().split(",")));
            //问题附件
            profile.setAttachmentPath(StrUtil.hasEmpty(workOrder.getAttachmentPath()) ? list : Arrays.asList(workOrder.getAttachmentPath().split(",")));
            //VDM标签
            profile.setVDMCode(StatusCodeUtil.VDM_CUSTOMER_PROBLEM_DESC);
            //错误代码
            profile.setErrorCode(StrUtil.hasBlank(workOrder.getErrorCode()) ? "" : workOrder.getErrorCode());
            return profile;
        }
        return null;
    }


    /**
     * 初始化工单沟通记录集合
     * @param recordList 原沟通记录
     * @param workOrder 工单
     * @return list
     */
    public List<CommunicationResult> initWorkOrderCommunicationRecords(List<CommunicationRecord> recordList,WorkOrder workOrder) {
        //返回结果数组
        List<CommunicationResult> resultList = new ArrayList<>();
        if (recordList.size() > 0 && recordList != null) {
            //空数组，内容图片空数组
            List<String> descImageList = new ArrayList<>();
            CommunicationResult result = null;
            for (CommunicationRecord record : recordList) {
                result = new CommunicationResult();
                if (record.getAccountId() != null && record.getAccountId().longValue() == workOrder.getAccountId().longValue()) {
                    //如果当前工单的用户id与当前沟通记录的发送者id相等。那么表示用户
                    Account account = accountMapper.selectById(record.getAccountId());
                    //用户头像、昵称
                    result.setHeadPic(StrUtil.hasEmpty(account.getHeadPic()) ? "" : account.getHeadPic()).setUsername(StrUtil.hasEmpty(account.getUsername()) ? "" : account.getUsername());
                    //工程师是否可以编辑沟通记录
                    result.setIsEdit(false).setIsEngineerRecord(false);
                } else {
                    //否则表示工程师、工程师是否可以编辑沟通记录
                    result.setHeadPic(StatusCodeUtil.ENGINEER_DEFAULT_HEADPIC).setUsername("海量数据技术支持").setIsEdit(true).setIsEngineerRecord(true);
                }
                //工单的错误代码
                result.setErrorCode(StrUtil.hasBlank(workOrder.getErrorCode()) ? "" : workOrder.getErrorCode());
                //沟通记录id、发送时间、发送内容、VDM标签
                result.setRecordId(record.getId().toString()).setCreateTime(record.getCreateTime().substring(0,16)).setContent(record.getContent()).setVDMCode(StrUtil.hasEmpty(record.getCodeFlag()) ? "" : record.getCodeFlag());
                //发送反馈内同截图列表、用户是否可见
                result.setDescImage(StrUtil.hasEmpty(record.getDescImage()) ? descImageList : Arrays.asList(record.getDescImage().split(","))).setIsShow(record.getIsShow() == 1 ? true : false);
                resultList.add(result);
            }
        }
        return resultList;
    }


    /**
     * 工程师查询工单记录
     * @param workOrderCommunicationReq 筛选条件
     * @return map
     */
    @Override
    public Map<String, Object> engineerQueryWorkOrderCommunicationRecord(WorkOrderCommunicationReq workOrderCommunicationReq) {
        Map<String, Object> map = new HashMap<>();
        WorkOrder workOrder = workOrderMapper.selectById(workOrderCommunicationReq.getWorkOrderId());
        //用户问题描述
        WorkOrderProblemProfile profile = initWorkOrderRecord(workOrder);
        //工单沟通记录
        List<CommunicationRecord> records = communicationRecordMapper.selectList(new QueryWrapper<CommunicationRecord>()
                        .eq("work_order_id", workOrder.getId())
                        .orderByAsc(StrUtil.isNotEmpty(workOrderCommunicationReq.getSortWay()) && Objects.equals("asc", workOrderCommunicationReq.getSortWay()), "create_time")
                        .orderByDesc(StrUtil.isNotEmpty(workOrderCommunicationReq.getSortWay()) && Objects.equals("desc", workOrderCommunicationReq.getSortWay()), "create_time"));
        //结果集合
        List<CommunicationResult> resultList = initWorkOrderCommunicationRecords(records, workOrder);
        //返回对象
        map.put("recordList",resultList);
        map.put("problem",profile == null ? "{}" : profile);
        return map;
    }


    /**
     * 工程师修改沟通记录可见状态
     * @param changeCommunicationRecordShowStatusReq 修改沟通记录可见状态
     * @return int
     */
    @Override
    public int engineerChangeRecordShowStatus(ChangeCommunicationRecordShowStatusReq changeCommunicationRecordShowStatusReq) {
        CommunicationRecord record = communicationRecordMapper.selectById(changeCommunicationRecordShowStatusReq.getRecordId());
        if (record != null) {
            record.setIsShow(changeCommunicationRecordShowStatusReq.getIsShowStatus());
            return communicationRecordMapper.updateById(record);
        }
        return 0;
    }


    /**
     * 工程师编辑沟通记录内容
     * @param changeCommunicationRecordContentReq 编辑对象
     * @return int
     */
    @Override
    public int engineerChangeRecordContent(ChangeCommunicationRecordContentReq changeCommunicationRecordContentReq) {
        CommunicationRecord record = communicationRecordMapper.selectById(changeCommunicationRecordContentReq.getRecordId());
        if (record != null) {
            record.setContent(changeCommunicationRecordContentReq.getContent());
            return communicationRecordMapper.updateById(record);
        }
        return 0;
    }


    /**
     * 添加反馈-普通反馈
     * @param addNormalFeedbackReq 反馈结果
     * @return int
     */
    @Override
    public int createCommunicationRecord(AddNormalFeedbackReq addNormalFeedbackReq) {
        WorkOrder workOrder = workOrderMapper.selectById(addNormalFeedbackReq.getWorkOrderId());
        if (workOrder != null) {
            //更新工单最后更新时间
            workOrder.setLastUpdateTime(DateUtil.now());
            workOrderMapper.updateById(workOrder);
            //添加反馈
            CommunicationRecord record = new CommunicationRecord();
            record.setWorkOrderId(workOrder.getId()).setEngineerId(workOrder.getEngineerId()).setCodeFlag(null).setIsShow(1).setContent(addNormalFeedbackReq.getContent()).setDescImage(addNormalFeedbackReq.getDescImage());
            return communicationRecordMapper.insert(record);
        }
        return 0;
    }


    /**
     * 添加VDM流程反馈
     * @param addVDMFeedbackReq 添加对象
     * @return int
     */
    @Override
    public int createCommunicationRecordVDM(AddVDMFeedbackReq addVDMFeedbackReq) {
        WorkOrder workOrder = workOrderMapper.selectById(addVDMFeedbackReq.getWorkOrderId());
        if (workOrder != null) {
            //工单id、工程师id、VDM标签、内容、用户是否可见、问题图片
            CommunicationRecord record = new CommunicationRecord().setWorkOrderId(workOrder.getId()).setEngineerId(workOrder.getEngineerId()).setCodeFlag(StrUtil.isNotEmpty(addVDMFeedbackReq.getVDMCode()) ? "VDM" + addVDMFeedbackReq.getVDMCode() : "").setIsShow(1).setContent(addVDMFeedbackReq.getContent()).setDescImage(addVDMFeedbackReq.getDescImage());
            int row = communicationRecordMapper.insert(record);
            if (row > 0) {
                //更新工单处理流程
                workOrder.setCurrentProcess(addVDMFeedbackReq.getCurrentProcess()).setLastUpdateTime(DateUtil.now());
                workOrderMapper.updateById(workOrder);
            }
            return row;
        }
        return 0;
    }


    /**
     * 修改转单后的工程师id到沟通记录
     * @param workOrderId 工单id
     * @param engineerId 转单后的工程师id
     * @return int
     */
    @Override
    public void changeCommunicationRecordEngineerID(String workOrderId, String engineerId) {
        //当前工单的沟通记录
        List<CommunicationRecord> communicationRecordList = communicationRecordMapper.selectList(new QueryWrapper<CommunicationRecord>().eq("work_order_id", workOrderId));
        if (communicationRecordList.size() > 0 && communicationRecordList != null) {
            for (CommunicationRecord record : communicationRecordList) {
                if (record.getEngineerId() != null) {
                    record.setEngineerId(Convert.toLong(engineerId));
                    communicationRecordMapper.updateById(record);
                }
            }
        }
    }


}
