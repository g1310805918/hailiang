package com.yunduan;

import cn.hutool.core.date.DateUtil;
import com.yunduan.entity.AdminAccount;
import com.yunduan.service.AdminAccountService;
import com.yunduan.utils.SnowFlakeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackApplicationTests {

    @Autowired
    private AdminAccountService adminAccountService;


    @Test
    void contextLoads() {

        AdminAccount account = new AdminAccount();

        account.setId(SnowFlakeUtil.getPrimaryKeyId().toString()).setUsername("ccc").setNickName("ccc").setEmail("111111").setMobile("111111").setStatus(0).setPassword("$2a$10$s8ilz9raFUVuWTDCLx4jtepKLl9RCrZKpv/KSj0U2PDXwIkNPGUm.").setCreateTime(DateUtil.now()).setDelFlag(0);
        adminAccountService.createAccount(account);
    }

}
