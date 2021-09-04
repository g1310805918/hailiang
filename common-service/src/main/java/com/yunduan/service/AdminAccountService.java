package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.AdminAccount;

public interface AdminAccountService extends IService<AdminAccount> {




    /**
     * 通过用户名获取用户
     *
     * @param username 用户名
     * @return
     */
    AdminAccount findByUsername(String username);
}
