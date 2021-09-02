package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.WorkOrder;
import com.yunduan.request.front.servicerequest.*;
import com.yunduan.vo.WorkOrderDetailBaseInfoVo;

import java.util.Map;

public interface WorkOrderService extends IService<WorkOrder> {


    /**
     * 查询用户工单系统统计
     * @param accountId 用户id
     * @return map
     */
    Map<String,Integer> queryWorkOrderStatistical(String accountId);


    /**
     * 查询当前用户公司所有订单记录
     * @param workOrderReq 筛选对象
     * @return map
     */
    Map<String,Object> queryCompanyWorkOrderList(WorkOrderReq workOrderReq);


    /**
     * 提交工单
     * @param createWorkOrderReq 添加对象
     * @return int
     */
    int createWorkOrder(CreateWorkOrderReq createWorkOrderReq);


    /**
     * 查询工单基本信息
     * @param workOrderId 工单id
     * @return WorkOrderDetailBaseInfoVo
     */
    WorkOrderDetailBaseInfoVo queryWorkOrderBaseInfo(String workOrderId);


    /**
     * 修改工单严重等级
     * @param changeWorkOrderProblemSeverity 修改对象
     * @return int
     */
    int changeWorkOrderProblemSeverity(ChangeWorkOrderProblemSeverity changeWorkOrderProblemSeverity);


    /**
     * 添加工单附件
     * @param createWorkOrderAttachReq 附件对象
     * @return int
     */
    int addWorkOrderAttach(CreateWorkOrderAttachReq createWorkOrderAttachReq);


    /**
     * 添加工单反馈
     * @param createWorkOrderFeedbackReq 添加对象
     * @return int
     */
    int addWorkOrderFeedback(CreateWorkOrderFeedbackReq createWorkOrderFeedbackReq);


    /**
     * 用户关闭工单申请
     * @param submitCloseWorkOrderApplyReq 原因对象
     * @return int
     */
    int accountCloseWorkOrder(SubmitCloseWorkOrderApplyReq submitCloseWorkOrderApplyReq);


    /**
     * 工程师端首页统计
     * @param engineerId 工程师id
     * @return map
     */
    Map<String,Integer> queryEngineerWorkOrderStatistical(String engineerId);
}
