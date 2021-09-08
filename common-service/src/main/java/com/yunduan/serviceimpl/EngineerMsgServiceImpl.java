package com.yunduan.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.BugManager;
import com.yunduan.entity.EngineerMsg;
import com.yunduan.entity.KnowledgeDocumentNoPass;
import com.yunduan.entity.WorkOrder;
import com.yunduan.mapper.*;
import com.yunduan.request.front.message.EngineerInitPageReq;
import com.yunduan.service.EngineerMsgService;
import com.yunduan.service.KnowledgeDocumentNoPassService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.vo.EngineerMsgDetailVo;
import com.yunduan.vo.EngineerMsgInitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EngineerMsgServiceImpl extends ServiceImpl<EngineerMsgMapper, EngineerMsg> implements EngineerMsgService {

    @Autowired
    private EngineerMsgMapper engineerMsgMapper;
    @Autowired
    private EngineerMapper engineerMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired
    private KnowledgeDocumentNoPassMapper knowledgeDocumentNoPassMapper;
    @Autowired
    private BugManagerMapper bugManagerMapper;




    /**
     * 工程师端消息列表初始化
     * @param engineerInitPageReq 筛选对象
     * @return list
     */
    @Override
    public List<EngineerMsgInitVo> queryScreenMessageList(EngineerInitPageReq engineerInitPageReq) {
        List<EngineerMsgInitVo> voList = new ArrayList<>();
        //筛选的天数
        Integer days = engineerInitPageReq.getDays();
        //筛选的开始时间
        String startDays = "";
        //筛选的结束时间
        String endDays = DateUtil.now();
        if (days != null) {
            if (days == 0) {
                startDays = DateUtil.today() + " 00:00:00";
            }else if (days == 7) {
                startDays = DateUtil.offsetDay(DateUtil.parseDate(DateUtil.today()),-7).toString();
            }else {
                startDays = DateUtil.offsetDay(DateUtil.parseDate(DateUtil.today()),-30).toString();
            }
        }
        //筛选条件
        QueryWrapper<EngineerMsg> queryWrapper = new QueryWrapper<EngineerMsg>()
                .eq("engineer_id", ContextUtil.getUserId())
                .eq(engineerInitPageReq.getMessageType() != null, "msg_type", engineerInitPageReq.getMessageType())
                .eq(engineerInitPageReq.getIsRead() != null, "is_read", engineerInitPageReq.getIsRead())
                .ge(StrUtil.isNotEmpty(startDays), "create_time", startDays)
                .lt(StrUtil.isNotEmpty(startDays), "create_time", endDays);
        //消息列表
        List<EngineerMsg> msgList = engineerMsgMapper.selectPage(new Page<>(engineerInitPageReq.getPageNo(), engineerInitPageReq.getPageSize()), queryWrapper).getRecords();
        if (msgList.size() > 0 && msgList != null) {
            EngineerMsgInitVo vo = null;
            for (EngineerMsg engineerMsg : msgList) {
                String time = engineerMsg.getCreateTime().replaceAll("-", "/").substring(5, 16);
                vo = new EngineerMsgInitVo().setId(engineerMsg.getId().toString()).setTitle(engineerMsg.getMsgTitle()).setContent(engineerMsg.getMsgContent()).setIsRead(engineerMsg.getIsRead()).setCreateTime(time).setMsgType(engineerMsg.getMsgType()).setTypeId(engineerMsg.getTypeId().toString());
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 消息详情
     * @param id 消息id
     * @return EngineerMsgDetailVo
     */
    @Override
    public EngineerMsgDetailVo queryMsgDetail(String id) {
        EngineerMsg msg = engineerMsgMapper.selectById(id);
        if (msg != null) {
            EngineerMsgDetailVo vo = new EngineerMsgDetailVo();
            //更新消息已读
            msg.setIsRead(1);
            int row = engineerMsgMapper.updateById(msg);
            if (row > 0) {
                //封装详情信息
                vo.setId(msg.getId().toString()).setTypeId(msg.getTypeId().toString()).setMsgType(msg.getMsgType());
                if (msg.getMsgType() == 1) {  //工单消息
                    WorkOrder workOrder = workOrderMapper.selectById(msg.getTypeId());
                    if (workOrder != null) {
                        //发起人
                        vo.setUsername(accountMapper.selectById(workOrder.getAccountId()).getUsername());
                        //问题概要
                        vo.setProblemProfile(workOrder.getProblemProfile());
                    }
                }else if (msg.getMsgType() == 2) {  //文档消息
                    KnowledgeDocumentNoPass document = knowledgeDocumentNoPassMapper.selectById(msg.getTypeId());
                    if (document != null) {
                        vo.setUsername(engineerMapper.selectById(document.getEngineerId()).getUsername());
                        vo.setProblemProfile(document.getDocTitle());
                    }
                }else {  //bug消息
                    BugManager bugManager = bugManagerMapper.selectById(msg.getTypeId());
                    if (bugManager != null) {
                        //发起工程师用户名
                        vo.setUsername(engineerMapper.selectById(bugManager.getEngineerId()).getUsername());
                        //bug标题
                        vo.setProblemProfile(bugManager.getBugTitle());
                    }
                }
                vo.setTitle(msg.getMsgTitle()).setContent(msg.getMsgContent()).setCreateTime(msg.getCreateTime().substring(0,16));
            }
            return vo;
        }
        return null;
    }


}
