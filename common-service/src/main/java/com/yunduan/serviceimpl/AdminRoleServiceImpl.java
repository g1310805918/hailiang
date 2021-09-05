package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.AdminRole;
import com.yunduan.entity.Role;
import com.yunduan.mapper.AdminRoleMapper;
import com.yunduan.mapper.dao.RoleDao;
import com.yunduan.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {

    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private RoleDao roleDao;


    /**
     * 通过用户id获取
     *
     * @param userId
     * @return
     */
    @Override
    public List<Role> findByUserId(String userId) {
        return adminRoleMapper.findByUserId(userId);
    }


    /**
     * 获取默认角色
     *
     * @param defaultRole
     * @return
     */
    @Override
    public List<Role> findByDefaultRole(boolean defaultRole) {
        return roleDao.findByDefaultRole(defaultRole);
    }


}
