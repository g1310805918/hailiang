package com.yunduan.serviceimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Account;
import com.yunduan.entity.BindingAccountCSI;
import com.yunduan.entity.CompanyCSI;
import com.yunduan.mapper.AccountMapper;
import com.yunduan.mapper.BindingAccountCSIMapper;
import com.yunduan.mapper.CompanyCSIMapper;
import com.yunduan.request.back.CompanyCSIInit;
import com.yunduan.service.CompanyCSIService;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class CompanyCSIServiceImpl extends ServiceImpl<CompanyCSIMapper, CompanyCSI> implements CompanyCSIService {

    @Autowired
    private CompanyCSIMapper companyCSIMapper;
    @Autowired
    private BindingAccountCSIMapper bindingAccountCSIMapper;
    @Autowired
    private AccountMapper accountMapper;


    /**
     * 初始化客户服务号列表
     * @param companyCSIInit 客户服务号列表
     * @return map
     */
    @Override
    public Map<String, Object> initPageData(CompanyCSIInit companyCSIInit) {
        Map<String, Object> map = new HashMap<>();
        //条件构造器
        QueryWrapper<CompanyCSI> queryWrapper = new QueryWrapper<CompanyCSI>()
                .like(StrUtil.isNotEmpty(companyCSIInit.getCompanyName()), "company_name", companyCSIInit.getCompanyName())
                .like(StrUtil.isNotEmpty(companyCSIInit.getMobile()), "cau_mobile", companyCSIInit.getMobile())
                .like(StrUtil.isNotEmpty(companyCSIInit.getEmail()), "cau_email", companyCSIInit.getEmail())
                .orderByDesc("create_time");
        //筛选出的结果集合
        List<CompanyCSI> records = companyCSIMapper.selectPage(new Page<>(companyCSIInit.getPageNo(), companyCSIInit.getPageSize()), queryWrapper).getRecords();

        map.put("voList",records);
        map.put("total",companyCSIMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 编辑客户服务号信息
     * @param companyCSI 客户服务号
     * @return int
     */
    @Override
    public int changeCompanyCSIInfo(CompanyCSI companyCSI) {
        CompanyCSI csiInfo = companyCSIMapper.selectById(companyCSI.getId());
        if (csiInfo != null) {
            csiInfo.setCsiNumber(companyCSI.getCsiNumber()).setCauMobile(companyCSI.getCauMobile()).setCauEmail(companyCSI.getCauEmail()).setCompanyName(companyCSI.getCompanyName());
            return companyCSIMapper.updateById(csiInfo);
        }
        return 0;
    }


    /**
     * 添加客户服务号
     * @param companyCSI 客户服务号对象
     * @return int
     */
    @Override
    public int createCompanyCSI(CompanyCSI companyCSI) {
        CompanyCSI isExist = companyCSIMapper.selectOne(new QueryWrapper<CompanyCSI>().eq("csi_number", companyCSI.getCsiNumber()));
        if (isExist != null) {
            return StatusCodeUtil.HAS_EXIST;
        }
        companyCSI.setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setId(SnowFlakeUtil.getPrimaryKeyId()).setUpdateTime(DateUtil.now());
        return companyCSIMapper.insert(companyCSI);
    }


    /**
     * 批量添加客户服务号
     * @param companyCSIList 客户服务号列表
     * @return list
     */
    @Override
    public List<CompanyCSI> createBatch(List<CompanyCSI> companyCSIList) {
        //最终返回结果
        List<CompanyCSI> resultList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(companyCSIList)) {
            for (CompanyCSI companyCSI : companyCSIList) {
                CompanyCSI isExist = companyCSIMapper.selectOne(new QueryWrapper<CompanyCSI>().eq("csi_number", companyCSI.getCsiNumber()));
                if (isExist != null) {
                    continue;
                }
                companyCSI.setId(SnowFlakeUtil.getPrimaryKeyId()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setUpdateTime(DateUtil.now());
                //保存不存在的CSI信息
                resultList.add(companyCSI);
            }
        }
        return resultList;
    }


    /**
     * 获取公司CSI编号下绑定的用户列表
     * @param companyId 公司id
     * @return list
     */
    @Override
    public List<Account> queryCompanyDropCSIBindingRecord(String companyId) {
        CompanyCSI companyCSI = companyCSIMapper.selectById(companyId);
        if (companyCSI != null) {
            List<Long> accountId = CollectionUtil.newArrayList();
            //CSI下的绑定记录
            List<BindingAccountCSI> bindingAccountCSIS = bindingAccountCSIMapper.selectList(new QueryWrapper<BindingAccountCSI>().eq("csi_id", companyCSI.getId()).orderByDesc("create_time"));
            if (CollectionUtil.isNotEmpty(bindingAccountCSIS)) {
                for (BindingAccountCSI bindingAccountCSI : bindingAccountCSIS) {
                    if (bindingAccountCSI == null) {
                        continue;
                    }
                    accountId.add(bindingAccountCSI.getAccountId());
                }
            }
            //封装结果
            if (CollectionUtil.isNotEmpty(accountId)) {
                List<Account> accountList = accountMapper.selectList(new QueryWrapper<Account>().in("id", accountId).orderByDesc("create_time"));
                if (CollectionUtil.isNotEmpty(accountList)) {
                    for (Account account : accountList) {
                        account.setCompanyName(companyCSI.getCompanyName()).setCsiNumber(companyCSI.getCsiNumber()).setCreateDateTime(DateUtil.formatDateTime(account.getCreateTime()));
                    }
                }
                return accountList;
            }
        }
        return null;
    }


}
