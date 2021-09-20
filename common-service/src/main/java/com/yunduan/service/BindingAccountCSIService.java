package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.BindingAccountCSI;
import com.yunduan.vo.AccountBindingCSI;
import com.yunduan.vo.BindingOtherCSIAccountVo;
import com.yunduan.vo.CustomerServiceNoVo;

import java.util.List;
import java.util.Map;

public interface BindingAccountCSIService extends IService<BindingAccountCSI> {


    /**
     * 绑定CSI编号
     *
     * @param accountId 用户id
     * @param csiNumber csi编号
     * @return int
     */
    int addBindingCSI(String accountId, String csiNumber);


    /**
     * 绑定CSI详情
     *
     * @param bindingId 绑定id
     * @return AccountBindingCSI
     */
    AccountBindingCSI queryCSIBindingPersonInfoList(String bindingId);


    /**
     * CAU操作绑定的用户
     *
     * @param bindingId 绑定id
     * @param type      1同意、2拒绝、3删除
     * @return int
     */
    int operationBindingAccountRecord(String bindingId, String type);


    /**
     * 绑定的CSI下的其他用户列表
     *
     * @param bindingId 绑定记录表id
     * @return list
     */
    List<BindingOtherCSIAccountVo> queryOtherCSIAccountList(String bindingId);


    /**
     * CAU分配给其他用户
     *
     * @param bindingId 绑定记录id
     * @param accountId 分配给的用户id
     * @return int
     */
    int distributionCAUToOtherAccount(String bindingId, String accountId);


    /**
     * 查询客户服务号列表
     *
     * @param accountId 用户id
     * @return list
     */
    List<CustomerServiceNoVo> queryCustomerServiceList(String accountId);


    /**
     * 用户绑定页面初始化
     * @param accountId 用户id
     * @param pageNo 页号
     * @param pageSize 页面大小
     * @return map
     */
    Map<String, Object> initUserAccountCSIRecord(String accountId, Integer pageNo, Integer pageSize);
}
