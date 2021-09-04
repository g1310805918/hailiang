package com.yunduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunduan.entity.AdminAccount;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AdminAccountMapper extends BaseMapper<AdminAccount> {

    /**
     * 通过用户名获取用户ID
     *
     * @param username 用户名
     * @return
     */
    String findIdByUsername(String username);
}
