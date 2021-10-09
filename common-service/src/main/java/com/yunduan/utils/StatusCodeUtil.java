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
    public static final String HEAD_PIC = "http://mvs.vastdata.com.cn/e1fe9925b.jpeg";

    //用户token前缀
    public static final String ACCOUNT_TOKEN = "account:token:";

    //默认Token
    public static final String DEFAULT_TOKEN = "yun";

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

    //工单已升级状态
    public static final Integer WORK_ORDER_UPGRADE_STATUS = 1;

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


    //首页用户概况柱状图数据统计
    public static final String ACCOUNT_BAR_DATA = "account:bar:info";

    //普通海量员工身份
    public static final Integer ACCOUNT_IDENTITY_FOR_NORMAL = 1;

    //工程师账号 正常状态
    public static final Integer ENGINEER_ACCOUNT_NORMAL_STATUS = 1;

    //工程师账号 禁用状态
    public static final Integer ENGINEER_ACCOUNT_DISABLE_STATUS = 2;

    //工程师在线状态
    public static final Integer ENGINEER_ACCOUNT_ONLINE_STATUS = 1;

    //工程师离线状态
    public static final Integer ENGINEER_ACCOUNT_OFFLINE_STATUS = 2;


    //工程师处理流程 -- 复杂方式
    public static final String PROCESSING_FLOW_1_1 = "1-1";  //问题澄清
    public static final String PROCESSING_FLOW_1_2 = "1-2";  //问题证据
    public static final String PROCESSING_FLOW_1_3 = "1-3";  //问题原因
    public static final String PROCESSING_FLOW_1_4 = "1-4";  //诊断依据
    public static final String PROCESSING_FLOW_1_5 = "1-5";  //解决方案
    public static final String PROCESSING_FLOW_1_6 = "1-6";  //方案依据
    public static final String PROCESSING_FLOW_1_7 = "1-7";  //文档管理
    //工程师处理流程 -- 简单方式
    public static final String PROCESSING_FLOW_2_1 = "2-1";  //问题
    public static final String PROCESSING_FLOW_2_2 = "2-2";  //回答
    public static final String PROCESSING_FLOW_2_3 = "2-3";  //文档管理



    //bug审核管理文档状态
    public static final Integer BUG_DOC_NO_REVIEW_STATUS = 1;
    public static final Integer BUG_DOC_PASS_REVIEW_STATUS = 2;
    public static final Integer BUG_DOC_NO_PASS_REVIEW_STATUS = 3;


    //文档不存在
    public static final Integer DOCUMENT_NOT_EXIST = 8003;


    //消息未读标志
    public static final Integer MESSAGE_NO_READ_FLAG = 0;
    //消息已读标志
    public static final Integer MESSAGE_HAS_READ_FLAG = 0;


    //工程师工单消息
    public static final Integer ENGINEER_MESSAGE_TYPE_WORK_ORDER = 1;
    //工程师文档消息
    public static final Integer ENGINEER_MESSAGE_TYPE_DOCUMENT = 2;
    //工程师bug消息
    public static final Integer ENGINEER_MESSAGE_TYPE_BUG = 3;
    //工程师系统消息
    public static final Integer ENGINEER_MESSAGE_TYPE_SYSTEM = 4;

    //用户放弃解决
    public static final String ACCOUNT_GIVE_UP_WORK_ORDER = "放弃解决";


}
