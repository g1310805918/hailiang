package com.yunduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunduan.entity.BindingAccountCSI;
import com.yunduan.vo.AccountBindingCSI;
import com.yunduan.vo.AccountBindingCSIPersonList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BindingAccountCSIMapper extends BaseMapper<BindingAccountCSI> {


    /**
     * 用户绑定的CSI记录列表
     * @param accountId 用户id
     * @return java.util.List
     */
    List<AccountBindingCSI> selectAccountBindingRecord(@Param("accountId") String accountId);


    /**
     * 查询CSI下绑定的人员列表
     * @param csiId csiId
     * @param bindingId CAU的绑定记录id【需排除自己】
     * @return list
     */
    List<AccountBindingCSIPersonList> selectCSIBindingPersonList(@Param("csiId") String csiId, @Param("bindingId") String bindingId);

}
