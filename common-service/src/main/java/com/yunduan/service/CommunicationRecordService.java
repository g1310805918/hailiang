package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CommunicationRecord;
import com.yunduan.request.front.servicerequest.*;

import java.util.Map;

public interface CommunicationRecordService extends IService<CommunicationRecord> {


    /**
     * 查询工单沟通记录
     * @param workOrderCommunicationReq 筛选条件
     * @return list
     */
    Map<String,Object> queryWorkOrderCommunicationRecord(WorkOrderCommunicationReq workOrderCommunicationReq);


    /**
     * 工程师查询工单记录
     * @param workOrderCommunicationReq 筛选条件
     * @return map
     */
    Map<String,Object> engineerQueryWorkOrderCommunicationRecord(WorkOrderCommunicationReq workOrderCommunicationReq);


    /**
     * 工程师修改沟通记录可见状态
     * @param changeCommunicationRecordShowStatusReq 修改沟通记录可见状态
     * @return int
     */
    int engineerChangeRecordShowStatus(ChangeCommunicationRecordShowStatusReq changeCommunicationRecordShowStatusReq);


    /**
     * 工程师编辑沟通记录内容
     * @param changeCommunicationRecordContentReq 编辑对象
     * @return int
     */
    int engineerChangeRecordContent(ChangeCommunicationRecordContentReq changeCommunicationRecordContentReq);


    /**
     * 添加反馈-普通反馈
     * @param addNormalFeedbackReq 反馈结果
     * @return int
     */
    int createCommunicationRecord(AddNormalFeedbackReq addNormalFeedbackReq);


    /**
     * 添加VDM流程反馈
     * @param addVDMFeedbackReq 添加对象
     * @return int
     */
    int createCommunicationRecordVDM(AddVDMFeedbackReq addVDMFeedbackReq);


    /**
     * 修改转单后的工程师id到沟通记录
     * @param workOrderId 工单id
     * @param engineerId 转单后的工程师id
     * @return int
     */
    void changeCommunicationRecordEngineerID(String workOrderId,String engineerId);
}
