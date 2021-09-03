package com.yunduan.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.SysDictionary;
import com.yunduan.entity.WorkOrder;
import com.yunduan.request.front.servicerequest.*;
import com.yunduan.service.*;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @ApiOperation(httpMethod = "POST",value = "修改工程师在线状态")
    public ResultUtil<String> engineerChangeOnlineStatus(@PathVariable("status") String status) {
        if (StrUtil.hasEmpty(status)) {
            log.error("修改工程师在线状态【status】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        boolean flag = false;
        Engineer engineer = engineerService.getById(ContextUtil.getUserId());
        if (engineer != null) {
            engineer.setOnlineStatus(Convert.toInt(status));
            flag = engineerService.updateById(engineer);
        }
        return flag ? resultUtil.AesJSONSuccess("个人状态更新成功","") : resultUtil.AesFAILError("个人状态更新失败");
    }


    @GetMapping("/engineer-get-work-order-base-info/{workOrderId}")
    @ApiOperation(httpMethod = "GET",value = "工程师获取工单基本信息")
    public ResultUtil engineerGetWorkOrderBaseInfo(@PathVariable("workOrderId") String workOrderId) {
        if (StrUtil.hasEmpty(workOrderId)) {
            log.error("工程师获取工单基本信息【workOrderId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        EngineerWorkOrderBaseInfoVo vo = workOrderService.engineerQueryWorkOrderBaseInfo(workOrderId);
        return resultUtil;
    }

}
