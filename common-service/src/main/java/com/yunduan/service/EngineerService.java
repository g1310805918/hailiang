package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.Engineer;
import com.yunduan.vo.OtherEngineerListVo;

import java.util.List;

public interface EngineerService extends IService<Engineer> {


    /**
     * 获取用户信息
     * @param email 邮箱
     * @return engineer
     */
    Engineer findByEmail(String email);


    /**
     * 工程师登录更新token
     * @param engineer 工程师账号
     * @return int
     */
    Engineer engineerLoginUpdateToken(Engineer engineer);


    /**
     * 其他工程师【排除当前用户】
     * @param engineerId 工程师id
     * @return list
     */
    List<OtherEngineerListVo> queryOtherEngineers(String engineerId);
}
