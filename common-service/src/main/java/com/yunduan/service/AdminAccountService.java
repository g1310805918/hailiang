package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.AdminAccount;
import com.yunduan.vo.back.SearchVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminAccountService extends IService<AdminAccount> {




    /**
     * 通过用户名获取用户
     *
     * @param username 用户名
     * @return
     */
    AdminAccount findByUsername(String username);


    /**
     * 多条件分页获取用户列表
     * @param user 管理员
     * @param searchVo 搜索条件
     * @param initPage 分页
     * @return page
     */
    Page<AdminAccount> findByCondition(AdminAccount user, SearchVo searchVo, Pageable initPage);


    /**
     * 添加用户
     * @param u 用户对象
     * @return AdminAccount
     */
    AdminAccount createAccount(AdminAccount u);

    /**
     * 通过手机号获取
     * @param mobile 手机号
     * @return adminAccount
     */
    AdminAccount findByMobile(String mobile);


    /**
     * 通过邮箱号获取
     * @param email 邮箱
     * @return AdminAccount
     */
    AdminAccount findByEmail(String email);


    /**
     * 通过用户名模糊搜索
     *
     * @param username
     * @param status
     * @return
     */
    List<AdminAccount> findByUsernameLikeAndStatus(String username, Integer status);
}
