package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CommunicationRecord;
import com.yunduan.request.front.servicerequest.WorkOrderCommunicationReq;

import java.util.Map;

public interface CommunicationRecordService extends IService<CommunicationRecord> {


    /**
     * 查询工单沟通记录
     * @param workOrderCommunicationReq 筛选条件
     * @return list
     */
    Map<String,Object> queryWorkOrderCommunicationRecord(WorkOrderCommunicationReq workOrderCommunicationReq);
}
