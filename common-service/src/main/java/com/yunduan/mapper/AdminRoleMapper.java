package com.yunduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunduan.entity.AdminAccount;
import com.yunduan.entity.AdminRole;
import com.yunduan.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {


    /**
     * 通过用户id获取
     *
     * @param userId 用户id = 管理员id
     * @return
     */
    List<Role> findByUserId(@Param("userId") String userId);

    /**
     * 通过角色id 获取用户
     *
     * @param roleId 角色id
     * @return
     */
    List<AdminAccount> findUserByRoleId(@Param("roleId") String roleId);
}
