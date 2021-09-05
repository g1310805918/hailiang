package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Role;
import com.yunduan.mapper.RoleMapper;
import com.yunduan.mapper.dao.RoleDao;
import com.yunduan.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleDao roleDao;


    /**
     * 分页获取角色
     * @param pageable 分页
     * @return page
     */
    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleDao.findAll(pageable);
    }


    /**
     * 添加角色
     * @param role 角色
     * @return role
     */
    @Override
    public Role createRole(Role role) {
        return roleDao.save(role);
    }
    

}
