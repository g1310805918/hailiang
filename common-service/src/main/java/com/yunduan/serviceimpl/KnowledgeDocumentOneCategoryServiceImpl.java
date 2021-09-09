package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.KnowledgeDocumentOneCategory;
import com.yunduan.entity.KnowledgeDocumentThreeCategory;
import com.yunduan.entity.KnowledgeDocumentTwoCategory;
import com.yunduan.mapper.KnowledgeDocumentOneCategoryMapper;
import com.yunduan.mapper.KnowledgeDocumentThreeCategoryMapper;
import com.yunduan.mapper.KnowledgeDocumentTwoCategoryMapper;
import com.yunduan.service.KnowledgeDocumentOneCategoryService;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.ProductNameVersionTwoVo;
import com.yunduan.vo.ProductNameVersionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class KnowledgeDocumentOneCategoryServiceImpl extends ServiceImpl<KnowledgeDocumentOneCategoryMapper, KnowledgeDocumentOneCategory> implements KnowledgeDocumentOneCategoryService {

    @Autowired
    private KnowledgeDocumentOneCategoryMapper knowledgeDocumentOneCategoryMapper;
    @Autowired
    private KnowledgeDocumentTwoCategoryMapper knowledgeDocumentTwoCategoryMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryMapper threeCategoryMapper;


    /**
     * 获取知识文档库一级分类列表
     * @return list
     */
    @Override
    public List<KnowledgeOneCategoryVo> getKnowledgeDocOneCategoryList() {
        List<KnowledgeOneCategoryVo> voList = new ArrayList<>();
        //所有1级分类列表
        List<KnowledgeDocumentOneCategory> oneCategories = knowledgeDocumentOneCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentOneCategory>().orderByDesc("id"));
        if (oneCategories.size() > 0 && oneCategories != null) {
            KnowledgeOneCategoryVo vo = null;
            for (KnowledgeDocumentOneCategory oneCategory : oneCategories) {
                vo = new KnowledgeOneCategoryVo();
                vo.setId(oneCategory.getId().toString()).setTitle(oneCategory.getCategoryTitle());
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 获取前两级分类
     * @return list
     */
    @Override
    public List<ProductNameVersionVo> queryBeginOneTwoLevelCategoryList() {
        List<ProductNameVersionVo> voList = new ArrayList<>();
        //所有一级分类列表
        List<KnowledgeDocumentOneCategory> oneCategories = knowledgeDocumentOneCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentOneCategory>().orderByDesc("id"));
        if (oneCategories.size() > 0 && oneCategories != null) {
            ProductNameVersionVo oneVo = null;
            for (KnowledgeDocumentOneCategory oneCategory : oneCategories) {
                oneVo = new ProductNameVersionVo();
                oneVo.setOneCategoryName(oneCategory.getCategoryTitle());
                //二级分类结果封装
                List<ProductNameVersionTwoVo> twoVoList = new ArrayList<>();
                //二级分类列表
                List<KnowledgeDocumentTwoCategory> twoCategories = knowledgeDocumentTwoCategoryMapper.selectList(new QueryWrapper<KnowledgeDocumentTwoCategory>().eq("one_category_id", oneCategory.getId()));
                if (twoCategories.size() > 0 && twoCategories != null) {
                    ProductNameVersionTwoVo twoVo = null;
                    for (KnowledgeDocumentTwoCategory twoCategory : twoCategories) {
                        twoVo = new ProductNameVersionTwoVo();
                        twoVo.setTwoCategoryId(twoCategory.getId().toString()).setTwoCategoryName(twoCategory.getCategoryTitle());
                        twoVoList.add(twoVo);
                    }
                }
                //设置一级分类下的二级分类
                oneVo.setTwoCategoryList(twoVoList);
                voList.add(oneVo);
            }
        }
        return voList;
    }


    /**
     * 分页初始化一级分类列表
     * @param categoryName 分类名称
     * @param pageNo 页号
     * @param pageSize 页面大小
     * @return map
     */
    @Override
    public Map<String, Object> initOneCategoryData(String categoryName, Integer pageNo, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        //条件构造器
        QueryWrapper<KnowledgeDocumentOneCategory> queryWrapper = new QueryWrapper<KnowledgeDocumentOneCategory>().like(StrUtil.isNotEmpty(categoryName), "category_title", categoryName).orderByDesc("create_time");
        //查询结果
        List<KnowledgeDocumentOneCategory> records = knowledgeDocumentOneCategoryMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper).getRecords();
        if (records.size() > 0 && records != null) {
            records.forEach( item -> {
                item.setLevel("1级");
            });
        }
        map.put("voList",records);
        map.put("total",knowledgeDocumentOneCategoryMapper.selectCount(queryWrapper));
        return map;
    }


    /**
     * 添加分类
     * @param categoryName 分类名称
     * @param parentId 上级id
     * @param level 等级
     * @return int
     */
    @Override
    public int createOneLevel(String categoryName, String parentId, String level) {
        if (Objects.equals("1",level)) {
            KnowledgeDocumentOneCategory oneLevel = knowledgeDocumentOneCategoryMapper.selectOne(new QueryWrapper<KnowledgeDocumentOneCategory>().eq("category_title", categoryName));
            if (oneLevel != null) {
                return StatusCodeUtil.HAS_EXIST;
            }
            oneLevel = new KnowledgeDocumentOneCategory();

            oneLevel.setId(SnowFlakeUtil.getPrimaryKeyId()).setCategoryTitle(categoryName).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setCreateTime(DateUtil.now());
            return knowledgeDocumentOneCategoryMapper.insert(oneLevel);
        }else if (Objects.equals("2",level)) {
            KnowledgeDocumentTwoCategory twoCategory = knowledgeDocumentTwoCategoryMapper.selectOne(new QueryWrapper<KnowledgeDocumentTwoCategory>().eq("category_title", categoryName));
            if (twoCategory != null) {
                return StatusCodeUtil.HAS_EXIST;
            }
            twoCategory = new KnowledgeDocumentTwoCategory().setId(SnowFlakeUtil.getPrimaryKeyId()).setCategoryTitle(categoryName).setOneCategoryId(Convert.toLong(parentId)).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG);
            return knowledgeDocumentTwoCategoryMapper.insert(twoCategory);
        }else {
            KnowledgeDocumentThreeCategory threeCategory = threeCategoryMapper.selectOne(new QueryWrapper<KnowledgeDocumentThreeCategory>().eq("category_title", categoryName));
            if (threeCategory != null) {
                return StatusCodeUtil.HAS_EXIST;
            }
            threeCategory = new KnowledgeDocumentThreeCategory().setId(SnowFlakeUtil.getPrimaryKeyId()).setCategoryTitle(categoryName).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setTwoCategoryId(Convert.toLong(parentId));
            return threeCategoryMapper.insert(threeCategory);
        }
    }


}
