package com.yunduan.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.*;
import com.yunduan.service.BugManagerService;
import com.yunduan.service.EngineerMsgService;
import com.yunduan.service.EngineerService;
import com.yunduan.service.KnowledgeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 发送消息工具类
 */
@Component
public class SendMessageUtil {

    private EngineerMsg msg = null;
    @Autowired
    private EngineerMsgService engineerMsgService;
    @Autowired
    private BugManagerService bugManagerService;
    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;
    @Autowired
    private EngineerService engineerService;


    /**
     * 系统发送工单消息给工程师
     * @param workOrder 工程师被分配的工单
     */
    public void sendWorkOrderMessage(WorkOrder workOrder) {
        msg = new EngineerMsg().setEngineerId(workOrder.getEngineerId()).setMsgType(StatusCodeUtil.ENGINEER_MESSAGE_TYPE_WORK_ORDER).setTypeId(workOrder.getId()).setMsgTitle("工单待处理通知").setMsgContent("您被分配了一个新的工单 " + workOrder.getOutTradeNo() + " 等待您的反馈");
        sendMessage(msg);
    }


    /**
     * COE管理员发送文档审核通过消息
     * @param documentId 文档id
     */
    public void sendDocumentPassMessage(String documentId) {
        KnowledgeDocument document = knowledgeDocumentService.getById(documentId);
        msg = new EngineerMsg().setEngineerId(document.getEngineerId()).setMsgType(StatusCodeUtil.ENGINEER_MESSAGE_TYPE_DOCUMENT).setTypeId(Convert.toLong(documentId)).setMsgTitle("文档审核结果通知").setMsgContent("您提交到COE等待审核的文档已经通过了");
        sendMessage(msg);
    }


    /**
     * COE管理员发送文档审核拒绝消息
     * @param documentId 文档id
     */
    public void sendDocumentRefusedMessage(String documentId) {
        KnowledgeDocument document = knowledgeDocumentService.getById(documentId);
        msg = new EngineerMsg().setEngineerId(document.getEngineerId()).setMsgType(StatusCodeUtil.ENGINEER_MESSAGE_TYPE_DOCUMENT).setTypeId(Convert.toLong(documentId)).setMsgTitle("文档审核结果通知").setMsgContent("您提交到COE等待审核的文档已被拒绝");
        sendMessage(msg);
    }


    /**
     * BDE发送文档审核结果通知给工程师
     * @param bugManagerId bug反馈记录id
     */
    public void sendBUGReviewPassMessage(String bugManagerId) {
        BugManager bugManager = bugManagerService.getById(bugManagerId);
        msg = new EngineerMsg().setEngineerId(bugManager.getEngineerId()).setMsgType(StatusCodeUtil.ENGINEER_MESSAGE_TYPE_BUG).setTypeId(Convert.toLong(bugManagerId)).setMsgTitle("BUG反馈结果通知").setMsgContent("您反馈的BUG ：" + (StrUtil.hasEmpty(bugManager.getBugTitle()) ? "" : bugManager.getBugTitle()) + "。已通过审核！");
        sendMessage(msg);
    }



    /**
     * BDE发送文档审核结果通知给工程师
     * @param bugManagerId bug反馈记录id
     */
    public void sendBUGReviewRefusedMessage(String bugManagerId) {
        BugManager bugManager = bugManagerService.getById(bugManagerId);
        msg = new EngineerMsg().setEngineerId(bugManager.getEngineerId()).setMsgType(StatusCodeUtil.ENGINEER_MESSAGE_TYPE_BUG).setTypeId(Convert.toLong(bugManagerId)).setMsgTitle("BUG反馈结果通知").setMsgContent("您反馈的BUG ：" + (StrUtil.hasEmpty(bugManager.getBugTitle()) ? "" : bugManager.getBugTitle()) + "。审核已被拒绝！");
        sendMessage(msg);
    }


    /**
     * 工程师发布知识文档时发送给COE工程师的消息
     * @param documentId 文档id
     */
    public void engineerSendNormalDocumentReviewApplyToCOE(String documentId) {
        //工程师列表
        List<Engineer> engineerList = engineerService.list(new QueryWrapper<Engineer>().eq("identity", 3).eq("account_status", StatusCodeUtil.ENGINEER_ACCOUNT_NORMAL_STATUS));
        if (engineerList.size() > 0 && engineerList != null) {
            engineerList.forEach(item -> {
                msg = new EngineerMsg().setEngineerId(item.getId()).setMsgType(StatusCodeUtil.ENGINEER_MESSAGE_TYPE_DOCUMENT).setTypeId(Convert.toLong(documentId)).setMsgTitle("文档审核通知").setMsgContent("你有一个新的文档审核申请，等待您的审核。");
                sendMessage(msg);
            });
        }
    }


    /**
     * 工程师发送bug审核申请消息至BDE工程师
     * @param bugManagerId bug文档id
     */
    public void sendBUGApplyToBDE(String bugManagerId) {
        //BDE工程师列表
        List<Engineer> engineerList = engineerService.list(new QueryWrapper<Engineer>().eq("identity", 4).eq("account_status", StatusCodeUtil.ENGINEER_ACCOUNT_NORMAL_STATUS));
        if (engineerList.size() > 0 && engineerList != null) {
            engineerList.forEach(item -> {
                msg = new EngineerMsg().setEngineerId(item.getId()).setMsgType(StatusCodeUtil.ENGINEER_MESSAGE_TYPE_BUG).setTypeId(Convert.toLong(bugManagerId)).setMsgTitle("BUG审核通知").setMsgContent("您有一个新的BUG审核申请待审核。");
                sendMessage(msg);
            });
        }
    }


    /**
     * 公共保存消息方法
     * @param msg 消息对象
     */
    protected void sendMessage(EngineerMsg msg) {
        engineerMsgService.save(msg);
    }


}
