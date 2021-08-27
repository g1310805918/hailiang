package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.AccountMsg;
import com.yunduan.request.front.message.InitListReq;
import com.yunduan.vo.MsgDetailVo;

import java.util.Map;

public interface AccountMsgService extends IService<AccountMsg> {


    /**
     * 用户消息列表
     * @param userId 用户id
     * @param initListReq 筛选对象
     * @return map
     */
    Map<String,Object> accountMsgListVo(Long userId, InitListReq initListReq);


    /**
     * 消息详情
     * @param msgId 消息id
     * @return MsgDetailVo
     */
    MsgDetailVo queryMsgById(String msgId);





}
