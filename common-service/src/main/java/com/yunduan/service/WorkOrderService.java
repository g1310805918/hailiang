package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.WorkOrder;
import com.yunduan.request.front.servicerequest.*;
import com.yunduan.vo.EngineerWorkOrderBaseInfoVo;
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


    /**
     * 初始化工程师端工单列表
     * @param engineerIndexInitReq 初始化对象
     * @return map
     */
    Map<String,Object> queryEngineerIndexInit(EngineerIndexInitReq engineerIndexInitReq);


    /**
     * 工程师查看工单基本信息
     * @param workOrderId 工单id
     * @return EngineerWorkOrderBaseInfoVo
     */
    EngineerWorkOrderBaseInfoVo engineerQueryWorkOrderBaseInfo(String workOrderId);


    /**
     * 关联工单文档信息
     * @param addKnowledgeDocReq 关联对象
     * @return int
     */
    int joinWorkOrderDocumentInfo(AddKnowledgeDocReq addKnowledgeDocReq);


    /**
     * 添加相关资料
     * @param addRelatedLinksReq 资料
     * @return int
     */
    int relatedLinks(AddRelatedLinksReq addRelatedLinksReq);


    /**
     * 工程师关闭工单
     * @param closeWorkOrderReq 参数
     * @return int
     */
    int closeWorkOrder(CloseWorkOrderReq closeWorkOrderReq);


    /**
     * 工程师删除关联的文档
     * @param engineerRemoveRelatedDocumentReq 删除的对象
     * @return int
     */
    int engineerRemoveRelated(EngineerRemoveRelatedDocumentReq engineerRemoveRelatedDocumentReq);


    /**
     * 提交文档管理
     * @param submitKMDocumentReq 文档
     * @return int
     */
    int submitKMDocument(SubmitKMDocumentReq submitKMDocumentReq);


    /**
     * 工程师分裂工单
     * @param engineerCreateWorkOrderReq 分裂参数
     * @return int
     */
    int engineerSplitWorkOrder(EngineerCreateWorkOrderReq engineerCreateWorkOrderReq);


    /**
     * 工程师升级工单
     * @param engineerUpgradeWorkOrderReq 升级参数
     * @return int
     */
    int engineerUpgradeWorkOrder(EngineerUpgradeWorkOrderReq engineerUpgradeWorkOrderReq);
}
