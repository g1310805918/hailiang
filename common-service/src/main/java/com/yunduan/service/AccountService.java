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


    /**
     * 查询用户个人中心基本信息
     * @return Account
     */
    Account queryAccountBaseInfo();


    /**
     * 解除绑定CSI
     * @param bindingId 绑定id
     * @return int
     */
    int unBindingCSI(String bindingId);


    /**
     * 编辑用户名
     * @param accountId 用户id
     * @param username 用户名
     * @return int
     */
    int changeUsername(String accountId,String username);


    /**
     * 修改手机号
     * @param accountId 用户id
     * @param oldMobile 旧手机号
     * @param newMobile 新手机号
     * @return int
     */
    int changeMobile(String accountId,String oldMobile, String newMobile);


    /**
     * 修改头像
     * @param accountId 用户id
     * @param headPic 头像
     * @return int
     */
    int changeHeadPic(String accountId,String headPic);


    /**
     * 获取所有已经注册的用户列表
     * @param username 用户名
     * @param mobile 手机号
     * @param email 邮箱
     * @param pageNo 页号
     * @param pageSize 页面大小
     * @return map
     */
    Map<String,Object> queryAllRegisteredAccountList(String username, String mobile, String email, Integer pageNo, Integer pageSize);


    /**
     * 获取用户概况基本信息
     * @return map
     */
    Map<String,Integer> getAccountSurveyBaseInfo();


    /**
     * 获取用户概况柱状图
     * @return map
     */
    Map<String,Object> getAccountSurveyBarInfo();
}
