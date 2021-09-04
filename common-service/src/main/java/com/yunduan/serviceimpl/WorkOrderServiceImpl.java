package com.yunduan.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.*;
import com.yunduan.mapper.*;
import com.yunduan.request.front.servicerequest.*;
import com.yunduan.service.KnowledgeDocumentService;
import com.yunduan.service.WorkOrderService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.*;


@Service
@Transactional
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired
    private BindingAccountCSIMapper bindingAccountCSIMapper;
    @Autowired
    private CompanyCSIMapper companyCSIMapper;
    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;
    @Autowired
    private CommunicationRecordMapper communicationRecordMapper;
    @Autowired
    private CollectionAccountMapper collectionAccountMapper;
    @Autowired
    private EngineerMapper engineerMapper;
    @Autowired
    private CollectionEngineerMapper collectionEngineerMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryMapper threeCategoryMapper;


    /**
     * 查询用户工单系统统计
     * @param accountId 用户id
     * @return map
     */
    @Override
    public Map<String, Integer> queryWorkOrderStatistical(String accountId) {
        Map<String, Integer> resultMap = new HashMap<>();
        //用户绑定的所有CSI编号集合
        Set<String> CSINumberSet = new TreeSet<>();
        //用户绑定的CSI编号记录
        List<AccountBindingCSI> bindingRecord = bindingAccountCSIMapper.selectAccountBindingRecord(accountId);
        if (bindingRecord.size() > 0 && bindingRecord != null) {
            for (AccountBindingCSI bindingCSI : bindingRecord) {
                CSINumberSet.add(bindingCSI.getCsiNumber());
            }
        }
        //用户绑定的CSI记录为空，那么直接返回
        if (CSINumberSet.isEmpty()) {
            //公司总计受理中的订单
            resultMap.put("companyTotalAcceptWorkOrderCount",0);
            //我受理中的工单
            resultMap.put("myAcceptWorkOrderCount",0);
            //我已完结的工单
            resultMap.put("myCloseWorkOrderCount",0);
            //我收藏的工单
            resultMap.put("myCollectionWorkOrderCount",0);
            return resultMap;
        }
        //公司总计受理中的订单
        Integer companyTotalAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("status", 2).in("csi_number", CSINumberSet));
        //我受理中的工单
        Integer myAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("account_id", ContextUtil.getUserId()).eq("status", 2));
        //我已完结的工单
        Integer myCloseWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("account_id", ContextUtil.getUserId()).eq("status", 4));
        //我收藏的工单
        Integer myCollectionWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("account_id", ContextUtil.getUserId()).eq("is_collection", 1));

        resultMap.put("companyTotalAcceptWorkOrderCount",companyTotalAcceptWorkOrderCount);
        resultMap.put("myAcceptWorkOrderCount",myAcceptWorkOrderCount);
        resultMap.put("myCloseWorkOrderCount",myCloseWorkOrderCount);
        resultMap.put("myCollectionWorkOrderCount",myCollectionWorkOrderCount);
        return resultMap;
    }


    /**
     * 查询当前用户公司所有订单记录
     * @param workOrderReq 筛选对象
     * @return map
     */
    @Override
    public Map<String, Object> queryCompanyWorkOrderList(WorkOrderReq workOrderReq) {
        Map<String, Object> map = new HashMap<>();
        //用户收藏的所有工单id集合
        List<Long> collectionWorkOrderIdList = new ArrayList<>();
        if (workOrderReq.getMyCollection()) {
            List<CollectionAccount> collectionWokOrderIds = collectionAccountMapper.selectList(new QueryWrapper<CollectionAccount>().eq("account_id", ContextUtil.getUserId()));
            if (!CollectionUtils.isEmpty(collectionWokOrderIds)) {
                collectionWokOrderIds.forEach(collectionAccount -> {
                    collectionWorkOrderIdList.add(collectionAccount.getWorkOrderId());
                });
            }
        }
        //通过CSI编号查询公司所有人员工单
        List<WorkOrder> records = workOrderMapper.selectPage(
                new Page<>(workOrderReq.getPageNo(), workOrderReq.getPageSize()),
                new QueryWrapper<WorkOrder>()
                        //技术工单或非技术工单
                        .eq("type", workOrderReq.getWorkOrderType())
                        //我收藏选中时
                        .in(collectionWorkOrderIdList.size() > 0 ,"id",collectionWorkOrderIdList)
                        //客户服务号
                        .in((workOrderReq.getCustomerCSINumber() != null && workOrderReq.getCustomerCSINumber().size() > 0),"csi_number",workOrderReq.getCustomerCSINumber())
                        //问题概要
                        .like(StrUtil.isNotEmpty(workOrderReq.getProblemProfile()),"problem_profile",workOrderReq.getProblemProfile())
                        //我受理选中时
                        .eq(workOrderReq.getMyAccept(), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                        .eq(workOrderReq.getMyAccept(), "account_id", ContextUtil.getUserId())
                        //进行中选中时
                        .eq(workOrderReq.getMyOngoing(), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
        ).getRecords();
        //工单结果返回
        List<WorkOrderListVo> voList = new ArrayList<>();
        if (records.size() > 0 && records != null) {
            WorkOrderListVo vo = null;
            for (WorkOrder record : records) {
                vo = new WorkOrderListVo();
                //工单id、问题概要、工单编号、分类名称
                vo.setId(record.getId().toString()).setProblemProfile(record.getProblemProfile()).setOutTradeNo(record.getOutTradeNo()).setCategoryName(record.getProductNameVersion() + record.getProblemType());
                //创建时间、上次更新时间、严重等级、主要联系人、工单状态
                vo.setCreateTime(record.getCreateTime()).setUpdateTime(record.getLastUpdateTime()).setProblemSeverity(record.getProblemSeverity()).setMainContact(record.getMainContact()).setStatus(record.getStatus());
                //是否收藏、客户服务号
                vo.setIsCollection(record.getIsCollection()).setCsiNumber(record.getCsiNumber());
                voList.add(vo);
            }
        }

        Integer total = workOrderMapper.selectCount(
                new QueryWrapper<WorkOrder>()
                        //客户服务号
                        .in((workOrderReq.getCustomerCSINumber() != null && workOrderReq.getCustomerCSINumber().size() > 0),"csi_number",workOrderReq.getCustomerCSINumber())
                        //技术工单或非技术工单
                        .eq("type", workOrderReq.getWorkOrderType())
                        //问题概要
                        .like(StrUtil.isNotEmpty(workOrderReq.getProblemProfile()),"problem_profile",workOrderReq.getProblemProfile())
                        //我收藏选中时
                        .eq(workOrderReq.getMyCollection(), "is_collection", StatusCodeUtil.WORK_ORDER_COLLECTION_STATUS)
                        .eq(workOrderReq.getMyCollection(), "account_id", ContextUtil.getUserId())
                        //我受理选中时
                        .eq(workOrderReq.getMyAccept(), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                        .eq(workOrderReq.getMyAccept(), "account_id", ContextUtil.getUserId())
                        //进行中选中时
                        .eq(workOrderReq.getMyOngoing(), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS));

        map.put("total",total);
        map.put("voList",voList);
        return map;
    }


    /**
     * 提交工单
     * @param createWorkOrderReq 添加对象
     * @return int
     */
    @Override
    public int createWorkOrder(CreateWorkOrderReq createWorkOrderReq) {
        WorkOrder workOrder = new WorkOrder();
        //用户id、工单编号、严重等级、问题概要
        workOrder.setAccountId(ContextUtil.getUserId()).setOutTradeNo("HL-" + RandomUtil.randomNumbers(11)).setProblemSeverity(createWorkOrderReq.getProblemSeverity()).setProblemProfile(createWorkOrderReq.getProblemProfile());
        //问题描述、问题描述图片、错误代码、问题附件、附件描述
        workOrder.setProblemDescription(createWorkOrderReq.getProblemDescription()).setProblemDesImage(createWorkOrderReq.getProblemDesImagePath()).setErrorCode(createWorkOrderReq.getErrorCode()).setAttachmentPath(createWorkOrderReq.getAttachmentPath()).setAttachmentDescription(createWorkOrderReq.getAttachmentDescription());
        //主要联系人、手机号、邮箱、备用联系人、手机、邮箱
        workOrder.setMainContact(createWorkOrderReq.getMainContact()).setMainMobile(createWorkOrderReq.getMainMobile()).setMainEmail(createWorkOrderReq.getMainEmail()).setContactWay(createWorkOrderReq.getContactWay()).setStandbyContact(createWorkOrderReq.getStandbyContact()).setStandbyMobile(createWorkOrderReq.getStandbyMobile()).setStandbyEmail(createWorkOrderReq.getStandbyEmail());
        //订单类型、硬件平台、操作系统、版本、系统语言、部署方式、产品名称版本、问题类型、客户服务号
        workOrder.setType(createWorkOrderReq.getWorkOrderType()).setHardwarePlatform(createWorkOrderReq.getHardware()).setOperatingSystem(createWorkOrderReq.getOperationSystem()).setOperatingSystemVersion(createWorkOrderReq.getOperationSystemVersion()).setOperatingSystemLanguage(createWorkOrderReq.getOperationSystemVersionLanguage()).setDeploymentWay(createWorkOrderReq.getDeploymentType()).setProductNameVersion(createWorkOrderReq.getProductNameVersion()).setProblemType(createWorkOrderReq.getProblemType()).setCsiNumber(createWorkOrderReq.getCsiNumber());
        //工单状态、最后更新时间
        workOrder.setStatus(StatusCodeUtil.WORK_ORDER_PROCESS_STATUS).setLastUpdateTime(DateUtil.now());
        //提交
        return workOrderMapper.insert(workOrder);
    }


    /**
     * 查询工单基本信息
     * @param workOrderId 工单id
     * @return WorkOrderDetailBaseInfoVo
     */
    @Override
    public WorkOrderDetailBaseInfoVo queryWorkOrderBaseInfo(String workOrderId) {
        //工单记录
        WorkOrder workOrder = workOrderMapper.selectById(workOrderId);
        if (workOrder != null) {
            CompanyCSI companyCSI = companyCSIMapper.selectOne(new QueryWrapper<CompanyCSI>().eq("csi_number", workOrder.getCsiNumber()));
            if (companyCSI == null) {
                log.error("查询工单基本信息【companyCSI】为空");
                return null;
            }
            WorkOrderDetailBaseInfoVo vo = getWorkOrderBaseInfo(workOrder, companyCSI);
            //判断用户是否收藏工单
            Integer collectionCount = collectionAccountMapper.selectCount(new QueryWrapper<CollectionAccount>().eq("account_id", ContextUtil.getUserId()).eq("work_order_id", workOrder.getId()));
            vo.setIsCollection(collectionCount > 0 ? 1 : 0);
            return vo;
        }
        return null;
    }


    /**
     * 获取工单基本信息
     * @param workOrder 工单
     * @param companyCSI 公司CSI信息
     * @return WorkOrderDetailBaseInfoVo
     */
    public WorkOrderDetailBaseInfoVo getWorkOrderBaseInfo(WorkOrder workOrder,CompanyCSI companyCSI) {
        WorkOrderDetailBaseInfoVo vo = new WorkOrderDetailBaseInfoVo();
        //是否是自己的工单，0否、1是  ||  工单状态
        vo.setIsMySelf(ContextUtil.getUserId().compareTo(workOrder.getAccountId()) == 0 ? 1 : 0).setStatus(workOrder.getStatus());
        //创建时间、工单id、客户服务号、公司名
        vo.setCreateTime(workOrder.getCreateTime().substring(0,16)).setWorkOrderId(workOrder.getId().toString()).setCsiNumber(workOrder.getCsiNumber()).setCompanyName(companyCSI.getCompanyName());
        //部署方式、硬件平台、最后更新时间
        vo.setDeploymentWay(workOrder.getDeploymentWay()).setHardwarePlatform(workOrder.getHardwarePlatform()).setLastUpdateTime(workOrder.getLastUpdateTime().substring(0,16));
        //主要联系人、备用联系人、操作系统、版本、语言
        vo.setMainContact(workOrder.getMainContact()).setStandbyContact(workOrder.getStandbyContact()).setOperatingSystem(workOrder.getOperatingSystem()).setOperatingSystemVersion(workOrder.getOperatingSystemVersion()).setOperatingSystemLanguage(workOrder.getOperatingSystemLanguage());
        //工单编号、问题概要、问题严重等级、产品名称版本、问题类型
        vo.setOutTradeNo(workOrder.getOutTradeNo()).setProblemProfile(workOrder.getProblemProfile()).setProblemSeverity(workOrder.getProblemSeverity()).setProductNameVersion(workOrder.getProductNameVersion()).setProblemType(workOrder.getProblemType());
        //升级状态、升级原因
        vo.setUpgradeStatus(workOrder.getUpgradeStatus()).setUpgradeReason(StrUtil.hasEmpty(workOrder.getUpgradeReason()) ? "" : workOrder.getUpgradeReason());
        //空集合
        List<String> list = new ArrayList<>();
        //相关附件
        vo.setAttachmentPath(StrUtil.hasEmpty(workOrder.getAttachmentPath()) ? list : Arrays.asList(workOrder.getAttachmentPath().split(",")));
        //相关链接
        vo.setRelatedLinks(StrUtil.hasEmpty(workOrder.getRelatedLinks()) ? list : Arrays.asList(workOrder.getRelatedLinks().split(",")));
        //相关知识文档
        vo.setKnowledgeDocId(getKnowledgeList(workOrder.getRelatedDoc(),1));
        //相关bug文档
        vo.setKnowLedgeBugDocId(getKnowledgeList(workOrder.getRelatedBugDoc(),2));
        return vo;
    }



    /**
     * 获取工单相关文档【知识文档、bug文档】
     * @param docIdStr 文档id
     * @param docType 文档类型
     * @return list
     */
    public List<KnowledgeListVo> getKnowledgeList(String docIdStr,Integer docType) {
        List<KnowledgeListVo> voList = new ArrayList<>();
        if (StrUtil.hasEmpty(docIdStr)) {
            return voList;
        }
        List<KnowledgeDocument> documents = knowledgeDocumentService.list(new QueryWrapper<KnowledgeDocument>().in("id", Arrays.asList(docIdStr.split(","))).eq("doc_type", docType));
        if (documents != null && documents.size() > 0) {
            KnowledgeListVo doc = null;
            for (KnowledgeDocument document : documents) {
                doc = new KnowledgeListVo().setId(document.getId().toString()).setDocTitle(document.getDocTitle());
                voList.add(doc);
            }
        }
        return voList;
    }



    /**
     * 修改工单严重等级
     * @param changeWorkOrderProblemSeverity 修改对象
     * @return int
     */
    @Override
    public int changeWorkOrderProblemSeverity(ChangeWorkOrderProblemSeverity changeWorkOrderProblemSeverity) {
        WorkOrder workOrder = workOrderMapper.selectById(changeWorkOrderProblemSeverity.getWorkOrderId());
        if (workOrder == null) {
            return StatusCodeUtil.NOT_FOUND_FLAG;
        }
        //严重等级
        workOrder.setProblemSeverity(changeWorkOrderProblemSeverity.getProblemSeverity() == null ? workOrder.getProblemSeverity() : changeWorkOrderProblemSeverity.getProblemSeverity());
        //备用联系人姓名
        workOrder.setStandbyContact(StrUtil.hasEmpty(changeWorkOrderProblemSeverity.getStandbyContact()) ? workOrder.getStandbyContact() : changeWorkOrderProblemSeverity.getStandbyContact());
        //备用联系人邮箱
        workOrder.setStandbyEmail(StrUtil.hasEmpty(changeWorkOrderProblemSeverity.getStandbyEmail()) ? workOrder.getMainEmail() : changeWorkOrderProblemSeverity.getStandbyEmail());
        //备用联系人手机号
        workOrder.setStandbyMobile(StrUtil.hasEmpty(changeWorkOrderProblemSeverity.getStandbyMobile()) ? workOrder.getStandbyMobile() : changeWorkOrderProblemSeverity.getStandbyMobile());
        return workOrderMapper.updateById(workOrder);
    }


    /**
     * 添加工单附件
     * @param createWorkOrderAttachReq 附件对象
     * @return int
     */
    @Override
    public int addWorkOrderAttach(CreateWorkOrderAttachReq createWorkOrderAttachReq) {
        WorkOrder workOrder = workOrderMapper.selectById(createWorkOrderAttachReq.getWorkOrderId());
        if (workOrder != null) {
            //附件地址
            workOrder.setAttachmentPath(StrUtil.hasEmpty(createWorkOrderAttachReq.getAttachPath()) ? workOrder.getAttachmentPath() : workOrder.getAttachmentPath() + "," + createWorkOrderAttachReq.getAttachPath());
            //附件描述、最后更新时间
            workOrder.setAttachmentDescription(StrUtil.hasEmpty(createWorkOrderAttachReq.getAttachDescription()) ? workOrder.getAttachmentDescription() : createWorkOrderAttachReq.getAttachDescription()).setLastUpdateTime(DateUtil.now());
            int row = workOrderMapper.updateById(workOrder);
            if (row > 0) {
                //添加沟通记录，用户更新记录
                CommunicationRecord record = new CommunicationRecord().setWorkOrderId(workOrder.getId()).setAccountId(workOrder.getAccountId()).setCodeFlag(StatusCodeUtil.VDM_CUSTOMER_UPDATE).setContent("将文件" + createWorkOrderAttachReq.getAttachPath() + "附件上传成功").setIsShow(1);
                row = communicationRecordMapper.insert(record);
            }else {
                throw new RuntimeException("更新用户工单附件信息失败！");
            }
            return row;
        }
        return 0;
    }


    /**
     * 添加工单反馈
     * @param createWorkOrderFeedbackReq 添加对象
     * @return int
     */
    @Override
    public int addWorkOrderFeedback(CreateWorkOrderFeedbackReq createWorkOrderFeedbackReq) {
        WorkOrder workOrder = workOrderMapper.selectById(createWorkOrderFeedbackReq.getWorkOrderId());
        if (workOrder == null) {
            log.error("添加工单反馈失败【workOrder】不存在");
            return 0;
        }
        //添加沟通记录
        CommunicationRecord record = new CommunicationRecord();
        record.setWorkOrderId(workOrder.getId()).setAccountId(workOrder.getAccountId()).setCodeFlag(StatusCodeUtil.VDM_CUSTOMER_UPDATE).setContent(createWorkOrderFeedbackReq.getFeedbackContent()).setIsShow(1).setDescImage(createWorkOrderFeedbackReq.getDescImage());
        communicationRecordMapper.insert(record);
        //更新工单最后更新时间
        workOrder.setLastUpdateTime(DateUtil.now());
        return workOrderMapper.updateById(workOrder);
    }


    /**
     * 用户关闭工单申请
     * @param submitCloseWorkOrderApplyReq 原因对象
     * @return int
     */
    @Override
    public int accountCloseWorkOrder(SubmitCloseWorkOrderApplyReq submitCloseWorkOrderApplyReq) {
        WorkOrder workOrder = workOrderMapper.selectById(submitCloseWorkOrderApplyReq.getWorkOrderId());
        if (workOrder != null) {
            workOrder.setStatus(StatusCodeUtil.WORK_ORDER_ACCOUNT_CLOSE_APPLY_STATUS);
            workOrder.setCloseReason(submitCloseWorkOrderApplyReq.getCloseReason()).setCloseFeedback(submitCloseWorkOrderApplyReq.getCloseFeedback());
            return workOrderMapper.updateById(workOrder);
        }
        return 0;
    }

    /**
     * 工程师端首页统计
     * @param engineerId 工程师id
     * @return map
     */
    @Override
    public Map<String, Integer> queryEngineerWorkOrderStatistical(String engineerId) {
        Map<String, Integer> map = new HashMap<>();
        //公司总计受理中的工单
        List<Integer> workOrderStatusList = new ArrayList<>();
        workOrderStatusList.add(1);
        workOrderStatusList.add(2);
        Integer companyTotalAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().in("status", workOrderStatusList));
        map.put("companyTotalAcceptWorkOrderCount",companyTotalAcceptWorkOrderCount);
        //我受理中的工单
        Integer myAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("engineer_id", engineerId).eq("status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS));
        map.put("myAcceptWorkOrderCount",myAcceptWorkOrderCount);
        //我的已完结工单
        Integer myCloseWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("engineer_id", engineerId).eq("status", StatusCodeUtil.WORK_ORDER_CLOSE_STATUS));
        map.put("myCloseWorkOrderCount",myCloseWorkOrderCount);
        //我收藏的工单
        Integer collectionCount = collectionEngineerMapper.selectCount(new QueryWrapper<CollectionEngineer>().eq("engineer_id", engineerId));
        map.put("myCollectionWorkOrderCount",collectionCount);
        return map;
    }


    /**
     * 初始化工程师端工单列表
     * @param engineerIndexInitReq 初始化对象
     * @return map
     */
    @Override
    public Map<String, Object> queryEngineerIndexInit(EngineerIndexInitReq engineerIndexInitReq) {
        Map<String, Object> map = new HashMap<>();
        //当前工程师id
        Long userId = ContextUtil.getUserId();
        //三级分类id不为空时，表示筛选了三级分类
        String threeCategoryName = "";
        if (StrUtil.isNotEmpty(engineerIndexInitReq.getThreeCategoryId())) {
            KnowledgeDocumentThreeCategory threeCategory = threeCategoryMapper.selectById(engineerIndexInitReq.getThreeCategoryId());
            threeCategoryName = threeCategory == null ? "" : threeCategory.getCategoryTitle();
        }
        //工程师收藏的工单id列表
        List<Long> workOrderIdList = new ArrayList<>();
        if (engineerIndexInitReq.getMyCollectionOrder() != null && engineerIndexInitReq.getMyCollectionOrder()) {
            //工程师收藏列表
            List<CollectionEngineer> collectionEngineerList = collectionEngineerMapper.selectList(new QueryWrapper<CollectionEngineer>().eq("engineer_id", userId));
            if (!CollectionUtils.isEmpty(collectionEngineerList)) {
                collectionEngineerList.forEach(collection -> {
                    workOrderIdList.add(collection.getWorkOrderId());
                });
            }
        }
        //条件构造器
        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<WorkOrder>()
                .eq(StrUtil.isNotEmpty(engineerIndexInitReq.getOutTradeNo()), "out_trade_no", engineerIndexInitReq.getOutTradeNo())
                .eq(StrUtil.isNotEmpty(engineerIndexInitReq.getProblemProfile()), "problem_profile", engineerIndexInitReq.getProblemProfile())
                .eq(StrUtil.isNotEmpty(engineerIndexInitReq.getAcceptPerson()), "engineer_id", engineerIndexInitReq.getAcceptPerson())
                .eq(StrUtil.isNotEmpty(threeCategoryName), "problem_type", threeCategoryName)
                //我受理中的工单
                .eq(engineerIndexInitReq.getMyAcceptOrder(), "engineer_id", userId)
                .eq(engineerIndexInitReq.getMyAcceptOrder(), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                //我的已完结工单
                .eq(engineerIndexInitReq.getMyCloseOrder(), "engineer_id", userId)
                .eq(engineerIndexInitReq.getMyAcceptOrder(), "status", StatusCodeUtil.WORK_ORDER_CLOSE_STATUS)
                //我收藏的工单
                .in(workOrderIdList.size() > 0 && workOrderIdList != null, "id", workOrderIdList)
                //开始时间
                .ge(StrUtil.isNotEmpty(engineerIndexInitReq.getStartTime()), "create_time", engineerIndexInitReq.getStartTime())
                //结束时间
                .le(StrUtil.isNotEmpty(engineerIndexInitReq.getEndTime()), "last_update_time", engineerIndexInitReq.getEndTime())
                //id倒序排序
                .orderByDesc("id");
        //筛选后的工单分页列表
        List<WorkOrder> records = workOrderMapper.selectPage(new Page<>(engineerIndexInitReq.getPageNo(), engineerIndexInitReq.getPageSize()), queryWrapper).getRecords();
        //结果封装
        List<WorkOrderListVo> voList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(records)) {
            WorkOrderListVo vo = null;
            for (WorkOrder record : records) {
                vo = new WorkOrderListVo();
                //工单id、问题概要、工单编号、分类名称、开始时间、最后更新时间
                vo.setId(record.getId().toString()).setProblemProfile(record.getProblemProfile()).setOutTradeNo(record.getOutTradeNo()).setCategoryName(record.getProductNameVersion() + record.getProblemType()).setCreateTime(record.getCreateTime().substring(0,16)).setUpdateTime(record.getLastUpdateTime().substring(0,16));
                //严重等级、联系人、工单状态、客户服务号
                vo.setProblemSeverity(record.getProblemSeverity()).setMainContact(record.getMainContact()).setStatus(record.getStatus()).setCsiNumber(record.getCsiNumber());
                //是否收藏工单
                Integer count = collectionEngineerMapper.selectCount(new QueryWrapper<CollectionEngineer>().eq("engineer_id", userId).eq("work_order_id", record.getId()));
                vo.setIsCollection(count > 0 ? 1 : 0);
                voList.add(vo);
            }
        }
        map.put("voList",voList);
        map.put("total",workOrderMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 工程师查看工单基本信息
     * @param workOrderId 工单id
     * @return EngineerWorkOrderBaseInfoVo
     */
    @Override
    public EngineerWorkOrderBaseInfoVo engineerQueryWorkOrderBaseInfo(String workOrderId) {
        //当前工单
        WorkOrder workOrder = workOrderMapper.selectById(workOrderId);
        if (workOrder != null) {
            CompanyCSI companyCSI = companyCSIMapper.selectOne(new QueryWrapper<CompanyCSI>().eq("csi_number", workOrder.getCsiNumber()));
            if (companyCSI != null) {
                //获取基本工单信息
                WorkOrderDetailBaseInfoVo orderBaseInfo = getWorkOrderBaseInfo(workOrder, companyCSI);
                //转换为JSON字符串
                String jsonString = JSONObject.toJSONString(orderBaseInfo);
                //解析为结果对象
                EngineerWorkOrderBaseInfoVo resultVo = JSONObject.parseObject(jsonString, EngineerWorkOrderBaseInfoVo.class);
                //设置当前工单处理流程
                resultVo.setCurrentProcess(StrUtil.hasEmpty(workOrder.getCurrentProcess()) ? "" : workOrder.getCurrentProcess());
                //设置工程师是否收藏工单
                Integer collectionCount = collectionEngineerMapper.selectCount(new QueryWrapper<CollectionEngineer>().eq("engineer_id", ContextUtil.getUserId()).eq("work_order_id", workOrderId));
                resultVo.setIsCollection(collectionCount > 0 ? 1 : 0);
                return resultVo;
            }
        }
        return null;
    }





}
