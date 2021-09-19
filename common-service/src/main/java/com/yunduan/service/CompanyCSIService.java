package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.Account;
import com.yunduan.entity.CompanyCSI;
import com.yunduan.request.back.CompanyCSIInit;

import java.util.List;
import java.util.Map;

public interface CompanyCSIService extends IService<CompanyCSI> {


    /**
     * 初始化客户服务号列表
     * @param companyCSIInit 客户服务号列表
     * @return map
     */
    Map<String,Object> initPageData(CompanyCSIInit companyCSIInit);


    /**
     * 编辑客户服务号信息
     * @param companyCSI 客户服务号
     * @return int
     */
    int changeCompanyCSIInfo(CompanyCSI companyCSI);


    /**
     * 添加客户服务号
     * @param companyCSI 客户服务号对象
     * @return int
     */
    int createCompanyCSI(CompanyCSI companyCSI);


    /**
     * 批量添加客户服务号
     * @param companyCSIList 客户服务号列表
     * @return list
     */
    List<CompanyCSI> createBatch(List<CompanyCSI> companyCSIList);


    /**
     * 获取公司CSI编号下绑定的用户列表
     * @param companyId 公司id
     * @return list
     */
    List<Account> queryCompanyDropCSIBindingRecord(String companyId);
}
