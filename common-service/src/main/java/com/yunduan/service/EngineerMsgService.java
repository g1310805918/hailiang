package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.EngineerMsg;
import com.yunduan.request.front.message.EngineerInitPageReq;
import com.yunduan.vo.EngineerMsgDetailVo;
import com.yunduan.vo.EngineerMsgInitVo;

import java.util.List;


public interface EngineerMsgService extends IService<EngineerMsg> {


    /**
     * 工程师端消息列表初始化
     * @param engineerInitPageReq 筛选对象
     * @return list
     */
    List<EngineerMsgInitVo> queryScreenMessageList(EngineerInitPageReq engineerInitPageReq);


    /**
     * 消息详情
     * @param id 消息id
     * @return EngineerMsgDetailVo
     */
    EngineerMsgDetailVo queryMsgDetail(String id);
}
