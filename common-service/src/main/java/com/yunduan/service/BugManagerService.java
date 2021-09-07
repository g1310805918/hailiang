package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.BugManager;
import com.yunduan.request.front.document.BugManagerInitPageReq;
import com.yunduan.request.front.document.CreateBugFeedbackReq;
import com.yunduan.request.front.document.EngineerBugInitPageReq;
import com.yunduan.vo.BudDetailVo;

import java.util.Map;

public interface BugManagerService extends IService<BugManager> {


    /**
     * BUG审核管理页面统计
     * @return map
     */
    Map<String,Integer> queryBaseInfo();


    /**
     * bug页面初始化
     * @param bugManagerInitPageReq 筛选对象
     * @return map
     */
    Map<String,Object> queryInitPage(BugManagerInitPageReq bugManagerInitPageReq);


    /**
     * bug反馈详情
     * @param id id
     * @return BudDetailVo
     */
    BudDetailVo queryDetail(String id);


    /**
     * 查询普通工程师BUG反馈页面统计
     * @return map
     */
    Map<String,Integer> queryFeedbackBaseInfo();


    /**
     * 工程师bug反馈页面初始化
     * @param engineerBugInitPageReq 筛选对象
     * @return map
     */
    Map<String,Object> queryEngineerInitPage(EngineerBugInitPageReq engineerBugInitPageReq);


    /**
     * 添加bug反馈
     * @param createBugFeedbackReq 添加参数
     * @return int
     */
    int createBugFeedback(CreateBugFeedbackReq createBugFeedbackReq);
}


