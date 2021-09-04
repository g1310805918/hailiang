package com.yunduan.common.constant;

/**
 * @author Victor
 */
public interface SecurityConstant {

    /**
     * token分割
     */
    String TOKEN_SPLIT = "Bearer ";

    /**
     * JWT签名加密key
     */
    String JWT_SIGN_KEY = "ningbo";

    /**
     * token参数头
     */
    String HEADER = "accessToken";

    /**
     * 权限参数头
     */
    String AUTHORITIES = "authorities";

    /**
     * 用户选择JWT保存时间参数头
     */
    String SAVE_LOGIN = "saveLogin";

    /**
     * github保存state前缀key
     */
    String GITHUB_STATE = "HIGHSET_GITHUB:";

    /**
     * qq保存state前缀key
     */
    String QQ_STATE = "HIGHSET_QQ:";

    /**
     * qq保存state前缀key
     */
    String WEIBO_STATE = "HIGHSET_WEIBO:";

    /**
     * 交互token前缀key
     */
    String TOKEN_PRE = "HIGHSET_TOKEN_PRE:";

    /**
     * 用户token前缀key 单点登录使用
     */
    String USER_TOKEN = "HIGHSET_USER_TOKEN:";
}
