package com.yunduan.serviceimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Account;
import com.yunduan.entity.AccountMsg;
import com.yunduan.entity.BindingAccountCSI;
import com.yunduan.entity.CompanyCSI;
import com.yunduan.mapper.AccountMapper;
import com.yunduan.mapper.AccountMsgMapper;
import com.yunduan.mapper.BindingAccountCSIMapper;
import com.yunduan.mapper.CompanyCSIMapper;
import com.yunduan.service.BindingAccountCSIService;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.AccountBindingCSI;
import com.yunduan.vo.AccountBindingCSIPersonList;
import com.yunduan.vo.BindingOtherCSIAccountVo;
import com.yunduan.vo.CustomerServiceNoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.util.*;


@Service
@Transactional
public class BindingAccountCSIServiceImpl extends ServiceImpl<BindingAccountCSIMapper, BindingAccountCSI> implements BindingAccountCSIService {

    private static final transient Logger log = LoggerFactory.getLogger(BindingAccountCSIServiceImpl.class);

    @Autowired
    private BindingAccountCSIMapper bindingAccountCSIMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CompanyCSIMapper companyCSIMapper;
    @Autowired
    private AccountMsgMapper accountMsgMapper;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    /**
     * 绑定CSI编号
     * @param accountId 用户id
     * @param csiNumber csi编号
     * @return int
     */
    @Override
    public int addBindingCSI(String accountId, String csiNumber) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", accountId));
        if (account != null) {
            //查询csi编号是否存在
            CompanyCSI companyCSI = companyCSIMapper.selectOne(new QueryWrapper<CompanyCSI>().eq("csi_number", csiNumber));
            if (companyCSI == null) {
                return StatusCodeUtil.NOT_FOUND_FLAG;
            }
            //如果当前用户 手机号、邮箱 与 客户公司记录信息一致。那么当前用户直接成为CAU身份
            if (Objects.equals(account.getMobile(),companyCSI.getCauMobile()) && Objects.equals(account.getEmail(),companyCSI.getCauEmail())) {
                BindingAccountCSI bind = new BindingAccountCSI();
                bind.setId(SnowFlakeUtil.getPrimaryKeyId()).setAccountId(account.getId()).setCsiId(companyCSI.getId()).setIdentity(2).setStatus(2).setCreateTime(DateUtil.now());
                return bindingAccountCSIMapper.insert(bind);
            }
            //如果不是CAU用户绑定，判断当前CSI编号是否有CAU身份绑定了
            BindingAccountCSI CAUBindingRecord = bindingAccountCSIMapper.selectOne(new QueryWrapper<BindingAccountCSI>().eq("csi_id", companyCSI.getId()).eq("identity", 2).eq("status",2));
            if (CAUBindingRecord == null) {
                return StatusCodeUtil.COMPANY_CSI_CAU_NO_BINDING;
            }
            //添加申请绑定记录(普通员工)
            BindingAccountCSI normalBinding = new BindingAccountCSI();
            normalBinding.setId(SnowFlakeUtil.getPrimaryKeyId()).setAccountId(account.getId()).setCsiId(companyCSI.getId()).setIdentity(1).setStatus(1).setCreateTime(DateUtil.now());
            int row = bindingAccountCSIMapper.insert(normalBinding);
            if (row > 0) {
                //异步发送验证信息到CAU用户信息
                threadPoolTaskExecutor.execute(() -> {
                    account.setAccountId(account.getId().toString());  //将用户id转换为字符串
                    //用户信息JSON字符串
                    String accountJson = JSONObject.toJSONString(account);
                    //向CAU管理员发送一条待审核消息
                    AccountMsg msg = new AccountMsg().setAccountId(SnowFlakeUtil.getPrimaryKeyId()).setMsgTitle(StatusCodeUtil.SYS_MATH_MSG).setMsgContent(accountJson).setMsgType(2).setAccountId(CAUBindingRecord.getAccountId()).setIsRead(0).setDelFlag(0).setCreateTime(DateUtil.now());
                    accountMsgMapper.insert(msg);
                });
            }
            return row;
        }
        return 0;
    }


    /**
     * 绑定CSI详情
     * @param bindingId 绑定id
     * @return AccountBindingCSI
     */
    @Override
    public AccountBindingCSI queryCSIBindingPersonInfoList(String bindingId) {
        //绑定记录
        BindingAccountCSI one = bindingAccountCSIMapper.selectOne(new QueryWrapper<BindingAccountCSI>().eq("id", bindingId));
        if (one != null) {
            //绑定的CSI公司基本信息
            CompanyCSI csiInfo = companyCSIMapper.selectOne(new QueryWrapper<CompanyCSI>().eq("id", one.getCsiId()));
            if(csiInfo == null) {
                return null;
            }
            AccountBindingCSI info = new AccountBindingCSI();
            //绑定id、CSI编号、公司名、产品名、身份、状态
            info.setBindingId(one.getId().toString()).setCsiNumber(csiInfo.getCsiNumber()).setCompanyName(csiInfo.getCompanyName()).setProductName(csiInfo.getProductName()).setIdentity(one.getIdentity()).setStatus(one.getStatus());
            //如果是CAU身份的话、查询公司下的绑定人员记录
            if (info.getIdentity() == 2) {
                //CSI下绑定的人员列表【排除CAU自己】
                List<AccountBindingCSIPersonList> personLists = bindingAccountCSIMapper.selectCSIBindingPersonList(csiInfo.getId().toString(),one.getId().toString());
                info.setPersonLists(personLists);
            }else {
                info.setPersonLists(new ArrayList<>());
            }
            return info;
        }
        return null;
    }


    /**
     * CAU操作绑定的用户
     * @param bindingId 绑定id
     * @param type 1同意、2拒绝、3删除
     * @return int
     */
    @Override
    public int operationBindingAccountRecord(String bindingId, String type) {
        //绑定记录
        BindingAccountCSI record = bindingAccountCSIMapper.selectOne(new QueryWrapper<BindingAccountCSI>().eq("id", bindingId));
        if (record == null) {
            log.error("CAU操作绑定用户 record 为空");
            return 0;
        }
        if (Objects.equals("1",type)) {
            //同意
            record.setStatus(2);
        }
        if (Objects.equals("2",type)) {
            //拒绝
            record.setStatus(3);
        }
        if (Objects.equals("3",type)){
            //删除记录
            return bindingAccountCSIMapper.deleteById(record.getId());
        }
        return bindingAccountCSIMapper.update(record,new QueryWrapper<BindingAccountCSI>().eq("id",record.getId()));
    }


    /**
     * 绑定的CSI下的其他用户列表
     * @param bindingId 绑定记录表id
     * @return list
     */
    @Override
    public List<BindingOtherCSIAccountVo> queryOtherCSIAccountList(String bindingId) {
        List<BindingOtherCSIAccountVo> voList = new ArrayList<>();
        //当前CAU绑定的CSI记录id
        BindingAccountCSI record = bindingAccountCSIMapper.selectOne(new QueryWrapper<BindingAccountCSI>().eq("id", bindingId));
        if (record != null) {
            //CSI下绑定的普通用户列表
            List<BindingAccountCSI> personList = bindingAccountCSIMapper.selectList(new QueryWrapper<BindingAccountCSI>().ne("id", record.getId()).eq("csi_id", record.getCsiId()).eq("identity", 1));
            if (personList.size() > 0 && personList != null) {
                BindingOtherCSIAccountVo vo = null;
                for (BindingAccountCSI bindingAccountCSI : personList) {
                    Account account = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", bindingAccountCSI.getAccountId()));
                    if (account == null) {
                        continue;
                    }
                    vo = new BindingOtherCSIAccountVo();
                    vo.setAccountId(account.getId().toString()).setUsername(account.getUsername());
                    voList.add(vo);
                }
            }
        }
        return voList;
    }


    /**
     * CAU分配给其他用户
     * @param bindingId 绑定记录id
     * @param accountId 分配给的用户id
     * @return int
     */
    @Override
    public int distributionCAUToOtherAccount(String bindingId, String accountId) {
        //原CAU身份绑定记录
        BindingAccountCSI accountCSI = bindingAccountCSIMapper.selectOne(new QueryWrapper<BindingAccountCSI>().eq("id", bindingId).eq("identity", 2));
        if (accountCSI != null) {
            //需要被分配CAU身份的普通用户
            BindingAccountCSI normalAccount = bindingAccountCSIMapper.selectOne(new QueryWrapper<BindingAccountCSI>().eq("account_id", accountId).eq("csi_id", accountCSI.getCsiId()).eq("identity", 1));
            if (normalAccount != null) {
                //修改之前绑定CAU用户为普通用户
                accountCSI.setIdentity(1);
                //修改被分配CAU身份用户为CAU管理员身份
                normalAccount.setIdentity(2);

                //更新
                bindingAccountCSIMapper.update(accountCSI, new QueryWrapper<BindingAccountCSI>().eq("id", accountCSI.getId()));
                bindingAccountCSIMapper.update(normalAccount, new QueryWrapper<BindingAccountCSI>().eq("id", normalAccount.getId()));

                //返回
                return 1;
            }
        }
        return 0;
    }


    /**
     * 查询客户服务号列表
     * @param accountId 用户id
     * @return list
     */
    @Override
    public List<CustomerServiceNoVo> queryCustomerServiceList(String accountId) {
        List<CustomerServiceNoVo> voList = new ArrayList<>();
        Set<Long> CSIIdSet = new TreeSet<>();
        //绑定列表
        List<BindingAccountCSI> bindingList = bindingAccountCSIMapper.selectList(new QueryWrapper<BindingAccountCSI>().eq("account_id", accountId));
        if (bindingList.size() > 0 && bindingList != null) {
            for (BindingAccountCSI bindingAccountCSI : bindingList) {
                //得到绑定的CSI列表id
                CSIIdSet.add(bindingAccountCSI.getCsiId());
            }
        }
        //条件构造器
        QueryWrapper<CompanyCSI> queryWrapper = new QueryWrapper<CompanyCSI>()
                .in("id", CSIIdSet)
                .orderByDesc("create_time");

        if (CSIIdSet.size() > 0 && CSIIdSet != null) {
            //用户绑定的CSI公司信息
            List<CompanyCSI> companyCSIList = companyCSIMapper.selectList(queryWrapper);
            if (companyCSIList.size() > 0 && companyCSIList != null) {
                CustomerServiceNoVo vo = null;
                for (CompanyCSI companyCSI : companyCSIList) {
                    vo = new CustomerServiceNoVo().setCsiNumber(companyCSI.getCsiNumber()).setCompanyName(companyCSI.getCompanyName());
                    voList.add(vo);
                }
            }
        }
        return voList;
    }


    /**
     * 用户绑定页面初始化
     * @param accountId 用户id
     * @param pageNo 页号
     * @param pageSize 页面大小
     * @return map
     */
    @Override
    public Map<String, Object> initUserAccountCSIRecord(String accountId, Integer pageNo, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();

        Set<Long> CSIIdSet = new TreeSet<>();
        //绑定列表
        List<BindingAccountCSI> bindingList = bindingAccountCSIMapper.selectList(new QueryWrapper<BindingAccountCSI>().eq("account_id", accountId));
        if (bindingList.size() > 0 && bindingList != null) {
            for (BindingAccountCSI bindingAccountCSI : bindingList) {
                //得到绑定的CSI列表id
                CSIIdSet.add(bindingAccountCSI.getCsiId());
            }
        }
        //条件构造器
        QueryWrapper<CompanyCSI> queryWrapper = new QueryWrapper<CompanyCSI>().in(CSIIdSet != null && CSIIdSet.size() > 0,"id", CSIIdSet).orderByDesc("create_time");
        //结果集合
        List<CustomerServiceNoVo> voList = new ArrayList<>();
        if (CSIIdSet.size() > 0 && CSIIdSet != null) {
            //绑定记录
            List<CompanyCSI> companyCSIS = companyCSIMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper).getRecords();
            if (CollectionUtil.isNotEmpty(companyCSIS)) {
                CustomerServiceNoVo vo = null;
                for (CompanyCSI companyCSI : companyCSIS) {
                    vo = new CustomerServiceNoVo().setCsiNumber(companyCSI.getCsiNumber()).setCompanyName(companyCSI.getCompanyName());
                    voList.add(vo);
                }
            }
        }
        map.put("voList",voList);
        map.put("total",companyCSIMapper.selectCount(queryWrapper));
        return map;
    }

}
