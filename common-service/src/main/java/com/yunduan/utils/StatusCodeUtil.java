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

    //默认头像
    public static final String HEAD_PIC = "http://qxedhpf35.hb-bkt.clouddn.com/head.png";

    //用户token前缀
    public static final String ACCOUNT_TOKEN = "account:token:";

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
}
