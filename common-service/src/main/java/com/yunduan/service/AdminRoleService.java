package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.AdminRole;
import com.yunduan.entity.Role;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface AdminRoleService extends IService<AdminRole> {



    /**
     * 通过用户id获取
     *
     * @param userId
     * @return
     */
    List<Role> findByUserId(String userId);


    /**
     * 获取默认角色
     *
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(boolean defaultRole);
}
