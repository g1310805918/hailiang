package com.yunduan.mapper.dao;

import com.yunduan.entity.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AdminAccountDao extends JpaRepository<AdminAccount,String> , JpaSpecificationExecutor<AdminAccount> {


    /**
     * 通过手机号查找用户
     * @param mobile 手机号
     * @return AdminAccount
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
