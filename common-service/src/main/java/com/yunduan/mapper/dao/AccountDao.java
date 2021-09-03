package com.yunduan.mapper.dao;

import com.yunduan.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDao extends JpaRepository<Account,Long> {

    /**
     * 根据手机号查询用户
     * @param mobile 手机号
     * @param email 邮箱
     * @return account
     */
    Account findByMobileAndEmail(String mobile,String email);

}
