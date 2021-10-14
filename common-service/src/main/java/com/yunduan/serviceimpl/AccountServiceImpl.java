package com.yunduan.serviceimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.*;
import com.yunduan.mapper.*;
import com.yunduan.mapper.dao.AccountDao;
import com.yunduan.request.front.account.AccountReq;
import com.yunduan.request.front.account.RegisteredReq;
import com.yunduan.service.AccountService;
import com.yunduan.service.BindingAccountCSIService;
import com.yunduan.utils.*;
import com.yunduan.vo.AccountBindingCSI;
import com.yunduan.vo.FavoritesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
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
    @Autowired
    private CompanyCSIMapper companyCSIMapper;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private BindingAccountCSIService bindingAccountCSIService;


    /**
     * 注册用户
     *
     * @param registeredReq 注册对象
     * @return account
     */
    @Override
    public int registeredAccount(RegisteredReq registeredReq) {
        //查询用户、判断用户手机号和邮箱号是否存在
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("mobile", registeredReq.getMobile()).or().eq("email", registeredReq.getEmail()));
        if (account != null) {
            return StatusCodeUtil.HAS_EXIST;
        }
        account = new Account();
        String accountId = SnowFlakeUtil.getPrimaryKeyId().toString();
        account.setId(Convert.toLong(accountId)).setUsername(registeredReq.getUsername()).setMobile(registeredReq.getMobile()).setEmail(registeredReq.getEmail()).setPassword(AESUtil.encrypt(registeredReq.getPassword()));
        account.setHeadPic(StatusCodeUtil.HEAD_PIC).setCreateTime(new Date()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setToken(IdUtil.simpleUUID());
        //CSI编号不为空
        Optional.ofNullable(registeredReq.getCSINumber()).ifPresent(csiNumber -> sendRegisterBindingCSIRecordApply(accountId, csiNumber));
        return accountMapper.insert(account);
    }

    /**
     * 注册用户绑定CSI号提交申请
     * 向CAU管理员提交申请
     *
     * @param csiNumber CSI编号
     */
    private void sendRegisterBindingCSIRecordApply(String accountId, String csiNumber) {
        //公司CSI记录
        CompanyCSI companyCSI = companyCSIMapper.selectOne(new QueryWrapper<CompanyCSI>().eq("csi_number", csiNumber));
        Optional.ofNullable(companyCSI).ifPresent(item -> {
            threadPoolTaskExecutor.execute(() -> bindingAccountCSIService.addBindingCSI(accountId, csiNumber));
        });
    }


    /**
     * 登录请求
     *
     * @param accountReq 登录对象
     * @return account
     */
    @Override
    public Account accountLogin(AccountReq accountReq) {
        int type = MatchDataUtil.matchDataType(accountReq.getMobileOrEmail());
        //手机号登录
        Account account = accountMapper.selectOne(new QueryWrapper<Account>()
                .eq(type == 1, "mobile", accountReq.getMobileOrEmail())
                .eq(type == 2, "email", accountReq.getMobileOrEmail()));
        if (account != null) {
            return account;
        }
        return null;
    }


    /**
     * 更新用户token
     *
     * @param id 用户id
     * @return int
     */
    @Override
    public Map<String, String> changeUserTokenForLogin(Long id) {
        Account account = accountDao.findById(id).get();
        if (account != null) {
            //删除之前token
            redisUtil.removeKey(StatusCodeUtil.ACCOUNT_TOKEN + account.getToken());
            String token = IdUtil.simpleUUID();
            account.setToken(token);
            int row = accountMapper.update(account, new QueryWrapper<Account>().eq("id", account.getId()));
            if (row > 0) {
                //保存redis中，设置有效期3天
                redisUtil.setStringKeyValue(StatusCodeUtil.ACCOUNT_TOKEN + token, account.getId().toString(), 3, TimeUnit.DAYS);
                Map<String, String> map = new HashMap<>();
                map.put("token", token);
                map.put("isBind", StrUtil.hasEmpty(account.getOpenId()) ? "0" : "1");
                map.put("username",account.getUsername());
                map.put("headPic",account.getHeadPic());
                return map;
            }
        }
        return null;
    }


    /**
     * 查询用户个人中心基本信息
     *
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
     *
     * @param account 当前用户
     * @return list
     */
    public List<FavoritesVo> getFavorites(Account account) {
        //收藏夹集合
        List<FavoritesVo> favoritesVoList = new ArrayList<>();
        //添加默认收藏夹
        FavoritesVo vo = new FavoritesVo();
        vo.setId(null).setCount(collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", account.getId()).eq("favorites_id", 0))).setFavoritesName("默认收藏夹");
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
     *
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
     *
     * @param accountId 用户id
     * @param username  用户名
     * @return int
     */
    @Override
    public int changeUsername(String accountId, String username) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", accountId));
        if (account != null) {
            account.setUsername(username);
            return accountMapper.update(account, new QueryWrapper<Account>().eq("id", account.getId()));
        }
        return 0;
    }


    /**
     * 修改手机号
     *
     * @param accountId 用户id
     * @param oldMobile 旧手机号
     * @param newMobile 新手机号
     * @return int
     */
    @Override
    public int changeMobile(String accountId, String oldMobile, String newMobile) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("mobile", oldMobile));
        if (account != null) {
            account.setMobile(newMobile);
            return accountMapper.updateById(account);
        }
        return 0;
    }


    /**
     * 修改头像
     *
     * @param accountId 用户id
     * @param headPic   头像
     * @return int
     */
    @Override
    public int changeHeadPic(String accountId, String headPic) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", accountId));
        if (account != null) {
            account.setHeadPic(StrUtil.hasEmpty(headPic) ? account.getHeadPic() : headPic);
            return accountMapper.update(account, new QueryWrapper<Account>().eq("id", account.getId()));
        }
        return 0;
    }


    /**
     * 获取所有已经注册的用户列表
     *
     * @param username 用户名
     * @param mobile   手机号
     * @param email    邮箱
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @return map
     */
    @Override
    public Map<String, Object> queryAllRegisteredAccountList(String username, String mobile, String email, Integer pageNo, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        //条件构造器
        QueryWrapper<Account> queryWrapper = new QueryWrapper<Account>()
                .like(StrUtil.isNotEmpty(username), "username", username)
                .like(StrUtil.isNotEmpty(mobile), "mobile", mobile)
                .like(StrUtil.isNotEmpty(email), "email", email).orderByDesc("create_time");
        //用户列表
        List<Account> accountList = accountMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper).getRecords();
        if (CollectionUtil.isNotEmpty(accountList)) {
            for (Account account : accountList) {
                account.setCreateDateTime(DateUtil.formatDateTime(account.getCreateTime()));
            }
        }

        //总记录条数
        Integer total = accountMapper.selectCount(queryWrapper);

        map.put("voList", accountList);
        map.put("total", total);
        return map;
    }


    /**
     * 获取用户概况基本信息
     *
     * @return map
     */
    @Override
    public Map<String, Integer> getAccountSurveyBaseInfo() {
        Map<String, Integer> map = CollectionUtil.newHashMap();
        //今日新增用户数
        Integer todayCount = accountMapper.selectCount(new QueryWrapper<Account>().like("create_time", DateUtil.today()));
        //昨天新增用户数
        Integer yesterdayCount = accountMapper.selectCount(new QueryWrapper<Account>().like("create_time", DateUtil.yesterday().toString().substring(0, 10)));
        //今日新增CSI用户数
        Integer todayCSIAccount = getTodayCSIAccount(DateUtil.today());
        //今日新增普通用户数
        Integer todayAddNormalAccount = todayCount - todayCSIAccount;
        //昨天新增CSI用户数
        Integer yesterdayAddCSICount = getTodayCSIAccount(DateUtil.yesterday().toString().substring(0, 10));
        //昨天新增普通用户数
        Integer yesterdayAddNormalCount = yesterdayCount - yesterdayAddCSICount;
        //本月新增用户数
        Integer monthAccountCount = accountMapper.selectCount(new QueryWrapper<Account>().like("create_time", getThisYearMonth()));
        //本月新增CSI用户数
        Integer monthCSIAccountCount = getTodayCSIAccount(getThisYearMonth());
        //本月新增普通用户数
        Integer monthNormalAccountCount = monthAccountCount - monthCSIAccountCount;

        map.put("todayCount", todayCount);
        map.put("yesterdayCount", yesterdayCount);
        map.put("todayCSIAccount", todayCSIAccount);
        map.put("todayAddNormalAccount", todayAddNormalAccount);
        map.put("yesterdayAddCSICount", yesterdayAddCSICount);
        map.put("yesterdayAddNormalCount", yesterdayAddNormalCount);
        map.put("monthAccountCount", monthAccountCount);
        map.put("monthCSIAccountCount", monthCSIAccountCount);
        map.put("monthNormalAccountCount", monthNormalAccountCount);
        return map;
    }


    /**
     * 获取当前年月
     *
     * @return String - 2021-09
     */
    protected String getThisYearMonth() {
        StringBuilder builder = new StringBuilder();
        int year = DateUtil.thisYear();  //当前年
        int month = DateUtil.thisMonth() + 1;  //当前月
        if (month < 10) {
            builder.append(year).append("-0").append(month);
        } else {
            builder.append(year).append("-").append(month);
        }
        return builder.toString();
    }


    /**
     * 今日新增CSI用户数
     *
     * @return Integer
     */
    protected Integer getTodayCSIAccount(String date) {
        int total = bindingAccountCSIMapper.selectCount(new QueryWrapper<BindingAccountCSI>().eq("status", 2).like("create_time", date));
        return total;
    }


    /**
     * 获取当前月的最后一天
     *
     * @return String
     */
    protected String getLastDayOfMonth() {
        //当前月份
        int month = DateUtil.thisMonth();
        Calendar calendar = Calendar.getInstance();
        // 设置月份
        calendar.set(Calendar.MONTH, month);
        // 获取某月最大天数
        int lastDay = 0;
        //2月的平年瑞年天数
        if (month == 2) {
            lastDay = calendar.getLeastMaximum(Calendar.DAY_OF_MONTH);
        } else {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }


    /**
     * 获取当前月的每天
     *
     * @param length 当前月的最后一天
     * @return Map
     */
    protected Map<String, ArrayList<String>> getMonthArr(int length) {
        Map<String, ArrayList<String>> result = CollectionUtil.newHashMap();
        ArrayList<String> arrayList = CollectionUtil.newArrayList();
        ArrayList<String> dateList = CollectionUtil.newArrayList();
        String thisYear = DateUtil.thisYear() + "";
        String thisMonth = "";
        int month = DateUtil.thisMonth() + 1;
        if (month <= 9) {
            thisMonth += "0" + month;
        } else {
            thisMonth += month;
        }
        for (int i = 1; i <= length; i++) {
            //某天
            String day = i < 10 ? "0" + i : "" + i;
            //yyyy-MM-dd
            String dateDay = thisYear + "-" + thisMonth + "-" + day;
            //添加某天
            arrayList.add(day);
            //添加年月日
            dateList.add(dateDay);
        }
        result.put("dayList", arrayList);
        result.put("dateList", dateList);
        return result;
    }


    /**
     * 获取每天新增用户数据统计
     *
     * @param dayOfMonth 当前月的每一天
     * @return Map
     */
    private Map<String, List<Integer>> getDayOfMonthAccountAddCount(List<String> dayOfMonth) {
        Map<String, List<Integer>> result = CollectionUtil.newHashMap();
        //封装结果
        List<Integer> CSICountList = CollectionUtil.newArrayList();
        List<Integer> NormalCountList = CollectionUtil.newArrayList();
        dayOfMonth.forEach(item -> {
            //今日新增所有用户
            Integer todayAddCount = accountMapper.selectCount(new QueryWrapper<Account>().like("create_time", item));
            //今日新增CSI用户数
            Integer todayAddCSICount = bindingAccountCSIMapper.selectCount(new QueryWrapper<BindingAccountCSI>().eq("status", 2).like("create_time", item));
            //今日新增普通用户数
            Integer todayAddNormalCount = todayAddCount - todayAddCSICount;
            CSICountList.add(todayAddCSICount);
            NormalCountList.add(todayAddNormalCount);
        });
        result.put("CSICountList", CSICountList);
        result.put("NormalCountList", NormalCountList);
        return result;
    }


    /**
     * 获取用户概况柱状图
     *
     * @return map
     */
    @Override
    public Map<String, Object> getAccountSurveyBarInfo() {
        HashMap<String, Object> hashMap = CollectionUtil.newHashMap();
        //当前月的最后一天
        int lastDayOfMonth = Convert.toInt(getLastDayOfMonth().substring(8, 10));
        //当前月的每一天
        Map<String, ArrayList<String>> resultMap = getMonthArr(lastDayOfMonth);
        List<String> monthArr = resultMap.get("dayList");
        //当前月的每一天 2021-09-01  ~~  2021-09-30
        Map<String, List<Integer>> dateList = getDayOfMonthAccountAddCount(resultMap.get("dateList"));
        //返回结果
        hashMap.put("monthArr", monthArr);
        hashMap.put("CSICountList", dateList.get("CSICountList"));
        hashMap.put("NormalCountList", dateList.get("NormalCountList"));
        return hashMap;
    }


}
