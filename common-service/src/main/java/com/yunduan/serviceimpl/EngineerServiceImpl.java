package com.yunduan.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Engineer;
import com.yunduan.mapper.EngineerMapper;
import com.yunduan.mapper.dao.EngineerDao;
import com.yunduan.service.EngineerService;
import com.yunduan.utils.RedisUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.OtherEngineerListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class EngineerServiceImpl extends ServiceImpl<EngineerMapper, Engineer> implements EngineerService {

    @Autowired
    private EngineerMapper engineerMapper;
    @Autowired
    private EngineerDao engineerDao;
    @Autowired
    private RedisUtil redisUtil;



    /**
     * 获取用户信息
     * @param email 邮箱
     * @return engineer
     */
    @Override
    public Engineer findByEmail(String email) {
        return engineerDao.findByEmail(email);
    }


    /**
     * 工程师登录更新token
     * @param engineer 工程师账号
     * @return int
     */
    @Override
    public Engineer engineerLoginUpdateToken(Engineer engineer) {
        String token = IdUtil.simpleUUID();
        //设置工程师token
        redisUtil.setStringKeyValue(StatusCodeUtil.ACCOUNT_TOKEN + token,engineer.getId().toString(),1, TimeUnit.DAYS);
        engineer.setToken(token).setLastLoginTime(DateUtil.now());
        int row = engineerMapper.updateById(engineer);
        return row > 0 ? engineer : null;
    }


    /**
     * 其他工程师【排除当前用户】
     * @param engineerId 工程师id
     * @return list
     */
    @Override
    public List<OtherEngineerListVo> queryOtherEngineers(String engineerId) {
        List<OtherEngineerListVo> voList = new ArrayList<>();
        //其他工程师列表
        List<Engineer> engineerList = engineerMapper.selectList(new QueryWrapper<Engineer>().ne("id", engineerId));
        if (!CollectionUtils.isEmpty(engineerList)) {
            engineerList.forEach(engineer -> {
                OtherEngineerListVo vo = new OtherEngineerListVo().setEngineerId(engineer.getId().toString()).setUsername(engineer.getUsername());
                voList.add(vo);
            });
        }
        return voList;
    }


}
