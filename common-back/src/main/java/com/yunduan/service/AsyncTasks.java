package com.yunduan.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Account;
import com.yunduan.entity.AccountMsg;
import com.yunduan.entity.SysMessage;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 使用线程池处理业务
 * 工具类
 */
@Component
public class AsyncTasks {

    private static final transient Logger log = LoggerFactory.getLogger(AsyncTasks.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountMsgService accountMsgService;


    /**
     * 向用户添加系统消息
     * @param sysMessage 系统消息
     */
    @Async("threadPoolTaskExecutor")
    public void doSaveSysMessage(SysMessage sysMessage) {
        if (sysMessage == null) {
            log.error("线程池业务--->>>向用户添加系统消息 【sysMessage】为空");
            return;
        }
        //所有用户列表
        List<Account> accountList = accountService.list();
        if (CollectionUtil.isNotEmpty(accountList)) {
            AccountMsg msg = null;
            //最终需要插入的结果集合
            List<AccountMsg> accountMsgList = new ArrayList<>(accountList.size());
            for (Account account : accountList) {
                //id、用户id、消息类型、是否已读、删除标志、添加时间、消息标题、消息内容、更新时间
                msg = new AccountMsg().setId(SnowFlakeUtil.getPrimaryKeyId()).setAccountId(account.getId()).setMsgType(1).setIsRead(0).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setCreateTime(DateUtil.now()).setMsgTitle(sysMessage.getTitle()).setMsgContent(sysMessage.getContent()).setUpdateTime(DateUtil.now());
                accountMsgList.add(msg);
            }
            //批量插入数据
            accountMsgService.saveBatch(accountMsgList);
        }
    }


    /**
     * 更新用户下的系统消息
     * @param messageTitle 消息标题
     * @param sysMessage 消息内容
     */
    @Async("threadPoolTaskExecutor")
    public void doEditAccountSysMessage(String messageTitle,SysMessage sysMessage) {
        List<AccountMsg> msgList = accountMsgService.list(new QueryWrapper<AccountMsg>().eq("msg_type", 1).eq("msg_title", messageTitle));
        if (CollectionUtil.isNotEmpty(msgList)) {
            for (AccountMsg msg : msgList) {
                msg.setMsgTitle(sysMessage.getTitle()).setMsgContent(sysMessage.getContent()).setUpdateTime(DateUtil.now());
            }
            accountMsgService.updateBatchById(msgList);
        }
    }


}
