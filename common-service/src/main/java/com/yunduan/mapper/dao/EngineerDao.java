package com.yunduan.mapper.dao;

import com.yunduan.entity.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngineerDao extends JpaRepository<Engineer,Long> {


    /**
     * 根据邮箱获取工程师账号
     * @param email 邮箱
     * @return engineer
     */
    Engineer findByEmail(String email);

}
