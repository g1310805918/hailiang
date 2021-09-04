package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CommunicationRecord;
import com.yunduan.request.front.servicerequest.ChangeCommunicationRecordContentReq;
import com.yunduan.request.front.servicerequest.ChangeCommunicationRecordShowStatusReq;
import com.yunduan.request.front.servicerequest.WorkOrderCommunicationReq;

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
}
