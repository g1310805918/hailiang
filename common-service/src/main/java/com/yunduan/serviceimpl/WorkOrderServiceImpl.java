package com.yunduan.serviceimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
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
import com.yunduan.utils.*;
import com.yunduan.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;


@Service
@Transactional
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired
    private BindingAccountCSIMapper bindingAccountCSIMapper;
    @Autowired
    private AccountMsgMapper accountMsgMapper;
    @Autowired
    private CompanyCSIMapper companyCSIMapper;
    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;
    @Autowired
    private CommunicationRecordMapper communicationRecordMapper;
    @Autowired
    private CollectionAccountMapper collectionAccountMapper;
    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    @Autowired
    private CollectionEngineerMapper collectionEngineerMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryMapper threeCategoryMapper;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private SendEmailUtil sendEmailUtil;
    @Autowired
    private EngineerMsgMapper engineerMsgMapper;
    @Autowired
    private SendMessageUtil sendMessageUtil;
    @Autowired
    private DistributionUtil distributionUtil;


    /**
     * 查询用户工单系统统计
     *
     * @param accountId 用户id
     * @return map
     */
    @Override
    public Map<String, Integer> queryWorkOrderStatistical(String accountId) {
        Map<String, Integer> resultMap = new HashMap<>();
        //当前登录用户id
        Long userId = ContextUtil.getUserId();
        //用户绑定的所有CSI编号集合
        Set<String> CSINumberSet = new TreeSet<>();
        //用户绑定的CSI编号记录
        List<AccountBindingCSI> bindingRecord = bindingAccountCSIMapper.selectAccountBindingRecord(accountId);
        if (bindingRecord.size() > 0 && bindingRecord != null) {
            for (AccountBindingCSI bindingCSI : bindingRecord) {
                CSINumberSet.add(bindingCSI.getCsiNumber());
            }
        }
        //当前用户未读消息数量
        Integer accountUnreadMessageCount = accountMsgMapper.selectCount(new QueryWrapper<AccountMsg>().eq("account_id", userId).eq("is_read", 0));
        //用户绑定的CSI记录为空，那么直接返回
        if (CSINumberSet.isEmpty()) {
            //公司总计受理中的订单
            resultMap.put("companyTotalAcceptWorkOrderCount", 0);
            //我受理中的工单
            resultMap.put("myAcceptWorkOrderCount", 0);
            //我已完结的工单
            resultMap.put("myCloseWorkOrderCount", 0);
            //我收藏的工单
            resultMap.put("myCollectionWorkOrderCount", 0);
            //用户未读消息数
            resultMap.put("accountUnreadMessageCount", accountUnreadMessageCount);
            return resultMap;
        }
        //公司总计受理中的订单
        Integer companyTotalAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("status", 2).in("csi_number", CSINumberSet));
        //我受理中的工单
        Integer myAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("account_id", userId).eq("status", 2));
        //我已完结的工单
        Integer myCloseWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("account_id", userId).eq("status", 4));
        //我收藏的工单
        Integer myCollectionWorkOrderCount = collectionAccountMapper.selectCount(new QueryWrapper<CollectionAccount>().eq("account_id",userId));
        resultMap.put("companyTotalAcceptWorkOrderCount", companyTotalAcceptWorkOrderCount);
        resultMap.put("myAcceptWorkOrderCount", myAcceptWorkOrderCount);
        resultMap.put("myCloseWorkOrderCount", myCloseWorkOrderCount);
        resultMap.put("myCollectionWorkOrderCount", myCollectionWorkOrderCount);
        resultMap.put("accountUnreadMessageCount", accountUnreadMessageCount);
        return resultMap;
    }


    /**
     * 查询当前用户公司所有订单记录
     *
     * @param workOrderReq 筛选对象
     * @return map
     */
    @Override
    public Map<String, Object> queryCompanyWorkOrderList(WorkOrderReq workOrderReq) {
        Map<String, Object> map = new HashMap<>();
        //当前用户的绑定CSI记录【用户没有绑定CSI编号那么就没有技术性工单】
        List<AccountBindingCSI> bindingRecord = bindingAccountCSIMapper.selectAccountBindingRecord(ContextUtil.getUserId().toString());
        if (CollectionUtil.isEmpty(bindingRecord)) {
            //空数组
            ArrayList<WorkOrder> list = CollectionUtil.newArrayList();
            map.put("total", 0);
            map.put("voList", list);
            return map;
        }
        //用户收藏的所有工单id集合
        List<Long> collectionWorkOrderIdList = new ArrayList<>();
        //我收藏的工单选中时
        if (workOrderReq.getMyCollection()) {
            List<CollectionAccount> collectionWokOrderIds = collectionAccountMapper.selectList(new QueryWrapper<CollectionAccount>().eq("account_id", ContextUtil.getUserId()));
            if (!CollectionUtils.isEmpty(collectionWokOrderIds)) {
                collectionWokOrderIds.forEach(collectionAccount -> {
                    collectionWorkOrderIdList.add(collectionAccount.getWorkOrderId());
                });
            }
            //用户没有收藏的记录时返回空对象
            if (CollectionUtil.isEmpty(collectionWorkOrderIdList)) {
                map.put("total", collectionWorkOrderIdList.size());
                map.put("voList", collectionWorkOrderIdList);
                return map;
            }
        }
        //用户绑定的所有CSI编号集合
        Set<String> bindCSINumberSet = new TreeSet<>();
        for (AccountBindingCSI bindingCSI : bindingRecord) {
            bindCSINumberSet.add(bindingCSI.getCsiNumber());
        }
        //客户服务号
        List<String> customerCSINumber = workOrderReq.getCustomerCSINumber();
        if (CollectionUtil.isNotEmpty(customerCSINumber)) {
            //客户服务号不为空时。表示用户筛选客户服务号下的工单
            bindCSINumberSet = new TreeSet<>();
            for (String s : customerCSINumber) {
                bindCSINumberSet.add(s);
            }
        }
        //条件构造器
        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<WorkOrder>()
                .eq("type", 2)
                //客户服务号
                .in("csi_number", bindCSINumberSet)
                //我收藏选中时
                .in(collectionWorkOrderIdList.size() > 0, "id", collectionWorkOrderIdList)
                //问题概要
                .like(StrUtil.isNotEmpty(workOrderReq.getProblemProfile()), "problem_profile", workOrderReq.getProblemProfile())
                //技术请求编号
                .like(StrUtil.isNotEmpty(workOrderReq.getOutTradeNo()), "out_trade_no", workOrderReq.getOutTradeNo())
                //我受理选中时
                .eq((workOrderReq.getMyAccept() != null && workOrderReq.getMyAccept()), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                .eq((workOrderReq.getMyAccept() != null && workOrderReq.getMyAccept()), "account_id", ContextUtil.getUserId())
                //进行中选中时
                .eq(workOrderReq.getMyOngoing(), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                //排序
                .orderByDesc("create_time");
        //通过CSI编号查询公司所有人员工单
        Page<WorkOrder> workOrderPage = workOrderMapper.selectPage(new Page<>(workOrderReq.getPageNo(), workOrderReq.getPageSize()), queryWrapper);
        List<WorkOrderListVo> listVo = getWorkOrderListVo(workOrderPage.getRecords());
        map.put("total", Convert.toInt(workOrderPage.getTotal()));
        map.put("voList", listVo);
        return map;
    }


    @Override
    public Map<String, Object> queryCompanyWorkOrderListNormal(WorkOrderReq workOrderReq) {
        Map<String, Object> map = CollectionUtil.newHashMap();
        //当前用户的绑定CSI记录
        List<AccountBindingCSI> bindingRecord = bindingAccountCSIMapper.selectAccountBindingRecord(ContextUtil.getUserId().toString());
        //当前用户绑定的CSI编号集合
        ArrayList<String> bindingRecords = CollectionUtil.newArrayList();
        if (CollectionUtil.isNotEmpty(bindingRecord)) {
            for (AccountBindingCSI bindingCSI : bindingRecord) {
                bindingRecords.add(bindingCSI.getCsiNumber());
            }
        }
        //【最终】绑定的CSI列表下的用户id集合
        ArrayList<Long> accountIdList = CollectionUtil.newArrayList();
        //当前用户绑定的CSI编号下其他用户列表
        if (CollectionUtil.isNotEmpty(bindingRecords)) {
            //当前用户绑定的公司CSI信息集合
            List<CompanyCSI> companyCSIList = companyCSIMapper.selectList(new QueryWrapper<CompanyCSI>().in("csi_number", bindingRecords));
            if (CollectionUtil.isNotEmpty(companyCSIList)) {
                //得到绑定的csi编号的id
                ArrayList<Long> csiIdList = CollectionUtil.newArrayList();
                for (CompanyCSI companyCSI : companyCSIList) {
                    csiIdList.add(companyCSI.getId());
                }
                //得到了这些CSI编号下的所有绑定的用户信息
                List<BindingAccountCSI> bindingAccountCSIS = bindingAccountCSIMapper.selectList(new QueryWrapper<BindingAccountCSI>().in("csi_id", csiIdList));
                if (CollectionUtil.isNotEmpty(bindingAccountCSIS)) {
                    for (BindingAccountCSI bindingAccountCSI : bindingAccountCSIS) {
                        accountIdList.add(bindingAccountCSI.getAccountId());
                    }
                }
            }
        } else {
            //当前用户没有绑定CSI编号信息，那么要获取他自己提交的工单列表
            accountIdList.add(ContextUtil.getUserId());
        }

        //用户收藏的所有工单id集合
        List<Long> collectionWorkOrderIdList = new ArrayList<>();
        //我收藏的工单选中时
        if (workOrderReq.getMyCollection()) {
            List<CollectionAccount> collectionWokOrderIds = collectionAccountMapper.selectList(new QueryWrapper<CollectionAccount>().eq("account_id", ContextUtil.getUserId()));
            if (CollectionUtil.isNotEmpty(collectionWokOrderIds)) {
                collectionWokOrderIds.forEach(collectionAccount -> {
                    collectionWorkOrderIdList.add(collectionAccount.getWorkOrderId());
                });
            }
            //用户没有收藏的记录时返回空对象
            if (CollectionUtil.isEmpty(collectionWorkOrderIdList)) {
                map.put("total", collectionWorkOrderIdList.size());
                map.put("voList", collectionWorkOrderIdList);
                return map;
            }
        }
        //条件构造器
        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<WorkOrder>()
                //非技术工单类型
                .eq("type", 1)
                //用户集合
                .in("account_id", accountIdList)
                //我收藏选中时
                .in(collectionWorkOrderIdList.size() > 0, "id", collectionWorkOrderIdList)
                //客户服务号
                .in(CollectionUtil.isNotEmpty(workOrderReq.getCustomerCSINumber()), "csi_number", workOrderReq.getCustomerCSINumber())
                //问题概要
                .like(StrUtil.isNotEmpty(workOrderReq.getProblemProfile()), "problem_profile", workOrderReq.getProblemProfile())
                //技术请求编号
                .like(StrUtil.isNotEmpty(workOrderReq.getOutTradeNo()), "out_trade_no", workOrderReq.getOutTradeNo())
                //我受理选中时
                .eq((workOrderReq.getMyAccept() != null && workOrderReq.getMyAccept()), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                .eq((workOrderReq.getMyAccept() != null && workOrderReq.getMyAccept()), "account_id", ContextUtil.getUserId())
                //进行中选中时
                .eq(workOrderReq.getMyOngoing(), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                //排序
                .orderByDesc("create_time");
        //获取非技术工单列表
        Page<WorkOrder> workOrderPage = workOrderMapper.selectPage(new Page<>(workOrderReq.getPageNo(), workOrderReq.getPageSize()), queryWrapper);
        List<WorkOrderListVo> listVo = getWorkOrderListVo(workOrderPage.getRecords());
        map.put("total", Convert.toInt(workOrderPage.getTotal()));
        map.put("voList", listVo);
        return map;
    }


    /**
     * 获取工单封装结果
     *
     * @param workOrderList 原工单列表
     * @return List
     */
    protected List<WorkOrderListVo> getWorkOrderListVo(List<WorkOrder> workOrderList) {
        //工单结果返回
        List<WorkOrderListVo> voList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(workOrderList)) {
            WorkOrderListVo vo = null;
            for (WorkOrder record : workOrderList) {
                vo = new WorkOrderListVo();
                //工单id、问题概要、工单编号、分类名称
                vo.setId(record.getId().toString()).setProblemProfile(record.getProblemProfile()).setOutTradeNo(record.getOutTradeNo()).setCategoryName(StrUtil.hasEmpty(record.getProductNameVersion()) ? "" + record.getProblemType() : record.getProductNameVersion() + record.getProblemType());
                //创建时间、上次更新时间、严重等级、主要联系人、工单状态
                vo.setCreateTime(record.getCreateTime()).setUpdateTime(record.getLastUpdateTime()).setProblemSeverity(record.getProblemSeverity()).setMainContact(record.getMainContact()).setStatus(record.getStatus());
                // 是否收藏【当前登录用户是否收藏当前工单】
                Integer count = collectionAccountMapper.selectCount(new QueryWrapper<CollectionAccount>().eq("account_id", ContextUtil.getUserId()).eq("work_order_id", record.getId()));
                vo.setIsCollection(count > 0 ? 1 : 0);
                //客户服务号
                vo.setCsiNumber(record.getCsiNumber());
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 提交工单
     *
     * @param createWorkOrderReq 添加对象
     * @return int
     */
    @Override
    public int createWorkOrder(CreateWorkOrderReq createWorkOrderReq) {
        WorkOrder workOrder = new WorkOrder();
        //用户id、工单编号、严重等级、问题概要
        workOrder.setAccountId(ContextUtil.getUserId()).setOutTradeNo("HL" + RandomUtil.randomNumbers(12)).setProblemSeverity(createWorkOrderReq.getProblemSeverity()).setProblemProfile(createWorkOrderReq.getProblemProfile());
        //问题描述、问题描述图片、错误代码、问题附件、附件描述
        workOrder.setProblemDescription(createWorkOrderReq.getProblemDescription()).setProblemDesImage(createWorkOrderReq.getProblemDesImagePath()).setErrorCode(createWorkOrderReq.getErrorCode()).setAttachmentPath(createWorkOrderReq.getAttachmentPath()).setAttachmentDescription(createWorkOrderReq.getAttachmentDescription());
        //主要联系人、手机号、邮箱、备用联系人、手机、邮箱
        workOrder.setMainContact(createWorkOrderReq.getMainContact()).setMainMobile(createWorkOrderReq.getMainMobile()).setMainEmail(createWorkOrderReq.getMainEmail()).setContactWay(createWorkOrderReq.getContactWay()).setStandbyContact(createWorkOrderReq.getStandbyContact()).setStandbyMobile(createWorkOrderReq.getStandbyMobile()).setStandbyEmail(createWorkOrderReq.getStandbyEmail());
        //订单类型、硬件平台、操作系统、版本、系统语言、部署方式、产品名称版本、问题类型、客户服务号
        workOrder.setType(createWorkOrderReq.getWorkOrderType()).setHardwarePlatform(createWorkOrderReq.getHardware()).setOperatingSystem(createWorkOrderReq.getOperationSystem()).setOperatingSystemVersion(createWorkOrderReq.getOperationSystemVersion()).setOperatingSystemLanguage(createWorkOrderReq.getOperationSystemVersionLanguage()).setDeploymentWay(createWorkOrderReq.getDeploymentType()).setProductNameVersion(createWorkOrderReq.getProductNameVersion()).setProblemType(createWorkOrderReq.getProblemType()).setCsiNumber(createWorkOrderReq.getCsiNumber());
        //工单状态、最后更新时间、工单所属三级分类id
        workOrder.setStatus(StatusCodeUtil.WORK_ORDER_PROCESS_STATUS).setLastUpdateTime(DateUtil.now()).setCategoryId(createWorkOrderReq.getCategoryId());
        //提交
        int row = workOrderMapper.insert(workOrder);
        if (row > 0) {
            //异步处理工单分配
            threadPoolTaskExecutor.execute(() -> distributionUtil.autoDistributionWorkOrderToEngineer(workOrder.getId().toString()));
        }
        return row;
    }


    /**
     * 查询工单基本信息
     *
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
     *
     * @param workOrder  工单
     * @param companyCSI 公司CSI信息
     * @return WorkOrderDetailBaseInfoVo
     */
    public WorkOrderDetailBaseInfoVo getWorkOrderBaseInfo(WorkOrder workOrder, CompanyCSI companyCSI) {
        WorkOrderDetailBaseInfoVo vo = new WorkOrderDetailBaseInfoVo();
        //是否是自己的工单，0否、1是  |  工单状态
        vo.setIsMySelf(ContextUtil.getUserId().compareTo(workOrder.getAccountId()) == 0 ? 1 : 0).setStatus(workOrder.getStatus());
        //创建时间、工单id、客户服务号、公司名
        vo.setCreateTime(workOrder.getCreateTime().substring(0, 16)).setWorkOrderId(workOrder.getId().toString()).setCsiNumber(workOrder.getCsiNumber()).setCompanyName(companyCSI.getCompanyName());
        //部署方式、硬件平台、最后更新时间
        vo.setDeploymentWay(workOrder.getDeploymentWay()).setHardwarePlatform(workOrder.getHardwarePlatform()).setLastUpdateTime(workOrder.getLastUpdateTime().substring(0, 16));
        //主要联系人、备用联系人、操作系统、版本、语言
        vo.setMainContact(workOrder.getMainContact()).setStandbyContact(StrUtil.hasEmpty(workOrder.getStandbyContact()) ? "" : workOrder.getStandbyContact()).setOperatingSystem(workOrder.getOperatingSystem()).setOperatingSystemVersion(workOrder.getOperatingSystemVersion()).setOperatingSystemLanguage(workOrder.getOperatingSystemLanguage());
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
        vo.setKnowledgeDocId(getKnowledgeList(workOrder.getRelatedDoc(), 1));
        //相关bug文档
        vo.setKnowLedgeBugDocId(getKnowledgeList(workOrder.getRelatedBugDoc(), 2));
        //用户是否可以重开工单(大于一个月就不可以重开、否则可以)
        long between = DateUtil.between(DateUtil.parseDate(workOrder.getLastUpdateTime()), DateUtil.parse(DateUtil.now()), DateUnit.DAY);
        vo.setIsCanOpenAgain(between > 31 ? 0 : 1);
        //工单类型【1非技术工单、2技术工单】
        vo.setWorkOrderType(workOrder.getType());
        //工单主要联系人手机号、主要联系人邮箱
        vo.setMainMobile(StrUtil.hasEmpty(workOrder.getMainMobile()) ? "" : workOrder.getMainMobile()).setMainEmail(StrUtil.hasEmpty(workOrder.getMainEmail()) ? "" : workOrder.getMainEmail());
        //工单备用联系人手机号、备用联系人邮箱
        vo.setStandbyMobile(StrUtil.hasEmpty(workOrder.getStandbyMobile()) ? "" : workOrder.getStandbyMobile()).setStandbyEmail(StrUtil.hasEmpty(workOrder.getStandbyEmail()) ? "" : workOrder.getStandbyEmail());
        //优先联系方式
        vo.setContactWay(workOrder.getContactWay());
        return vo;
    }


    /**
     * 获取工单相关文档【知识文档、bug文档】
     *
     * @param docIdStr 文档id
     * @param docType  文档类型
     * @return list
     */
    public List<KnowledgeListVo> getKnowledgeList(String docIdStr, Integer docType) {
        List<KnowledgeListVo> voList = new ArrayList<>();
        if (StrUtil.hasEmpty(docIdStr)) {
            return voList;
        }
        List<KnowledgeDocument> documents = knowledgeDocumentService.list(new QueryWrapper<KnowledgeDocument>().in("id", Arrays.asList(docIdStr.split(","))).eq("doc_type", docType));
        if (documents != null && documents.size() > 0) {
            KnowledgeListVo doc = null;
            for (KnowledgeDocument document : documents) {
                doc = new KnowledgeListVo().setId(document.getId().toString()).setDocumentNumber(document.getDocNumber()).setDocTitle(document.getDocTitle());
                voList.add(doc);
            }
        }
        return voList;
    }


    /**
     * 修改工单严重等级
     *
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
     *
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
            } else {
                throw new RuntimeException("更新用户工单附件信息失败！");
            }
            return row;
        }
        return 0;
    }


    /**
     * 添加工单反馈
     *
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
        int row = workOrderMapper.updateById(workOrder);
        if (row > 0) {
            //向工程师发送邮件提醒工程师处理用户反馈的问题
            threadPoolTaskExecutor.execute(() -> sendEmailUtil.sendRemindEngineerForWorkOrder(workOrder));
        }
        return row;
    }


    /**
     * 用户关闭工单申请
     *
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
     *
     * @param engineerId 工程师id
     * @return map
     */
    @Override
    public Map<String, Integer> queryEngineerWorkOrderStatistical(String engineerId) {
        Map<String, Integer> map = new HashMap<>();
        //公司总计受理中的工单
        ArrayList<Integer> workOrderStatusList = CollectionUtil.newArrayList(1, 2);
        Integer companyTotalAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().in("status", workOrderStatusList));
        map.put("companyTotalAcceptWorkOrderCount", companyTotalAcceptWorkOrderCount);
        //我受理中的工单
        Integer myAcceptWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("engineer_id", engineerId).eq("status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS));
        map.put("myAcceptWorkOrderCount", myAcceptWorkOrderCount);
        //我的已完结工单
        Integer myCloseWorkOrderCount = workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("engineer_id", engineerId).eq("status", StatusCodeUtil.WORK_ORDER_CLOSE_STATUS));
        map.put("myCloseWorkOrderCount", myCloseWorkOrderCount);
        //我收藏的工单
        Integer collectionCount = collectionEngineerMapper.selectCount(new QueryWrapper<CollectionEngineer>().eq("engineer_id", engineerId));
        map.put("myCollectionWorkOrderCount", collectionCount);
        //当前工程师未读消息数量
        Integer engineerUnreadMessageCount = engineerMsgMapper.selectCount(new QueryWrapper<EngineerMsg>().eq("engineer_id", engineerId).eq("is_read", 0));
        map.put("engineerUnreadMessageCount",engineerUnreadMessageCount);
        return map;
    }


    /**
     * 初始化工程师端工单列表
     *
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
                .like(StrUtil.isNotEmpty(engineerIndexInitReq.getOutTradeNo()), "out_trade_no", engineerIndexInitReq.getOutTradeNo())
                .like(StrUtil.isNotEmpty(engineerIndexInitReq.getProblemProfile()), "problem_profile", engineerIndexInitReq.getProblemProfile())
                .eq(StrUtil.isNotEmpty(engineerIndexInitReq.getAcceptPerson()), "engineer_id", engineerIndexInitReq.getAcceptPerson())
                .eq(StrUtil.isNotEmpty(threeCategoryName), "problem_type", threeCategoryName)
                //我受理中的工单
                .eq((engineerIndexInitReq.getMyAcceptOrder() != null && engineerIndexInitReq.getMyAcceptOrder()), "engineer_id", userId)
                .eq((engineerIndexInitReq.getMyAcceptOrder() != null && engineerIndexInitReq.getMyAcceptOrder()), "status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)
                //我的已完结工单
                .eq((engineerIndexInitReq.getMyCloseOrder() != null && engineerIndexInitReq.getMyCloseOrder()), "engineer_id", userId)
                .eq((engineerIndexInitReq.getMyAcceptOrder() != null && engineerIndexInitReq.getMyAcceptOrder()), "status", StatusCodeUtil.WORK_ORDER_CLOSE_STATUS)
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
        map.put("voList", getEngineerResultWorkOrderList(records, userId));
        map.put("total", workOrderMapper.selectCount(queryWrapper));
        return map;
    }

    /**
     * 获取工程师工单列表
     *
     * @param records 元数据集合
     * @return 封装结果
     */
    protected List<WorkOrderListVo> getEngineerResultWorkOrderList(List<WorkOrder> records, Long engineerId) {
        List<WorkOrderListVo> voList = CollectionUtil.newArrayList();
        if (CollectionUtil.isNotEmpty(records)) {
            WorkOrderListVo vo = null;
            for (WorkOrder record : records) {
                vo = new WorkOrderListVo();
                //工单id、问题概要、工单编号、分类名称、开始时间、最后更新时间
                vo.setId(record.getId().toString()).setProblemProfile(record.getProblemProfile()).setOutTradeNo(record.getOutTradeNo()).setCategoryName(record.getProductNameVersion() + record.getProblemType()).setCreateTime(record.getCreateTime().substring(0, 16)).setUpdateTime(record.getLastUpdateTime().substring(0, 16));
                //严重等级、联系人、工单状态、客户服务号
                vo.setProblemSeverity(record.getProblemSeverity()).setMainContact(record.getMainContact()).setStatus(record.getStatus()).setCsiNumber(record.getCsiNumber());
                //是否收藏工单
                Integer count = collectionEngineerMapper.selectCount(new QueryWrapper<CollectionEngineer>().eq("engineer_id", engineerId).eq("work_order_id", record.getId()));
                vo.setIsCollection(count > 0 ? 1 : 0);
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 工程师查看工单基本信息
     *
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


    /**
     * 关联工单文档信息
     *
     * @param addKnowledgeDocReq 关联对象
     * @return int
     */
    @Override
    public int joinWorkOrderDocumentInfo(AddKnowledgeDocReq addKnowledgeDocReq) {
        WorkOrder workOrder = workOrderMapper.selectById(addKnowledgeDocReq.getWorkOrderId());
        if (workOrder != null) {
            KnowledgeDocument document = knowledgeDocumentMapper.selectOne(new QueryWrapper<KnowledgeDocument>().eq("doc_number", addKnowledgeDocReq.getDocumentNumber()));
            if (document == null) {
                return StatusCodeUtil.DOCUMENT_NOT_EXIST;
            }
            //文档类型【1知识、2bug】
            Integer type = addKnowledgeDocReq.getDocumentType();

            String resultStr = "";
            if (type == 1) {
                //相关知识文档【工单之前没有关联过文档，直接关联新文档，否则拼接新文档】
                resultStr = StrUtil.hasEmpty(workOrder.getRelatedDoc()) ? document.getId().toString() + "," : workOrder.getRelatedDoc() + document.getId() + ",";
                workOrder.setRelatedDoc(resultStr);
            } else {
                //相关bug文档
                resultStr = StrUtil.hasEmpty(workOrder.getRelatedBugDoc()) ? document.getId().toString() + "," : workOrder.getRelatedBugDoc() + document.getId() + ",";
                workOrder.setRelatedBugDoc(resultStr);
            }
            return workOrderMapper.updateById(workOrder);
        }
        return 0;
    }


    /**
     * 添加相关资料
     *
     * @param addRelatedLinksReq 资料
     * @return int
     */
    @Override
    public int relatedLinks(AddRelatedLinksReq addRelatedLinksReq) {
        WorkOrder workOrder = workOrderMapper.selectById(addRelatedLinksReq.getWorkOrderId());
        if (workOrder != null) {
            //链接类型【1 资料链接、2 相关附件】
            Integer linkType = addRelatedLinksReq.getLinkType();
            String str = "";
            if (linkType == 1) {
                str = StrUtil.hasEmpty(workOrder.getRelatedLinks()) ? addRelatedLinksReq.getPath() : workOrder.getRelatedLinks() + "," + addRelatedLinksReq.getPath();
                workOrder.setRelatedLinks(str);
            } else {
                str = StrUtil.hasEmpty(workOrder.getAttachmentPath()) ? addRelatedLinksReq.getPath() : workOrder.getAttachmentPath() + "," + addRelatedLinksReq.getPath();
                workOrder.setAttachmentPath(str);
            }
            return workOrderMapper.updateById(workOrder);
        }
        return 0;
    }


    /**
     * 工程师关闭工单
     *
     * @param closeWorkOrderReq 参数
     * @return int
     */
    @Override
    public int closeWorkOrder(CloseWorkOrderReq closeWorkOrderReq) {
        WorkOrder workOrder = workOrderMapper.selectById(closeWorkOrderReq.getWorkOrderId());
        if (workOrder != null) {
            workOrder.setEngineerCloseReason(closeWorkOrderReq.getReason()).setEngineerCloseFeedback(closeWorkOrderReq.getFeedback()).setStatus(StatusCodeUtil.WORK_ORDER_CLOSE_STATUS).setLastUpdateTime(DateUtil.now());
            return workOrderMapper.updateById(workOrder);
        }
        return 0;
    }


    /**
     * 工程师删除关联的文档
     *
     * @param engineerRemoveRelatedDocumentReq 删除的对象
     * @return int
     */
    @Override
    public int engineerRemoveRelated(EngineerRemoveRelatedDocumentReq engineerRemoveRelatedDocumentReq) {
        WorkOrder workOrder = workOrderMapper.selectById(engineerRemoveRelatedDocumentReq.getWorkOrderId());
        if (workOrder != null) {
            //类型
            Integer type = engineerRemoveRelatedDocumentReq.getDocumentType();
            if (type == 1) {
                //知识文档
                String relatedDoc = workOrder.getRelatedDoc();
                relatedDoc = relatedDoc.replaceAll(engineerRemoveRelatedDocumentReq.getDocumentId() + ",", "");
                workOrder.setRelatedDoc(relatedDoc);
            } else {
                //bug文档
                String relatedBugDoc = workOrder.getRelatedBugDoc();
                relatedBugDoc = relatedBugDoc.replaceAll(engineerRemoveRelatedDocumentReq.getDocumentId() + ",", "");
                workOrder.setRelatedBugDoc(relatedBugDoc);
            }
            return workOrderMapper.updateById(workOrder);
        }
        return 0;
    }


    /**
     * 提交文档管理
     *
     * @param submitKMDocumentReq 文档
     * @return int
     */
    @Override
    public int submitKMDocument(SubmitKMDocumentReq submitKMDocumentReq) {
        WorkOrder workOrder = workOrderMapper.selectById(submitKMDocumentReq.getWorkOrderId());
        if (workOrder != null) {
            workOrder.setCloseAssDocument(Convert.toLong(submitKMDocumentReq.getDocumentId())).setCurrentProcess(submitKMDocumentReq.getCurrentProcess()).setLastUpdateTime(DateUtil.now());
            return workOrderMapper.updateById(workOrder);
        }
        return 0;
    }


    /**
     * 工程师分裂工单
     *
     * @param engineerCreateWorkOrderReq 分裂参数
     * @return int
     */
    @Override
    public int engineerSplitWorkOrder(EngineerCreateWorkOrderReq engineerCreateWorkOrderReq) {
        WorkOrder workOrder = workOrderMapper.selectById(engineerCreateWorkOrderReq.getWorkOrderId());
        if (workOrder == null) {
            return StatusCodeUtil.NOT_FOUND_FLAG;
        }
        WorkOrder order = new WorkOrder();
        //用户id、工单编号、严重等级、问题概要
        order.setAccountId(workOrder.getAccountId()).setOutTradeNo("HL-" + RandomUtil.randomNumbers(11)).setProblemSeverity(workOrder.getProblemSeverity()).setProblemProfile(engineerCreateWorkOrderReq.getProblemProfile());
        //问题描述、问题描述图片、错误代码、问题附件、附件描述
        order.setProblemDescription(engineerCreateWorkOrderReq.getProblemDescription()).setProblemDesImage(engineerCreateWorkOrderReq.getProblemDesImagePath()).setErrorCode(engineerCreateWorkOrderReq.getErrorCode()).setAttachmentPath(engineerCreateWorkOrderReq.getAttachmentPath());
        //主要联系人、手机号、邮箱、备用联系人、手机、邮箱
        order.setMainContact(workOrder.getMainContact()).setMainMobile(workOrder.getMainMobile()).setMainEmail(workOrder.getMainEmail()).setContactWay(workOrder.getContactWay()).setStandbyContact(workOrder.getStandbyContact()).setStandbyMobile(workOrder.getStandbyMobile()).setStandbyEmail(workOrder.getStandbyEmail());
        //工单类型、硬件平台、操作系统、版本、系统语言、部署方式、产品名称版本、问题类型、客户服务号
        order.setType(workOrder.getType()).setHardwarePlatform(workOrder.getHardwarePlatform()).setOperatingSystem(workOrder.getOperatingSystem()).setOperatingSystemVersion(workOrder.getOperatingSystemVersion()).setOperatingSystemLanguage(workOrder.getOperatingSystemLanguage()).setDeploymentWay(workOrder.getDeploymentWay()).setProductNameVersion(workOrder.getProductNameVersion()).setProblemType(workOrder.getProblemType()).setCsiNumber(workOrder.getCsiNumber());
        //工单状态、最后更新时间、工单所属三级分类id
        order.setStatus(StatusCodeUtil.WORK_ORDER_PROCESS_STATUS).setLastUpdateTime(DateUtil.now()).setCategoryId(engineerCreateWorkOrderReq.getCategoryId());
        //提交分裂工单
        int row = workOrderMapper.insert(order);
        if (row > 0) {
            //异步处理工单分配
            threadPoolTaskExecutor.execute(() -> distributionUtil.autoDistributionWorkOrderToEngineer(order.getId().toString()));
        }
        return row;
    }


    /**
     * 工程师升级工单
     *
     * @param engineerUpgradeWorkOrderReq 升级参数
     * @return int
     */
    @Override
    public int engineerUpgradeWorkOrder(EngineerUpgradeWorkOrderReq engineerUpgradeWorkOrderReq) {
        WorkOrder workOrder = workOrderMapper.selectById(engineerUpgradeWorkOrderReq.getWorkOrderId());
        if (workOrder == null) {
            return StatusCodeUtil.NOT_FOUND_FLAG;
        }
        workOrder.setUpgradeStatus(StatusCodeUtil.WORK_ORDER_UPGRADE_STATUS).setUpgradeReason(engineerUpgradeWorkOrderReq.getUpgradeReason());
        int row = workOrderMapper.updateById(workOrder);
        if (row > 0) {
            //发送告警信息到 manager
            threadPoolTaskExecutor.execute(() -> sendMessageUtil.sendManagerWorkOrderUpgradeAlarm(workOrder));
        }
        return row;
    }


    /**
     * 获取工程师类型工单列表
     *
     * @param engineerId 工程师id
     * @param tagName    标签名
     * @param pageNo     页号
     * @param pageSize   页面大小
     * @return map
     */
    @Override
    public Map<String, Object> getEngineerTypeWorkOrder(String engineerId, String tagName, Integer pageNo, Integer pageSize) {
        Map<String, Object> map = CollectionUtil.newHashMap();
        Integer workOrderStatus = Objects.equals("inWork", tagName) ? StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS : StatusCodeUtil.WORK_ORDER_CLOSE_STATUS;
        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<WorkOrder>().eq("engineer_id", engineerId).eq("status", workOrderStatus).orderByDesc("create_time");
        //工单列表
        List<WorkOrder> workOrderList = workOrderMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper).getRecords();
        map.put("voList", workOrderList);
        map.put("total", workOrderMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 获取用户历史提交的工单记录
     *
     * @param accountId 用户id
     * @param pageNo    页号
     * @param pageSize  页面大小
     * @return map
     */
    @Override
    public Map<String, Object> getAccountHistoryWorkOrderList(String accountId, Integer pageNo, Integer pageSize) {
        Map<String, Object> map = CollectionUtil.newHashMap();
        QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper<WorkOrder>().eq("account_id", accountId).orderByDesc("create_time");
        //工单列表
        List<WorkOrder> workOrderList = workOrderMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper).getRecords();
        if (CollectionUtil.isNotEmpty(workOrderList)) {
            for (WorkOrder workOrder : workOrderList) {
                Integer status = workOrder.getStatus();
                workOrder.setWorkOrderStatus(status == 1 ? "待处理" : status == 2 ? "处理中" : status == 3 ? "" : status == 4 ? "已关闭" : "提交结单申请");
            }
        }
        map.put("voList", workOrderList);
        map.put("total", workOrderMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 获取工单概况统计信息
     *
     * @return map
     */
    @Override
    public Map<String, Integer> getWorkOrderSurvey() {
        Map<String, Integer> map = CollectionUtil.newHashMap();
        //今日新增工单、处理中的工单、已处理工单、待处理的工单
        map.put("todayAddCount", workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().like("create_time", DateUtil.today())));
        //处理中的工单
        map.put("inApplyCount", workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("status", StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS)));
        //已处理工单
        map.put("hasDoCount", workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("status", StatusCodeUtil.WORK_ORDER_CLOSE_STATUS)));
        //昨天新增工单
        map.put("yesterdayAddCount", workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().like("create_time", DateUtil.yesterday().toString().substring(0, 10))));
        //待处理工单
        map.put("noApplyCount", workOrderMapper.selectCount(new QueryWrapper<WorkOrder>().eq("status", StatusCodeUtil.WORK_ORDER_PROCESS_STATUS)));
        return map;
    }

}
