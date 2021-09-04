package com.yunduan.config.jpa;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.AdminAccount;
import com.yunduan.service.AdminAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * 审计记录创建或修改用户
 *
 * @author Victor
 */
@Configuration
@Slf4j
public class UserAuditor implements AuditorAware<String> {


    @Autowired
    private AdminAccountService adminAccountService;

    @Override
    public Optional<String> getCurrentAuditor() {

        UserDetails user;
        try {
            user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userid = adminAccountService.getOne(new QueryWrapper<AdminAccount>().eq("username", user.getUsername())).getId();
            return StrUtil.isBlank(userid) ? Optional.empty() : Optional.ofNullable(userid);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
