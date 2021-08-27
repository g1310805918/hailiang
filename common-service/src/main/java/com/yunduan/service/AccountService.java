package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.Account;
import com.yunduan.request.front.account.AccountReq;
import com.yunduan.request.front.account.RegisteredReq;

import java.util.Map;

public interface AccountService extends IService<Account> {


    /**
     * 注册用户
     * @param registeredReq 注册对象
     * @return account
     */
    int registeredAccount(RegisteredReq registeredReq);


    /**
     * 登录请求
     * @param accountReq 登录对象
     * @return account
     */
    Account accountLogin(AccountReq accountReq);


    /**
     * 更新用户token
     * @param id 用户id
     * @return int
     */
    Map<String,String> changeUserTokenForLogin(Long id);


}
