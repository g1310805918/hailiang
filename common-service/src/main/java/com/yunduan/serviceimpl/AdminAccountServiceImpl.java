package com.yunduan.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.AdminAccount;
import com.yunduan.entity.Permission;
import com.yunduan.entity.Role;
import com.yunduan.mapper.AdminAccountMapper;
import com.yunduan.mapper.AdminRoleMapper;
import com.yunduan.mapper.PermissionMapper;
import com.yunduan.mapper.dao.AdminAccountDao;
import com.yunduan.service.AdminAccountService;
import com.yunduan.vo.back.SearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private AdminAccountDao adminAccountDao;


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


    /**
     * 多条件分页获取用户列表
     * @param user 管理员
     * @param searchVo 搜索条件
     * @param initPage 分页
     * @return page
     */
    @Override
    public Page<AdminAccount> findByCondition(AdminAccount user, SearchVo searchVo, Pageable initPage) {
        return adminAccountDao.findAll(new Specification<AdminAccount>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<AdminAccount> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> usernameField = root.get("username");
                Path<String> mobileField = root.get("mobile");
                Path<String> emailField = root.get("email");
                Path<String> sexField = root.get("sex");
                Path<Integer> typeField = root.get("type");
                Path<Integer> statusField = root.get("status");
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<Predicate>();

                //模糊搜素
                if (StrUtil.isNotBlank(user.getUsername())) {
                    list.add(cb.like(usernameField, '%' + user.getUsername() + '%'));
                }
                if (StrUtil.isNotBlank(user.getMobile())) {
                    list.add(cb.like(mobileField, '%' + user.getMobile() + '%'));
                }
                if (StrUtil.isNotBlank(user.getEmail())) {
                    list.add(cb.like(emailField, '%' + user.getEmail() + '%'));
                }

                //性别
                if (StrUtil.isNotBlank(user.getSex())) {
                    list.add(cb.equal(sexField, user.getSex()));
                }
                //类型
                if (user.getType() != null) {
                    list.add(cb.equal(typeField, user.getType()));
                }
                //状态
                if (user.getStatus() != null) {
                    list.add(cb.equal(statusField, user.getStatus()));
                }
                //创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }
                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, initPage);
    }


    /**
     * 添加用户
     * @param u 用户对象
     * @return AdminAccount
     */
    @Override
    public AdminAccount createAccount(AdminAccount u) {
        return adminAccountDao.save(u);
    }


    /**
     * 通过手机号获取
     * @param mobile 手机号
     * @return adminAccount
     */
    @Override
    public AdminAccount findByMobile(String mobile) {
        return adminAccountDao.findByMobile(mobile);
    }

    /**
     * 通过邮箱号获取
     * @param email 邮箱
     * @return AdminAccount
     */
    @Override
    public AdminAccount findByEmail(String email) {
        return adminAccountDao.findByEmail(email);
    }


    /**
     * 通过用户名模糊搜索
     *
     * @param username
     * @param status
     * @return
     */
    @Override
    public List<AdminAccount> findByUsernameLikeAndStatus(String username, Integer status) {
        return adminAccountDao.findByUsernameLikeAndStatus(username, status);
    }



}
