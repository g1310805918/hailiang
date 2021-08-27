package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Account;
import com.yunduan.mapper.AccountMapper;
import com.yunduan.mapper.dao.AccountDao;
import com.yunduan.request.front.account.AccountReq;
import com.yunduan.request.front.account.RegisteredReq;
import com.yunduan.service.AccountService;
import com.yunduan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountMapper accountMapper;


    /**
     * 注册用户
     * @param registeredReq 注册对象
     * @return account
     */
    @Override
    public int registeredAccount(RegisteredReq registeredReq) {
        //查询用户、判断用户手机号和邮箱号是否存在
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("mobile",registeredReq.getMobile()).or().eq("email",registeredReq.getEmail()));
        if (account != null) {
            return StatusCodeUtil.HAS_EXIST;
        }
        account = new Account();
        account.setId(SnowFlakeUtil.getPrimaryKeyId()).setUsername(registeredReq.getUsername()).setMobile(registeredReq.getMobile()).setEmail(registeredReq.getEmail()).setPassword(AESUtil.encrypt(registeredReq.getPassword()));
        account.setHeadPic(StatusCodeUtil.HEAD_PIC).setCreateTime(new Date()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setToken(IdUtil.simpleUUID());
        return accountMapper.insert(account);
    }


    /**
     * 登录请求
     * @param accountReq 登录对象
     * @return account
     */
    @Override
    public Account accountLogin(AccountReq accountReq) {
        int type = MatchDataUtil.matchDataType(accountReq.getMobileOrEmail());
        //手机号登录
        Account account = accountMapper.selectOne(new QueryWrapper<Account>()
                .eq(type == 1,"mobile",accountReq.getMobileOrEmail())
                .eq(type == 2,"email",accountReq.getMobileOrEmail()));
        if (account != null) {
            return account;
        }
        return null;
    }


    /**
     * 更新用户token
     * @param id 用户id
     * @return int
     */
    @Override
    public Map<String,String> changeUserTokenForLogin(Long id) {
        Account account = accountDao.findById(id).get();
        if (account != null) {
            //删除之前token
            redisUtil.removeKey(StatusCodeUtil.ACCOUNT_TOKEN + account.getToken());
            String token = IdUtil.simpleUUID();
            account.setToken(token);
            int row = accountMapper.update(account, new QueryWrapper<Account>().eq("id", account.getId()));
            if (row > 0) {
                //保存redis中，设置有效期3天
                redisUtil.setStringKeyValue(StatusCodeUtil.ACCOUNT_TOKEN + token,account.getId().toString(),3, TimeUnit.DAYS);
                Map<String,String> map = new HashMap<>();
                map.put("token",token);
                map.put("isBind", StrUtil.hasEmpty(account.getOpenId()) ? "0" : "1");
                return map;
            }
        }
        return null;
    }



}
