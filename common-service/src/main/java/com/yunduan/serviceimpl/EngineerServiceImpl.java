package com.yunduan.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionAccountDocument;
import com.yunduan.entity.CollectionEngineerDocument;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.entity.Engineer;
import com.yunduan.mapper.CollectionEngineerDocumentMapper;
import com.yunduan.mapper.CollectionFavoritesMapper;
import com.yunduan.mapper.EngineerMapper;
import com.yunduan.mapper.dao.EngineerDao;
import com.yunduan.service.EngineerService;
import com.yunduan.utils.RedisUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.FavoritesVo;
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
    @Autowired
    private CollectionEngineerDocumentMapper collectionEngineerDocumentMapper;
    @Autowired
    private CollectionFavoritesMapper collectionFavoritesMapper;



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


    /**
     * 查询工程师基本信息
     * @param engineerId 工程师id
     * @return engineer
     */
    @Override
    public Engineer engineerBaseInfo(String engineerId) {
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer != null) {
            //收藏夹集合
            List<FavoritesVo> favoritesVoList = new ArrayList<>();
            //添加默认收藏夹
            FavoritesVo vo = new FavoritesVo();
            vo.setId(null).setCount(collectionEngineerDocumentMapper.selectCount(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", engineerId).eq("favorites_id", null))).setFavoritesName("默认收藏夹");
            favoritesVoList.add(vo);
            //查询用户所有收藏夹
            List<CollectionFavorites> favoritesList = collectionFavoritesMapper.selectList(new QueryWrapper<CollectionFavorites>().eq("engineer_id", engineerId));
            if (favoritesList.size() > 0 && favoritesList != null) {
                //得到用户创建收藏夹
                for (CollectionFavorites collectionFavorites : favoritesList) {
                    vo = new FavoritesVo();
                    vo.setId(collectionFavorites.getId().toString()).setFavoritesName(collectionFavorites.getFavoritesName());
                    //收藏夹下的文档总数
                    Integer total = collectionEngineerDocumentMapper.selectCount(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", engineerId).eq("favorites_id", collectionFavorites.getId()).orderByDesc("create_time"));
                    vo.setCount(total);
                    favoritesVoList.add(vo);
                }
            }
            engineer.setFavoritesVoList(favoritesVoList);
            return engineer;
        }
        return null;
    }


    /**
     * 修改工程师头像
     * @param engineerId 工程师id
     * @param headPic 头像地址
     * @return int
     */
    @Override
    public int changeHeadPic(Long engineerId, String headPic) {
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer != null) {
            engineer.setHeadPic(StrUtil.hasEmpty(headPic) ? engineer.getHeadPic() : headPic);
            return engineerMapper.updateById(engineer);
        }
        return 0;
    }


    /**
     * 工程师修改用户名
     * @param engineerId 工程师id
     * @param username 用户名
     * @return int
     */
    @Override
    public int changeUsername(String engineerId, String username) {
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer != null) {
            engineer.setUsername(StrUtil.hasEmpty(username) ? engineer.getUsername() : username);
            return engineerMapper.updateById(engineer);
        }
        return 0;
    }


    /**
     * 工程师换绑手机号
     * @param engineerId 工程师id
     * @param mobile 手机号
     * @return int
     */
    @Override
    public int changeMobile(String engineerId, String mobile) {
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer != null) {
            engineer.setMobile(StrUtil.hasEmpty(mobile) ? engineer.getMobile() : mobile);
            return engineerMapper.updateById(engineer);
        }
        return 0;
    }


}
