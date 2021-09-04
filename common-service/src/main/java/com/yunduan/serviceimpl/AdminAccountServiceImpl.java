package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.AdminAccount;
import com.yunduan.entity.Permission;
import com.yunduan.entity.Role;
import com.yunduan.mapper.AdminAccountMapper;
import com.yunduan.mapper.AdminRoleMapper;
import com.yunduan.mapper.PermissionMapper;
import com.yunduan.service.AdminAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AdminAccountServiceImpl extends ServiceImpl<AdminAccountMapper, AdminAccount> implements AdminAccountService {

    @Autowired
    private AdminAccountMapper adminAccountMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private PermissionMapper permissionMapper;


    /**
     * 通过用户名获取用户
     *
     * @param username 用户名
     * @return
     */
    @Override
    public AdminAccount findByUsername(String username) {
        AdminAccount user = adminAccountMapper.selectOne(new QueryWrapper<AdminAccount>().eq("username",username));
        if (user == null) {
            return null;
        }
        // 关联角色
        List<Role> roleList = adminRoleMapper.findByUserId(user.getId());
        user.setRoles(roleList);
        // 关联权限菜单
        List<Permission> permissionList = permissionMapper.findByUserId(user.getId());
        user.setPermissions(permissionList);
        return user;
    }


}
