package com.yunduan.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.SysDictionary;
import com.yunduan.entity.WorkOrder;
import com.yunduan.request.front.servicerequest.*;
import com.yunduan.service.*;
import com.yunduan.utils.*;
import com.yunduan.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Api(tags = {"服务请求接口"})
@RequestMapping("/api/index/")
public class ServiceRequestController {

    private static final transient Logger log = LoggerFactory.getLogger(ServiceRequestController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private BindingAccountCSIService bindingAccountCSIService;
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private KnowledgeDocumentOneCategoryService knowledgeDocumentOneCategoryService;
    @Autowired
    private KnowledgeDocumentThreeCategoryService knowledgeDocumentThreeCategoryService;
    @Autowired
    private SysDictionaryService sysDictionaryService;
    @Autowired
    private CommunicationRecordService communicationRecordService;
    @Autowired
    private CollectionAccountService collectionAccountService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private CollectionEngineerService collectionEngineerService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;
    @Autowired
    private SendMessageUtil sendMessageUtil;
    @Autowired
    private DistributionUtil distributionUtil;


    @GetMapping("/company-work-order-statistical")
    @ApiOperation(httpMethod = "GET", value = "用户-首页公司订单统计")
    public ResultUtil<Map<String, Integer>> companyWorkOrderStatistical() {
        Map<String, Integer> resultMap = workOrderService.queryWorkOrderStatistical(ContextUtil.getUserId().toString());
        return resultUtil.AesJSONSuccess("SUCCESS", resultMap);
    }


    @GetMapping("/account-work-order-list")
    @ApiOperation(httpMethod = "GET", value = "用户工单列表")
    public ResultUtil<Map<String, Object>> accountWorkOrderList(WorkOrderReq workOrderReq) {
        workOrderReq = AESUtil.decryptToObj(workOrderReq.getData(), WorkOrderReq.class);
        Map<String, Object> map = workOrderService.queryCompanyWorkOrderList(workOrderReq);
        return resultUtil.AesJSONSuccess("SUCCESS", map);
    }


    @GetMapping("/get-customer-service-no")
    @ApiOperation(httpMethod = "GET", value = "获取客户服务号列表")
    public ResultUtil<List<CustomerServiceNoVo>> getCustomerServiceNo() {
        List<CustomerServiceNoVo> voList = bindingAccountCSIService.queryCustomerServiceList(ContextUtil.getUserId().toString());
        return resultUtil.AesJSONSuccess("SUCCESS", voList);
    }


    @GetMapping("/get-product-name-version")
    @ApiOperation(httpMethod = "GET", value = "获取产品名称、版本信息")
    public ResultUtil<List<ProductNameVersionVo>> getProductNameVersion() {
        List<ProductNameVersionVo> voList = knowledgeDocumentOneCategoryService.queryBeginOneTwoLevelCategoryList();
        return resultUtil.AesJSONSuccess("SUCCESS", voList);
    }


    @GetMapping("/get-product-name-version-problem-category/{twoCategoryId}")
    @ApiOperation(httpMethod = "GET", value = "获取问题类型")
    public ResultUtil<List<ProductNameVersionThreeVo>> getProductNameVersionProblemCategory(@PathVariable("twoCategoryId") String twoCategoryId) {
        if (StrUtil.hasEmpty(twoCategoryId)) {
            log.error("获取问题类型【twoCategoryId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        List<ProductNameVersionThreeVo> voList = knowledgeDocumentThreeCategoryService.queryThreeLevelCategoryVo(twoCategoryId);
        return resultUtil.AesJSONSuccess("SUCCESS", voList);
    }


    @GetMapping("/get-hardware-system-deployment")
    @ApiOperation(httpMethod = "GET", value = "获取硬件平台、操作系统、部署方式")
    public ResultUtil<List<SysDictionaryListVo>> getHardwareSystemDeployment(HardwareSystemDeploymentReq hardwareSystemDeploymentReq) {
        hardwareSystemDeploymentReq = AESUtil.decryptToObj(hardwareSystemDeploymentReq.getData(), HardwareSystemDeploymentReq.class);
        List<SysDictionaryListVo> voList = sysDictionaryService.queryOneLevelSysDictionary(hardwareSystemDeploymentReq.getCodeName());
        return resultUtil.AesJSONSuccess("SUCCESS", voList);
    }


    @GetMapping("/get-system-version/{id}")
    @ApiOperation(httpMethod = "GET", value = "获取操作系统-->版本")
    public ResultUtil<List<SystemVersionVo>> getSystemVersion(@PathVariable("id") String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("获取操作系统-->版本【dictionaryId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        List<SystemVersionVo> voList = new ArrayList<>();
        //操作系统对应的版本信息
        List<SysDictionary> list = sysDictionaryService.list(new QueryWrapper<SysDictionary>().eq("code_name", "版本").eq("parent_id", id));
        if (list.size() > 0 && list != null) {
            SystemVersionVo vo = null;
            for (SysDictionary sysDictionary : list) {
                vo = new SystemVersionVo().setId(sysDictionary.getId().toString()).setContent(sysDictionary.getContent());
                voList.add(vo);
            }
        }
        return resultUtil.AesJSONSuccess("SUCCESS", voList);
    }


    @GetMapping("/get-system-version-language/{id}")
    @ApiOperation(httpMethod = "GET", value = "获取操作系统-->版本-->系统语言")
    public ResultUtil<List<SysDictionary>> getSystemVersionLanguage(@PathVariable("id") String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("获取操作系统-->版本-->系统语言【id】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        List<SysDictionary> list = sysDictionaryService.list(new QueryWrapper<SysDictionary>().eq("code_name", "系统语言").eq("parent_id", id));
        return resultUtil.AesJSONSuccess("SUCCESS", list);
    }


    @PostMapping("/submit-work-order")
    @ApiOperation(httpMethod = "POST", value = "提交工单")
    public ResultUtil<String> submitWorkOrder(CreateWorkOrderReq createWorkOrderReq) {
        createWorkOrderReq = AESUtil.decryptToObj(createWorkOrderReq.getData(),CreateWorkOrderReq.class);
        int row = workOrderService.createWorkOrder(createWorkOrderReq);
        return row > 0 ? resultUtil.AesJSONSuccess("提交成功","") : resultUtil.AesFAILError("提交失败");
    }


    @GetMapping("/work-order-detail/{workOrderId}")
    @ApiOperation(httpMethod = "GET",value = "工单详情-基本内容")
    public ResultUtil<WorkOrderDetailBaseInfoVo> workOrderDetail(@PathVariable("workOrderId") String workOrderId) {
        if (StrUtil.hasEmpty(workOrderId)) {
            log.error("【工单详情-基本内容】workOrderId 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        WorkOrderDetailBaseInfoVo infoVo = workOrderService.queryWorkOrderBaseInfo(workOrderId);
        return resultUtil.AesJSONSuccess("SUCCESS",infoVo);
    }


    @GetMapping("/work-order-detail-communication-record")
    @ApiOperation(httpMethod = "GET",value = "工单详情-获取工单沟通记录")
    public ResultUtil<Map<String, Object>> workOrderDetailCommunicationRecord(WorkOrderCommunicationReq workOrderCommunicationReq) {
        workOrderCommunicationReq = AESUtil.decryptToObj(workOrderCommunicationReq.getData(),WorkOrderCommunicationReq.class);
        Map<String, Object> map = communicationRecordService.queryWorkOrderCommunicationRecord(workOrderCommunicationReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/change-work-order-problem-severity")
    @ApiOperation(httpMethod = "POST",value = "调整问题严重等级")
    public ResultUtil<String> changeWorkOrderProblemSeverity(ChangeWorkOrderProblemSeverity changeWorkOrderProblemSeverity) {
        changeWorkOrderProblemSeverity = AESUtil.decryptToObj(changeWorkOrderProblemSeverity.getData(),ChangeWorkOrderProblemSeverity.class);
        int row = workOrderService.changeWorkOrderProblemSeverity(changeWorkOrderProblemSeverity);
        if (row == StatusCodeUtil.NOT_FOUND_FLAG) {
            return resultUtil.AesFAILError("工单不存在");
        }
        return row > 0 ? resultUtil.AesJSONSuccess("修改成功","") : resultUtil.AesFAILError("修改失败");
    }


    @PostMapping("/create-work-order-attach")
    @ApiOperation(httpMethod = "POST",value = "添加工单附件")
    public ResultUtil<String> createWorkOrderAttach(CreateWorkOrderAttachReq createWorkOrderAttachReq) {
        createWorkOrderAttachReq = AESUtil.decryptToObj(createWorkOrderAttachReq.getData(),CreateWorkOrderAttachReq.class);
        int row = workOrderService.addWorkOrderAttach(createWorkOrderAttachReq);
        return row > 0 ? resultUtil.AesJSONSuccess("附件上传成功","") : resultUtil.AesFAILError("附件上传失败");
    }


    @PostMapping("/create-work-order-feedback")
    @ApiOperation(httpMethod = "POST",value = "用户-添加工单反馈")
    public ResultUtil<String> createWorkOrderFeedback(CreateWorkOrderFeedbackReq createWorkOrderFeedbackReq) {
        createWorkOrderFeedbackReq = AESUtil.decryptToObj(createWorkOrderFeedbackReq.getData(),CreateWorkOrderFeedbackReq.class);
        int row = workOrderService.addWorkOrderFeedback(createWorkOrderFeedbackReq);
        return row > 0 ? resultUtil.AesJSONSuccess("反馈成功","") : resultUtil.AesFAILError("反馈失败");
    }


    @PostMapping("/collection-work-order/{workOrderId}")
    @ApiOperation(httpMethod = "POST",value = "收藏工单、取消收藏")
    public ResultUtil<String> collectionWorkOrder(@PathVariable("workOrderId") String workOrderId) {
        if (StrUtil.hasEmpty(workOrderId)) {
            log.error("收藏工单【workOrderId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = collectionAccountService.createCollectionAccount(ContextUtil.getUserId(), Convert.toLong(workOrderId));
        return row == 1 ? resultUtil.AesJSONSuccess("收藏成功","") : row == -1 ? resultUtil.AesFAILError("收藏失败") : row == 2 ? resultUtil.AesJSONSuccess("取消收藏成功","") : resultUtil.AesFAILError("取消失败");
    }


    @GetMapping("/get-work-order-upgrade-reason/{workOrderId}")
    @ApiOperation(httpMethod = "GET",value = "查看工单升级原因")
    public ResultUtil<String> getWorkOrderUpgradeReason(@PathVariable("workOrderId") String workOrderId) {
        if (StrUtil.hasEmpty(workOrderId)) {
            log.error("查看工单升级原因【workOrderId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        WorkOrder order = workOrderService.getById(workOrderId);
        return resultUtil.AesJSONSuccess("SUCCESS",StrUtil.hasEmpty(order.getUpgradeReason()) ? "" : order.getUpgradeReason());
    }


    @PostMapping("/submit-close-work-order-apply")
    @ApiOperation(httpMethod = "POST",value = "提交关闭工单申请")
    public ResultUtil<String> submitCloseWorkOrderApply(SubmitCloseWorkOrderApplyReq submitCloseWorkOrderApplyReq) {
        submitCloseWorkOrderApplyReq = AESUtil.decryptToObj(submitCloseWorkOrderApplyReq.getData(),SubmitCloseWorkOrderApplyReq.class);
        int row = workOrderService.accountCloseWorkOrder(submitCloseWorkOrderApplyReq);
        return row > 0 ? resultUtil.AesJSONSuccess("申请提交成功","") : resultUtil.AesFAILError("申请提交失败");
    }


    @GetMapping("/get-engineer-statistical")
    @ApiOperation(httpMethod = "GET",value = "工程师-获取首页统计数据")
    public ResultUtil<Map<String, Integer>> getEngineerStatistical() {
        Map<String, Integer> map = workOrderService.queryEngineerWorkOrderStatistical(ContextUtil.getUserId().toString());
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/get-others-engineer")
    @ApiOperation(httpMethod = "GET",value = "获取全部受理人【排除自己的所有其他工程师】")
    public ResultUtil<List<OtherEngineerListVo>> getOtherEngineer() {
        List<OtherEngineerListVo> voList = engineerService.queryOtherEngineers(ContextUtil.getUserId().toString());
        return resultUtil.AesJSONSuccess("SUCCESS",voList);
    }


    @GetMapping("/engineer-index-init")
    @ApiOperation(httpMethod = "GET",value = "工程师-工单列表")
    public ResultUtil<Map<String, Object>> engineerIndexInit(EngineerIndexInitReq engineerIndexInitReq) {
        engineerIndexInitReq = AESUtil.decryptToObj(engineerIndexInitReq.getData(),EngineerIndexInitReq.class);
        Map<String, Object> map = workOrderService.queryEngineerIndexInit(engineerIndexInitReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/engineer-collection-work-order/{workOrderId}")
    @ApiOperation(httpMethod = "POST",value = "工程师收藏工单")
    public ResultUtil<String> engineerCollectionWorkOrder(@PathVariable("workOrderId") String workOrderId) {
        if (StrUtil.hasEmpty(workOrderId)) {
            log.error("工程师收藏工单【workOrderId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = collectionEngineerService.createCollectionEngineer(ContextUtil.getUserId(), Convert.toLong(workOrderId));
        return row == 1 ? resultUtil.AesJSONSuccess("收藏成功","") : row == -1 ? resultUtil.AesFAILError("收藏失败") : row == 2 ? resultUtil.AesJSONSuccess("取消收藏成功","") : resultUtil.AesFAILError("取消失败");
    }

    @PostMapping("/engineer-change-online-status/{status}")
    @ApiOperation(httpMethod = "POST",value = "修改工程师在线状态【1在线、2离线】")
    public ResultUtil<String> engineerChangeOnlineStatus(@PathVariable("status") String status) {
        if (StrUtil.hasEmpty(status)) {
            log.error("修改工程师在线状态【status】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        boolean flag = false;
        Engineer engineer = engineerService.getById(ContextUtil.getUserId());
        if (engineer != null) {
            engineer.setOnlineStatus(Objects.equals("1",status) ? StatusCodeUtil.ENGINEER_ACCOUNT_ONLINE_STATUS : StatusCodeUtil.ENGINEER_ACCOUNT_OFFLINE_STATUS);
            flag = engineerService.updateById(engineer);
        }
        return flag ? resultUtil.AesJSONSuccess("个人状态更新成功","") : resultUtil.AesFAILError("个人状态更新失败");
    }


    @GetMapping("/engineer-get-work-order-base-info/{workOrderId}")
    @ApiOperation(httpMethod = "GET",value = "工程师获取工单基本信息")
    public ResultUtil<EngineerWorkOrderBaseInfoVo> engineerGetWorkOrderBaseInfo(@PathVariable("workOrderId") String workOrderId) {
        if (StrUtil.hasEmpty(workOrderId)) {
            log.error("工程师获取工单基本信息【workOrderId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        EngineerWorkOrderBaseInfoVo vo = workOrderService.engineerQueryWorkOrderBaseInfo(workOrderId);
        return vo != null ? resultUtil.AesJSONSuccess("SUCCESS",vo) : resultUtil.AesFAILError("网络错误");
    }


    @GetMapping("/engineer-get-work-order-communication-record")
    @ApiOperation(httpMethod = "GET",value = "工程师获取工单沟通记录")
    public ResultUtil<Map<String, Object>> engineerGetWorkOrderCommunicationRecord(WorkOrderCommunicationReq workOrderCommunicationReq) {
        workOrderCommunicationReq = AESUtil.decryptToObj(workOrderCommunicationReq.getData(),WorkOrderCommunicationReq.class);
        Map<String, Object> map = communicationRecordService.engineerQueryWorkOrderCommunicationRecord(workOrderCommunicationReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @PostMapping("/engineer-change-communication-record-show-status")
    @ApiOperation(httpMethod = "POST",value = "工程师修改沟通记录可见状态")
    public ResultUtil<String> engineerChangeCommunicationRecordShowStatus(ChangeCommunicationRecordShowStatusReq changeCommunicationRecordShowStatusReq) {
        changeCommunicationRecordShowStatusReq = AESUtil.decryptToObj(changeCommunicationRecordShowStatusReq.getData(),ChangeCommunicationRecordShowStatusReq.class);
        int row = communicationRecordService.engineerChangeRecordShowStatus(changeCommunicationRecordShowStatusReq);
        return row > 0 ? resultUtil.AesJSONSuccess("修改成功","") : resultUtil.AesFAILError("修改失败");
    }


    @PostMapping("/engineer-change-communication-record-content")
    @ApiOperation(httpMethod = "POST",value = "工程师编辑沟通记录")
    public ResultUtil<String> engineerChangeCommunicationRecordContent (ChangeCommunicationRecordContentReq changeCommunicationRecordContentReq) {
        changeCommunicationRecordContentReq = AESUtil.decryptToObj(changeCommunicationRecordContentReq.getData(),ChangeCommunicationRecordContentReq.class);
        int row = communicationRecordService.engineerChangeRecordContent(changeCommunicationRecordContentReq);
        return row > 0 ? resultUtil.AesJSONSuccess("修改成功","") : resultUtil.AesFAILError("修改失败");
    }


    @PostMapping("/engineer-add-knowledge-document")
    @ApiOperation(httpMethod = "GET",value = "工程师关联工单【知识文档、bug文档】")
    public ResultUtil<String> engineerAddKnowledgeDocument(AddKnowledgeDocReq addKnowledgeDocReq) {
        addKnowledgeDocReq = AESUtil.decryptToObj(addKnowledgeDocReq.getData(),AddKnowledgeDocReq.class);
        int row = workOrderService.joinWorkOrderDocumentInfo(addKnowledgeDocReq);
        if (row == StatusCodeUtil.DOCUMENT_NOT_EXIST) {
            return resultUtil.AesFAILError("文档不存在，请检查编号是否正确！");
        }
        return row > 0 ? resultUtil.AesJSONSuccess("添加成功","") : resultUtil.AesFAILError("添加失败");
    }


    @PostMapping("/engineer-add-attachment-path")
    @ApiOperation(httpMethod = "POST",value = "工程师关联【相关资料链接、相关资料附件】")
    public ResultUtil<String> engineerAddAttachmentPath(AddRelatedLinksReq addRelatedLinksReq) {
        addRelatedLinksReq = AESUtil.decryptToObj(addRelatedLinksReq.getData(),AddRelatedLinksReq.class);
        int row = workOrderService.relatedLinks(addRelatedLinksReq);
        return row > 0 ? resultUtil.AesJSONSuccess("添加成功","") : resultUtil.AesFAILError("添加失败");
    }


    @PostMapping("/engineer-change-model")
    @ApiOperation(httpMethod = "POST",value = "工程师模块更改")
    public ResultUtil<String> changeModel(ChangeModelReq changeModelReq) {
        changeModelReq = AESUtil.decryptToObj(changeModelReq.getData(),ChangeModelReq.class);
        WorkOrder one = workOrderService.getById(changeModelReq.getWorkOrderId());
        boolean flag = false;
        if (one != null) {
            one.setProductNameVersion(changeModelReq.getProductNameVersion()).setProblemType(changeModelReq.getProductType());
            flag = workOrderService.updateById(one);
        }
        return flag ? resultUtil.AesJSONSuccess("更改成功","") : resultUtil.AesFAILError("更改失败");
    }


    @PostMapping("/engineer-add-order-feedback-normal")
    @ApiOperation(httpMethod = "POST",value = "添加普通工单反馈")
    public ResultUtil<String> engineerAddOrderFeedbackNormal(AddNormalFeedbackReq addNormalFeedbackReq) {
        addNormalFeedbackReq = AESUtil.decryptToObj(addNormalFeedbackReq.getData(),AddNormalFeedbackReq.class);
        int row = communicationRecordService.createCommunicationRecord(addNormalFeedbackReq);
        return row > 0 ? resultUtil.AesJSONSuccess("反馈成功","") : resultUtil.AesFAILError("反馈失败");
    }


    @PostMapping("/engineer-add-order-feedback-vdm")
    @ApiOperation(httpMethod = "POST",value = "添加VDM流程反馈")
    public ResultUtil<String> engineerAddOrderFeedbackVDM(AddVDMFeedbackReq addVDMFeedbackReq) {
        addVDMFeedbackReq = AESUtil.decryptToObj(addVDMFeedbackReq.getData(),AddVDMFeedbackReq.class);
        int row = communicationRecordService.createCommunicationRecordVDM(addVDMFeedbackReq);
        return row > 0 ? resultUtil.AesJSONSuccess("反馈成功","") : resultUtil.AesFAILError("反馈失败");
    }



    @PostMapping("/engineer-transfer-order")
    @ApiOperation(httpMethod = "POST",value = "工程师-工单转单")
    public ResultUtil<String> engineerTransferOrder(TransferOrderReq transferOrderReq) {
        transferOrderReq = AESUtil.decryptToObj(transferOrderReq.getData(),TransferOrderReq.class);
        WorkOrder workOrder = workOrderService.getById(transferOrderReq.getWorkOrderId());
        if (workOrder == null || StrUtil.hasEmpty(transferOrderReq.getEngineerId())) {
            log.error("【工程师-工单转单】工单不存在 或者 接收工单的工程师id为空");
            return resultUtil.AesFAILError("非法请求");
        }
        workOrder.setEngineerId(StrUtil.hasEmpty(transferOrderReq.getEngineerId()) ? workOrder.getEngineerId() : Convert.toLong(transferOrderReq.getEngineerId()));
        boolean flag = workOrderService.updateById(workOrder);
        if (flag) {
            //更新该工单沟通记录的工程师id为转单后的工程师id
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    communicationRecordService.changeCommunicationRecordEngineerID(workOrder.getId().toString(),workOrder.getEngineerId().toString());
                    //发送消息通知接受工单的工程师
                    sendMessageUtil.sendWorkOrderMessage(workOrder);
                }
            });
        }
        return flag ? resultUtil.AesJSONSuccess("转单成功","") : resultUtil.AesFAILError("转单失败");
    }


    @PostMapping("/engineer-close-work-order")
    @ApiOperation(httpMethod = "POST",value = "工程师关闭工单")
    public ResultUtil<String> engineerCloseWorkOrder(CloseWorkOrderReq closeWorkOrderReq) {
        closeWorkOrderReq = AESUtil.decryptToObj(closeWorkOrderReq.getData(),CloseWorkOrderReq.class);
        int row = workOrderService.closeWorkOrder(closeWorkOrderReq);
        return row > 0 ? resultUtil.AesJSONSuccess("关闭成功","") : resultUtil.AesFAILError("关闭失败");
    }


    @PostMapping("/open-again")
    @ApiOperation(httpMethod = "POST",value = "用户重开工单")
    public ResultUtil<String> openAgain(OpenAgainReq openAgainReq) {
        openAgainReq = AESUtil.decryptToObj(openAgainReq.getData(),OpenAgainReq.class);
        WorkOrder workOrder = workOrderService.getById(openAgainReq.getWorkOrderId());
        boolean flag = false;
        if (workOrder != null) {
            //重开原因、重开描述、工单处理中状态
            workOrder.setOpenAgainReason(openAgainReq.getOpenAgainReason()).setOpenAgainDesc(openAgainReq.getOpenAgainDesc()).setStatus(StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS);
            flag = workOrderService.updateById(workOrder);
        }
        return flag ? resultUtil.AesJSONSuccess("重开成功","") : resultUtil.AesFAILError("重开失败");
    }

    @PostMapping("/engineer-open-again")
    @ApiOperation(httpMethod = "POST",value = "工程师重开工单")
    public ResultUtil<String> engineerOpenAgain(OpenAgainReq openAgainReq) {
        openAgainReq = AESUtil.decryptToObj(openAgainReq.getData(),OpenAgainReq.class);
        WorkOrder workOrder = workOrderService.getById(openAgainReq.getWorkOrderId());
        boolean flag = false;
        if (workOrder != null) {
            //重开原因、重开描述、工单处理中状态
            workOrder.setEngineerOpenAgainReason(openAgainReq.getOpenAgainReason()).setEngineerOpenAgainDesc(openAgainReq.getOpenAgainDesc()).setStatus(StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS);
            flag = workOrderService.updateById(workOrder);
        }
        return flag ? resultUtil.AesJSONSuccess("重开成功","") : resultUtil.AesFAILError("重开失败");
    }


    @PostMapping("/engineer-remove-related-document")
    @ApiOperation(httpMethod = "POST",value = "工程师删除相关【知识文档、bug文档】")
    public ResultUtil<String> engineerRemoveRelatedDocument(EngineerRemoveRelatedDocumentReq engineerRemoveRelatedDocumentReq) {
        engineerRemoveRelatedDocumentReq = AESUtil.decryptToObj(engineerRemoveRelatedDocumentReq.getData(),EngineerRemoveRelatedDocumentReq.class);
        int row = workOrderService.engineerRemoveRelated(engineerRemoveRelatedDocumentReq);
        return row > 0 ? resultUtil.AesJSONSuccess("删除成功","") : resultUtil.AesFAILError("删除失败");
    }


    @GetMapping("/engineer-dynamic-search-document")
    @ApiOperation(httpMethod = "GET",value = "KM文档管理动态搜索文档")
    public ResultUtil<List<DocumentListVo>> engineerDynamicSearchDocument(DynamicSearchDocumentReq dynamicSearchDocumentReq) {
        dynamicSearchDocumentReq = AESUtil.decryptToObj(dynamicSearchDocumentReq.getData(),DynamicSearchDocumentReq.class);
        List<DocumentListVo> voList = knowledgeDocumentService.dynamicDocumentList(dynamicSearchDocumentReq);
        return resultUtil.AesJSONSuccess("SUCCESS",voList);
    }


    @PostMapping("/engineer-related-document")
    @ApiOperation(httpMethod = "POST",value = "工程师提交文档管理")
    public ResultUtil engineerRelatedDocument(SubmitKMDocumentReq submitKMDocumentReq) {
        submitKMDocumentReq = AESUtil.decryptToObj(submitKMDocumentReq.getData(),SubmitKMDocumentReq.class);
        int row = workOrderService.submitKMDocument(submitKMDocumentReq);
        return row > 0 ? resultUtil.AesJSONSuccess("提交成功","") : resultUtil.AesFAILError("提交失败");
    }


    @PostMapping("/engineer-sys-auto/{workOrderId}")
    @ApiOperation(httpMethod = "POST",value = "工程师重新分配工单【放回系统自动分配】")
    public ResultUtil<String> engineerSysAuto(@PathVariable String workOrderId) {
        if (StrUtil.hasEmpty(workOrderId)) {
            log.error("工程师重新分配工单【放回系统自动分配】工单id为空");
            return resultUtil.AesFAILError("非法请求");
        }
        //以防万一、更新这个工单状态为待处理状态。这样就会被系统定时任务扫描到
        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (workOrder == null) {
            return resultUtil.AesFAILError("非法请求");
        }
        workOrder.setStatus(StatusCodeUtil.WORK_ORDER_PROCESS_STATUS);
        workOrderService.updateById(workOrder);
        //另起线程执行分配任务，直接返回结果
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //直接调用分配，如果没有分配成功、那么后续会由定时任务来分配
                distributionUtil.autoDistributionWorkOrderToEngineer(workOrderId);
            }
        });
        return resultUtil.AesJSONSuccess("已将工单放回系统，等待分配。","");
    }


    @PostMapping("/engineer-split-work-order")
    @ApiOperation(httpMethod = "POST",value = "工程师分裂工单")
    public ResultUtil<String> engineerSplitWorkOrder(EngineerCreateWorkOrderReq engineerCreateWorkOrderReq) {
        engineerCreateWorkOrderReq = AESUtil.decryptToObj(engineerCreateWorkOrderReq.getData(),EngineerCreateWorkOrderReq.class);
        int row = workOrderService.engineerSplitWorkOrder(engineerCreateWorkOrderReq);
        if (row == StatusCodeUtil.NOT_FOUND_FLAG) {
            return resultUtil.AesFAILError("工单不存在，无法分裂！");
        }
        return row > 0 ? resultUtil.AesJSONSuccess("分裂成功","") : resultUtil.AesFAILError("分裂失败");
    }


    @PostMapping("/engineer-upgrade-work-order")
    @ApiOperation(httpMethod = "POST",value = "工程师升级工单")
    public ResultUtil<String> engineerUpgradeWorkOrder(EngineerUpgradeWorkOrderReq engineerUpgradeWorkOrderReq) {
        engineerUpgradeWorkOrderReq = AESUtil.decryptToObj(engineerUpgradeWorkOrderReq.getData(),EngineerUpgradeWorkOrderReq.class);
        int row = workOrderService.engineerUpgradeWorkOrder(engineerUpgradeWorkOrderReq);
        if (row == StatusCodeUtil.NOT_FOUND_FLAG) {
            return resultUtil.AesFAILError("非法请求");
        }
        return row > 0 ? resultUtil.AesJSONSuccess("提交成功","") : resultUtil.AesFAILError("提交失败");
    }



}
