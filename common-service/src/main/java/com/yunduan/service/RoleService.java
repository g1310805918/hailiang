package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService extends IService<Role> {


    /**
     * 分页获取角色
     * @param pageable 分页
     * @return page
     */
    Page<Role> findAll(Pageable pageable);


    /**
     * 添加角色
     * @param role 角色
     * @return role
     */
    Role createRole(Role role);
}
