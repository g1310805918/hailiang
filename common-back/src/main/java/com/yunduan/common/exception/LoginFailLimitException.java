package com.yunduan.common.exception;


import org.springframework.security.authentication.InternalAuthenticationServiceException;

/**
 * @author Victor
 */
public class LoginFailLimitException extends InternalAuthenticationServiceException {

    private String msg;

    public LoginFailLimitException(String msg) {
        super(msg);
        setMsg(msg);
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
