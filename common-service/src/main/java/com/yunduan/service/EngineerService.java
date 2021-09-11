package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.Engineer;
import com.yunduan.request.back.EngineerInit;
import com.yunduan.utils.ContextUtil;
import com.yunduan.vo.OtherEngineerListVo;

import java.util.List;
import java.util.Map;

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


    /**
     * 查询工程师基本信息
     * @param engineerId 工程师id
     * @return engineer
     */
    Engineer engineerBaseInfo(String engineerId);


    /**
     * 修改工程师头像
     * @param engineerId 工程师id
     * @param headPic 头像地址
     * @return int
     */
    int changeHeadPic(Long engineerId, String headPic);


    /**
     * 工程师修改用户名
     * @param engineerId 工程师id
     * @param username 用户名
     * @return int
     */
    int changeUsername(String engineerId, String username);


    /**
     * 工程师换绑手机号
     * @param engineerId 工程师id
     * @param mobile 手机号
     * @return int
     */
    int changeMobile(String engineerId, String mobile);


    /**
     * 初始化工程师列表
     * @param engineerInit 初始化对象
     * @return map
     */
    Map<String,Object> engineerListInit(EngineerInit engineerInit);
}
