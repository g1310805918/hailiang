package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.SysMessage;

import java.util.Map;

public interface SysMessageService extends IService<SysMessage> {


    /**
     * 初始化消息页面
     * @param title 消息标题
     * @param pageNo 页号
     * @param pageSize 页面大小
     * @return map
     */
    Map<String,Object> initPageData(String title, Integer pageNo, Integer pageSize);


    /**
     * 发布系统公告
     * @param title 系统标题
     * @param content 内容
     * @return SysMessage
     */
    SysMessage createSysMessage(String title,String content);
}
