package com.yunduan.utils;

public class StatusCodeUtil {

    //未删除标志
    public static final Integer NOT_DELETE_FLAG = 0;

    //已删除标志
    public static final Integer DELETE_FLAG = 1;

    //成功状态
    public static final Integer SUCCESS_CODE = 200;

    //系统错误
    public static final Integer SYSTEM_ERROR = 500;

    //token失效
    public static final Integer TOKEN_FAILURE = 401;

    //空
    public static final Integer NOT_FOUND_FLAG = 404;

    //已存在
    public static final Integer HAS_EXIST = 8001;

    //公司CAU管理员未绑定CSI编号
    public static final Integer COMPANY_CSI_CAU_NO_BINDING = 8002;

    //验证消息标题
    public static final String SYS_MATH_MSG = "请求加入公司CSI验证";

    //默认头像
    public static final String HEAD_PIC = "http://qxedhpf35.hb-bkt.clouddn.com/head.png";

    //用户token前缀
    public static final String ACCOUNT_TOKEN = "account:token:";

    //工程师Token前缀
    public static final String ENGINEER_TOKEN = "engineer:token:";

    //用户手机验证码前缀
    public static final String VERIFICATION_CODE = "verification:code:";

    //手机号标志
    public static final Integer MOBILE_FLAG = 1;

    //邮箱标志
    public static final Integer EMAIL_FLAG = 2;

    //验证消息详情 - 用户名
    public static final String MSG_USERNAME = "username";

    //验证消息详情 - 手机号
    public static final String MSG_USER_MOBILE = "mobile";

    //验证消息详情 - ID账号
    public static final String MSG_ACCOUNT_ID = "accountId";

    //验证消息详情 - 申请时间
    public static final String MSG_APPLY_TIME = "applyTime";

    //验证消息详情 - 验证状态【1待审核、2审核通过、3拒绝】
    public static final String MSG_APPLY_STATUS = "applyStatus";

    //待处理工单
    public static final Integer WORK_ORDER_PROCESS_STATUS = 1;

    //已收藏工单
    public static final Integer WORK_ORDER_COLLECTION_STATUS = 1;

    //受理中工单
    public static final Integer WORK_ORDER_ACCEPT_STATUS = 2;

    //已关闭工单
    public static final Integer WORK_ORDER_CLOSE_STATUS = 4;

    //用户提交申请关闭工单状态
    public static final Integer WORK_ORDER_ACCOUNT_CLOSE_APPLY_STATUS = 5;


    public static final String VDM_CUSTOMER_PROBLEM_DESC = "客户问题描述";

    public static final String VDM_CUSTOMER_UPDATE = "客户已更新";

    public static final String VDM_PROBLEM_CLARIFY = "VDM问题澄清";

    public static final String VDM_PROBLEM_EVIDENCE = "VDM问题证据";

    public static final String VDM_PROBLEM_REASON = "VDM问题原因";

    public static final String VDM_DIAGNOSTIC_BASIS = "VDM诊断依据";

    public static final String VDM_SOLUTION = "VDM解决方案";

    public static final String VDM_PLAN_BASIS = "VDM方案依据";

    public static final String VDM_DATA_COLLECTION = "VDM数据收集";

    public static final String VDM_ANALYSE_RESEARCH = "VDM分析研究";


    //工程师账号 正常状态
    public static final Integer ENGINEER_ACCOUNT_NORMAL_STATUS = 1;

    //工程师账号 禁用状态
    public static final Integer ENGINEER_ACCOUNT_DISABLE_STATUS = 2;

    //工程师在线状态
    public static final Integer ENGINEER_ACCOUNT_ONLINE_STATUS = 1;

    //工程师离线状态
    public static final Integer ENGINEER_ACCOUNT_OFFLINE_STATUS = 2;


}
