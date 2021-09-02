package com.yunduan.serviceimpl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Account;
import com.yunduan.entity.CommunicationRecord;
import com.yunduan.entity.WorkOrder;
import com.yunduan.mapper.AccountMapper;
import com.yunduan.mapper.CommunicationRecordMapper;
import com.yunduan.mapper.WorkOrderMapper;
import com.yunduan.request.front.servicerequest.WorkOrderCommunicationReq;
import com.yunduan.service.CommunicationRecordService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.CommunicationResult;
import com.yunduan.vo.WorkOrderProblemProfile;
import org.springframework.beans.factory.annotation.Autowired;
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
        //空数组，内容图片空数组
        List<String> descImageList = new ArrayList<>();
        if (recordList.size() > 0 && recordList != null) {
            CommunicationResult result = null;
            for (CommunicationRecord record : recordList) {
                result = new CommunicationResult();
                if (record.getAccountId() != null && record.getAccountId().longValue() == workOrder.getAccountId().longValue()) {
                    //如果当前工单的用户id与当前沟通记录的发送者id相等。那么表示用户
                    Account account = accountMapper.selectById(record.getAccountId());
                    //用户头像、昵称
                    result.setHeadPic(StrUtil.hasEmpty(account.getHeadPic()) ? "" : account.getHeadPic()).setUsername(StrUtil.hasEmpty(account.getUsername()) ? "" : account.getUsername());
                }else {
                    //否则表示工程师
                    result.setHeadPic("LOGO").setUsername("海量数据技术支持");
                }
                //发送时间、发送内容、VDM标签
                result.setCreateTime(record.getCreateTime().substring(0,16)).setContent(record.getContent()).setVDMCode(record.getCodeFlag());
                //发送反馈内同截图列表
                result.setDescImage(StrUtil.hasEmpty(record.getDescImage()) ? descImageList : Arrays.asList(record.getDescImage().split(",")));
                resultList.add(result);
            }
        }
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
            profile.setAttachmentPath(StrUtil.hasEmpty(workOrder.getProblemDesImage()) ? "" : Arrays.asList(workOrder.getAttachmentPath().split(",")).get(0));
            //VDM标签
            profile.setVDMCode(StatusCodeUtil.VDM_CUSTOMER_PROBLEM_DESC);
            return profile;
        }
        return null;
    }


}
