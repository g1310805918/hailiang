package com.yunduan.serviceimpl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Account;
import com.yunduan.entity.BindingAccountCSI;
import com.yunduan.entity.CollectionAccountDocument;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.mapper.*;
import com.yunduan.mapper.dao.AccountDao;
import com.yunduan.request.front.account.AccountReq;
import com.yunduan.request.front.account.RegisteredReq;
import com.yunduan.service.AccountService;
import com.yunduan.utils.*;
import com.yunduan.vo.AccountBindingCSI;
import com.yunduan.vo.FavoritesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.util.*;
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
    @Autowired
    private CollectionFavoritesMapper collectionFavoritesMapper;
    @Autowired
    private CollectionAccountDocumentMapper collectionAccountDocumentMapper;
    @Autowired
    private BindingAccountCSIMapper bindingAccountCSIMapper;


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


    /**
     * 查询用户个人中心基本信息
     * @return Account
     */
    @Override
    public Account queryAccountBaseInfo() {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", ContextUtil.getUserId()));
        if (account != null) {
            //获取用户收藏夹
            List<FavoritesVo> favorites = getFavorites(account);
            //设置收藏夹
            account.setFavoritesVoList(favorites);
            //设置用户绑定CSI记录列表
            List<AccountBindingCSI> bindingCSIS = bindingAccountCSIMapper.selectAccountBindingRecord(account.getId().toString());
            account.setBindingCSIList(bindingCSIS);
            return account;
        }
        return null;
    }

    /**
     * 获取用户收藏夹列表
     * @param account 当前用户
     * @return list
     */
    public List<FavoritesVo> getFavorites(Account account) {
        //收藏夹集合
        List<FavoritesVo> favoritesVoList = new ArrayList<>();
        //添加默认收藏夹
        FavoritesVo vo = new FavoritesVo();
        vo.setId(null).setCount(collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", account.getId()).eq("favorites_id", null))).setFavoritesName("默认收藏夹");
        favoritesVoList.add(vo);
        //查询用户所有收藏夹
        List<CollectionFavorites> favoritesList = collectionFavoritesMapper.selectList(new QueryWrapper<CollectionFavorites>().eq("account_id", account.getId()));
        if (favoritesList.size() > 0 && favoritesList != null) {
            //得到用户创建收藏夹
            for (CollectionFavorites collectionFavorites : favoritesList) {
                vo = new FavoritesVo();
                vo.setId(collectionFavorites.getId().toString()).setFavoritesName(collectionFavorites.getFavoritesName());
                //收藏夹下的文档总数
                Integer total = collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", account.getId()).eq("favorites_id", collectionFavorites.getId()).orderByDesc("create_time"));
                vo.setCount(total);
                favoritesVoList.add(vo);
            }
        }
        return favoritesVoList;
    }




    /**
     * 解除绑定CSI
     * @param bindingId 绑定id
     * @return int
     */
    @Override
    public int unBindingCSI(String bindingId) {
        BindingAccountCSI one = bindingAccountCSIMapper.selectOne(new QueryWrapper<BindingAccountCSI>().eq("id", bindingId));
        if (one != null) {
            return bindingAccountCSIMapper.deleteById(one.getId());
        }
        return 0;
    }


    /**
     * 编辑用户名
     * @param accountId 用户id
     * @param username 用户名
     * @return int
     */
    @Override
    public int changeUsername(String accountId, String username) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", accountId));
        if (account != null) {
            account.setUsername(username);
            return accountMapper.update(account,new QueryWrapper<Account>().eq("id",account.getId()));
        }
        return 0;
    }


    /**
     * 修改手机号
     * @param accountId 用户id
     * @param mobile 新手机号
     * @return int
     */
    @Override
    public int changeMobile(String accountId, String mobile) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("mobile", mobile));
        if (account != null) {
            account.setMobile(mobile);
            return accountMapper.update(account, new QueryWrapper<Account>().eq("id", account.getId()));
        }
        return 0;
    }


    /**
     * 修改头像
     * @param accountId 用户id
     * @param headPic 头像
     * @return int
     */
    @Override
    public int changeHeadPic(String accountId, String headPic) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", accountId));
        if (account != null) {
            account.setHeadPic(StrUtil.hasEmpty(headPic) ? account.getHeadPic() : headPic);
            return accountMapper.update(account,new QueryWrapper<Account>().eq("id",account.getId()));
        }
        return 0;
    }

}
