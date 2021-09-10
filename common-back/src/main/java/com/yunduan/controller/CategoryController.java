package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.*;
import com.yunduan.service.*;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.KnowledgeOneCategoryVo;
import com.yunduan.vo.ProductNameVersionThreeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Api(tags = {"分类接口"})
@RequestMapping("/category")
public class CategoryController {

    private static final transient Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private KnowledgeDocumentOneCategoryService oneCategoryService;
    @Autowired
    private KnowledgeDocumentTwoCategoryService twoCategoryService;
    @Autowired
    private KnowledgeDocumentThreeCategoryService threeCategoryService;
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private EngineerService engineerService;


    @ApiOperation("初始化一级分类列表")
    @RequestMapping(value = "/init-one-category-list", method = RequestMethod.GET)
    public Result<Map<String, Object>> getInitOneCategoryList(String categoryName, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            log.error("初始化一级分类列表【pageNo、pageSize】为空");
            return ResultUtil.error("非法请求");
        }
        Map<String, Object> map = oneCategoryService.initOneCategoryData(categoryName, pageNo, pageSize);
        return ResultUtil.data(map);
    }


    @PostMapping("/edit-categoryName")
    @ApiOperation(httpMethod = "POST", value = "编辑分类名称")
    public Result<String> editCategoryName(String categoryId, String categoryName, String level) {
        if (StrUtil.hasEmpty(categoryId) || StrUtil.hasEmpty(categoryName) || StrUtil.hasEmpty(level)) {
            log.error("编辑分类名称【categoryId、categoryName】为空");
            return ResultUtil.error("非法请求");
        }
        boolean flag = false;
        if (Objects.equals("1", level)) {
            KnowledgeDocumentOneCategory oneCategory = oneCategoryService.getById(categoryId);
            if (oneCategory != null) {
                oneCategory.setCategoryTitle(categoryName);
                flag = oneCategoryService.updateById(oneCategory);
            }
        } else if (Objects.equals("2", level)) {
            KnowledgeDocumentTwoCategory twoCategory = twoCategoryService.getById(categoryId);
            if (twoCategory != null) {
                twoCategory.setCategoryTitle(categoryName);
                flag = twoCategoryService.updateById(twoCategory);
            }
        } else {
            KnowledgeDocumentThreeCategory threeCategory = threeCategoryService.getById(categoryId);
            if (threeCategory != null) {
                threeCategory.setCategoryTitle(categoryName);
                flag = threeCategoryService.updateById(threeCategory);
            }
        }
        return flag ? ResultUtil.data("", "操作成功") : ResultUtil.error("操作失败");
    }


    @GetMapping("/remove-one-category/{id}")
    @ApiOperation(httpMethod = "GET", value = "删除一级分类")
    public Result<String> removeOneCategory(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("删除一级分类【id】为空");
            return ResultUtil.error("非法请求");
        }
        //id集合
        List<String> idList = Arrays.asList(id.split(","));
        int rows = twoCategoryService.count(new QueryWrapper<KnowledgeDocumentTwoCategory>().in("one_category_id", idList));
        if (rows > 0) {
            return ResultUtil.error(StatusCodeUtil.SYSTEM_ERROR, "分类下存在下级，删除失败！");
        }
        boolean flag = oneCategoryService.removeByIds(idList);
        return flag ? ResultUtil.data("", "操作成功") : ResultUtil.error("操作失败");
    }


    @GetMapping("/get-two-category-list/{oneLevelId}")
    @ApiOperation(httpMethod = "GET", value = "获取二级分类列表")
    public Result<List<KnowledgeOneCategoryVo>> getTwoCategoryList(@PathVariable String oneLevelId) {
        if (StrUtil.hasEmpty(oneLevelId)) {
            log.error("获取二级分类列表【oneLevelId】为空");
            return ResultUtil.error("非法请求");
        }
        List<KnowledgeOneCategoryVo> voList = twoCategoryService.queryTwoCategoryList(oneLevelId);
        return ResultUtil.data(voList);
    }


    @GetMapping("/remove-two-category/{id}")
    @ApiOperation(httpMethod = "GET", value = "删除二级分类")
    public Result<String> removeTwoCategory(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("删除二级分类【id】为空");
            return ResultUtil.error("非法请求");
        }
        List<String> idList = Arrays.asList(id.split(","));
        int rows = threeCategoryService.count(new QueryWrapper<KnowledgeDocumentThreeCategory>().in("two_category_id", idList));
        if (rows > 0) {
            return ResultUtil.error(StatusCodeUtil.SYSTEM_ERROR, "分类下存在下级，删除失败！");
        }
        boolean flag = twoCategoryService.removeByIds(idList);
        return flag ? ResultUtil.data("", "操作成功") : ResultUtil.error("操作失败");
    }


    @GetMapping("/get-three-category-list/{twoCategoryId}")
    @ApiOperation(httpMethod = "GET", value = "获取三级列表分类")
    public Result<List<ProductNameVersionThreeVo>> getThreeCategoryList(@PathVariable String twoCategoryId) {
        if (StrUtil.hasEmpty(twoCategoryId)) {
            log.error("获取三级列表分类【twoCategoryId】为空");
            return ResultUtil.error("非法请求");
        }
        List<ProductNameVersionThreeVo> voList = threeCategoryService.queryThreeLevelCategoryVo(twoCategoryId);
        return ResultUtil.data(voList);
    }


    @GetMapping("/remove-three-category/{id}")
    @ApiOperation(httpMethod = "GET", value = "删除三级分类")
    public Result<String> removeThreeCategory(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("删除三级分类【id】为空");
            return ResultUtil.error("非法请求");
        }
        List<String> list = Arrays.asList(id.split(","));
        int rows = workOrderService.count(new QueryWrapper<WorkOrder>().in("category_id", list));
        if (rows > 0) {
            return ResultUtil.error("分类下存在用户工单绑定信息，删除失败。");
        }
        rows = 0;
        for (String s : list) {
            rows += engineerService.count(new QueryWrapper<Engineer>().eq("account_status", StatusCodeUtil.ENGINEER_ACCOUNT_NORMAL_STATUS).like("product_category_id", s));
            if (rows > 0) {
                return ResultUtil.error("存在绑定工程师、删除失败。");
            }
        }
        boolean flag = threeCategoryService.removeById(id);
        return flag ? ResultUtil.success("操作成功") : ResultUtil.error("操作失败");
    }


    @RequestMapping(value = "/add-one-level-category", method = RequestMethod.POST)
    @ApiOperation("添加一级分类")
    public Result<String> addOneLevelCategory(String categoryName, String parentId, String level) {
        if (StrUtil.hasEmpty(categoryName) || StrUtil.hasEmpty(level)) {
            log.error("添加一级分类 【categoryName、level】为空");
            return ResultUtil.error("非法请求");
        }
        int rows = oneCategoryService.createOneLevel(categoryName,parentId,level);
        if (rows == StatusCodeUtil.HAS_EXIST) {
            return ResultUtil.error("该分类已存在");
        }
        return rows > 0 ? ResultUtil.success("操作成功") : ResultUtil.error("操作失败");
    }


}
