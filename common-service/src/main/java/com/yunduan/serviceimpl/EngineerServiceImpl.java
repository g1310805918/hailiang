package com.yunduan.serviceimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.*;
import com.yunduan.mapper.CollectionEngineerDocumentMapper;
import com.yunduan.mapper.CollectionFavoritesMapper;
import com.yunduan.mapper.EngineerMapper;
import com.yunduan.mapper.KnowledgeDocumentThreeCategoryMapper;
import com.yunduan.mapper.dao.EngineerDao;
import com.yunduan.request.back.EngineerInit;
import com.yunduan.service.EngineerService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.RedisUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.EngineerCategoryListVo;
import com.yunduan.vo.FavoritesVo;
import com.yunduan.vo.OtherEngineerListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
    @Autowired
    private KnowledgeDocumentThreeCategoryMapper threeCategoryMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService threeCategoryService;



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


    /**
     * 初始化工程师列表
     * @param engineerInit 初始化对象
     * @return map
     */
    @Override
    public Map<String,Object> engineerListInit(EngineerInit engineerInit) {
        Map<String,Object> map = new HashMap<>();
        //条件构造器
        QueryWrapper<Engineer> queryWrapper = new QueryWrapper<Engineer>()
                .eq(engineerInit.getIdentity() != null, "identity", engineerInit.getIdentity())
                .eq(engineerInit.getAccountStatus() != null, "account_status", engineerInit.getAccountStatus())
                .like(StrUtil.isNotEmpty(engineerInit.getUsername()), "username", engineerInit.getUsername())
                .like(StrUtil.isNotEmpty(engineerInit.getMobile()), "mobile", engineerInit.getMobile())
                .like(StrUtil.isNotEmpty(engineerInit.getEmail()), "email", engineerInit.getEmail())
                .orderByDesc("create_time");
        //工程师列表
        List<Engineer> engineerList = engineerMapper.selectPage(new Page<>(engineerInit.getPageNo(), engineerInit.getPageSize()), queryWrapper).getRecords();
        //总数
        Integer total = engineerMapper.selectCount(queryWrapper);
        map.put("voList",engineerList);
        map.put("total",total);
        return map;
    }


    /**
     * 添加工程师
     * @param engineer 工程师
     * @return int
     */
    @Override
    public int createEngineer(Engineer engineer) {
        String idStr = "";
        String categoryName = "";
        List<String> categoryId = engineer.getCategoryId();
        for (String id : categoryId) {
            idStr = idStr + id + ",";
            //获取三级分类名称
            KnowledgeDocumentThreeCategory threeCategory = threeCategoryMapper.selectById(id);
            if (threeCategory != null) {
                categoryName += threeCategory.getCategoryTitle() + "/";
            }
        }
        engineer.setPassword(AESUtil.encrypt(engineer.getPassword()));  //加密密码
        engineer.setIdentityName(engineer.getIdentity() == 1 ? "海量员工" : engineer.getIdentity() == 2 ? "技术支持工程师" : engineer.getIdentity() == 3 ? "COE工程师" : engineer.getIdentity() == 4 ? "BDE工程师" : "Manager");
        engineer.setProductCategoryId(idStr).setProductCategoryName(categoryName).setOrderNumber(0);
        return engineerMapper.insert(engineer);
    }



    /**
     * 编辑工程师基本信息
     * @param engineer 工程师对象
     * @return int
     */
    @Override
    public int editEngineerBaseInfo(Engineer engineer) {
        Engineer oldEngineer = engineerMapper.selectById(engineer.getId());
        if (oldEngineer != null) {
            oldEngineer.setUsername(engineer.getUsername()).setMobile(engineer.getMobile()).setEmail(engineer.getEmail()).setIdentity(engineer.getIdentity()).setIdentityName(engineer.getIdentity() == 1 ? "海量员工" : engineer.getIdentity() == 2 ? "技术支持工程师" : engineer.getIdentity() == 3 ? "COE工程师" : engineer.getIdentity() == 4 ? "BDE工程师" : "Manager");
            return engineerMapper.updateById(oldEngineer);
        }
        return 0;
    }


    /**
     * 加载工程师技术模块集合
     * @param engineerId 工程师id
     * @return list
     */
    @Override
    public List<EngineerCategoryListVo> loadEngineerCategoryList(String engineerId) {
        List<EngineerCategoryListVo> voList = new ArrayList<>();
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer != null) {
            if (StrUtil.hasEmpty(engineer.getProductCategoryId())) {
                return voList;
            }
            //工程师技术模块id集合
            List<String> categoryIdList = Arrays.asList(engineer.getProductCategoryId().split(","));
            if (categoryIdList.size() > 0 && categoryIdList != null ){
                EngineerCategoryListVo vo = null;
                for (String categoryId : categoryIdList) {
                    vo = new EngineerCategoryListVo().setCategoryId(categoryId).setCategoryName(threeCategoryService.getKnowledgeCategoryName(categoryId));
                    voList.add(vo);
                }
            }
        }
        return voList;
    }


    /**
     * 批量删除工程师技术模块
     * @param engineerId 工程师id
     * @param batchId 技术模块id
     * @return int
     */
    @Override
    public int removeBatchEngineerCategory(String engineerId, String batchId) {
        int rows = 0;
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer != null) {
            if (StrUtil.hasEmpty(engineer.getProductCategoryId())) {
                return StatusCodeUtil.NOT_FOUND_FLAG;
            }
            List<String> list = Arrays.asList(batchId.split(","));

            String productCategoryId = engineer.getProductCategoryId();

            for (String s : list) {
                if (productCategoryId.contains(s)) {
                    productCategoryId = productCategoryId.replaceAll(s, "");
                }
            }

            engineer.setProductCategoryId(productCategoryId);

            List<String> productIdList = Arrays.asList(productCategoryId.split(","));
            if (productIdList != null && productIdList.size() > 0) {
                String productCategoryName = "";
                for (String s : productIdList) {
                    KnowledgeDocumentThreeCategory threeCategory = threeCategoryMapper.selectById(s);
                    if (threeCategory != null) {
                        productCategoryName += threeCategory.getCategoryTitle() + "/";
                    }
                }
                engineer.setProductCategoryName(productCategoryName);
            }else {
                engineer.setProductCategoryName("");
            }
            return engineerMapper.updateById(engineer);
        }
        return rows;
    }



    /**
     * 获取工程师所没有的技术模块列表
     * @param engineerId 工程师id
     * @return list
     */
    @Override
    public List<KnowledgeDocumentThreeCategory> getEngineerHaveNotCategoryList(String engineerId) {
        //工程师已有技术模块集合
        List<String> hasCategoryIdList = new ArrayList<>();
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer == null) {
            return null;
        }
        if (StrUtil.isNotEmpty(engineer.getProductCategoryId())) {
            hasCategoryIdList = Arrays.asList(engineer.getProductCategoryId().split(","));
        }
        //工程师没有的技术模块集合
        List<KnowledgeDocumentThreeCategory> threeCategories = threeCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentThreeCategory>().notIn(hasCategoryIdList.size() > 0 && hasCategoryIdList != null, "id", hasCategoryIdList).orderByDesc("create_time"));
        return threeCategories;
    }


    /**
     * 添加工程师技术模块
     * @param engineerId 工程师id
     * @param categoryIdList 技术模块id集合
     * @return int
     */
    @Override
    public int addEngineerHasNotCategory(String engineerId,String[] categoryIdList) {
        Engineer engineer = engineerMapper.selectById(engineerId);
        if (engineer != null) {
            List<String> result = new ArrayList<>();
            //已有的技术模块
            String hasCategoryId = engineer.getProductCategoryId();
            if (StrUtil.isNotEmpty(hasCategoryId)) {
                //合并已有模块 、 需要添加模块
                result.addAll(Arrays.asList(categoryIdList));
                result.addAll(Arrays.asList(hasCategoryId.split(",")));
            }
            String str = "";
            String categoryName = "";
            for (String s : result) {
                str += s + ",";
                KnowledgeDocumentThreeCategory threeCategory = threeCategoryMapper.selectById(s);
                if (threeCategory != null) {
                    categoryName += threeCategory.getCategoryTitle() + "/";
                }
            }
            engineer.setProductCategoryId(str).setProductCategoryName(categoryName);

            return engineerMapper.updateById(engineer);
        }
        return 0;
    }


    /**
     * 获取工程师概况统计数据
     * @return List
     */
    @Override
    public List<Integer> engineerBaseCountInfo() {
        //总工程师数
        Integer count = engineerMapper.selectCount(new QueryWrapper<Engineer>());
        ArrayList<Integer> list = CollectionUtil.newArrayList();
        list.add(1);
        list.add(2);
        //普通工程师总计
        Integer normalEngineerCount = engineerMapper.selectCount(new QueryWrapper<Engineer>().in("identity", list));
        //COE工程师数
        Integer COEEngineerCount = engineerMapper.selectCount(new QueryWrapper<Engineer>().eq("identity", 3));
        //BDE工程师数
        Integer BDEEngineerCount = engineerMapper.selectCount(new QueryWrapper<Engineer>().eq("identity", 4));
        //manager工程师数
        Integer ManagerEngineerCount = engineerMapper.selectCount(new QueryWrapper<Engineer>().eq("identity", 5));
        list = CollectionUtil.newArrayList();
        list.add(count);
        list.add(normalEngineerCount);
        list.add(COEEngineerCount);
        list.add(BDEEngineerCount);
        list.add(ManagerEngineerCount);
        return list;
    }



    /**
     * 获取工程师排名
     * @return List
     */
    @Override
    public List<Engineer> engineerRankList() {
        //条件构造器
        QueryWrapper<Engineer> queryWrapper = new QueryWrapper<Engineer>().orderByDesc("order_number");
        List<Engineer> engineerList = engineerMapper.selectPage(new Page<>(1, 4), queryWrapper).getRecords();
        return engineerList;
    }


}
