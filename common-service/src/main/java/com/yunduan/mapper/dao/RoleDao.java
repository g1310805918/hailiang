package com.yunduan.mapper.dao;

import com.yunduan.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RoleDao extends JpaRepository<Role,String>, JpaSpecificationExecutor<Role> {

    /**
     * 分页获取角色
     * @param pageable 分页
     * @return page
     */
    Page<Role> findAll(Pageable pageable);


    /**
     * 获取默认角色
     *
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(boolean defaultRole);

}
