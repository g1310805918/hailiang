package com.yunduan.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.KnowledgeDocumentThreeCategory;
import com.yunduan.entity.WorkOrder;
import com.yunduan.request.back.EngineerInit;
import com.yunduan.service.EngineerService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.service.WorkOrderService;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.EngineerCategoryListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/engineer")
@Api(tags = {"工程师管理接口"})
public class EngineerController {

    private static final transient Logger log = LoggerFactory.getLogger(EngineerController.class);

    @Autowired
    private EngineerService engineerService;
    @Autowired
    private KnowledgeDocumentThreeCategoryService threeCategoryService;
    @Autowired
    private WorkOrderService workOrderService;


    @PostMapping("/init-list")
    @ApiOperation(httpMethod = "POST",value = "工程师管理页面初始化")
    public Result<Map<String, Object>> initList(EngineerInit engineerInit) {
        Map<String, Object> map = engineerService.engineerListInit(engineerInit);
        return ResultUtil.data(map);
    }


    @PostMapping("/disable-engineer/{engineerId}/{accountStatus}")
    @ApiOperation(httpMethod = "POST",value = "禁用工程师账号")
    public Result<String> disableEngineer(@PathVariable("engineerId") String engineerId, @PathVariable("accountStatus") String accountStatus) {
        if (StrUtil.hasEmpty(engineerId) || StrUtil.hasEmpty(accountStatus)) {
            log.error("禁用工程师账号【engineerId、accountStatus】为空");
            return ResultUtil.error("非法请求");
        }
        boolean flag = false;
        Engineer engineer = engineerService.getById(engineerId);
        if (engineer != null) {
            engineer.setAccountStatus(Convert.toInt(accountStatus));
            flag = engineerService.updateById(engineer);
        }
        return flag ? ResultUtil.success("操作成功") : ResultUtil.error("操作失败");
    }


    @PostMapping("/remove-engineer/{engineerId}")
    @ApiOperation(httpMethod = "POST",value = "删除工程师")
    public Result<String> removeEngineer(@PathVariable String engineerId) {
        if (StrUtil.hasEmpty(engineerId)) {
            log.error("删除工程师【engineerId】为空");
            return ResultUtil.error("非法请求");
        }
        List<String> idList = Arrays.asList(engineerId.split(","));
        boolean flag = engineerService.removeByIds(idList);
        return flag ? ResultUtil.success("删除成功") : ResultUtil.error("删除失败");
    }


    @GetMapping("/get-all-three-category-list")
    @ApiOperation(httpMethod = "GET",value = "获取所有技术模块集合")
    public Result<List<KnowledgeDocumentThreeCategory>> getAllThreeCategoryList() {
        //三级技术模块集合
        List<KnowledgeDocumentThreeCategory> threeCategories = threeCategoryService.list(new QueryWrapper<KnowledgeDocumentThreeCategory>().orderByDesc("create_time"));
        return ResultUtil.data(threeCategories);
    }


    @PostMapping("/create-engineer")
    @ApiOperation(httpMethod = "POST",value = "添加工程师")
    public Result<String> createEngineer(Engineer engineer) {
        if (engineer == null) {
            return ResultUtil.error("非法请求");
        }
        int row = engineerService.createEngineer(engineer);
        return row > 0 ? ResultUtil.success("添加成功") : ResultUtil.error("添加失败");
    }


    @PostMapping("/edit-engineer-base-info")
    @ApiOperation(httpMethod = "POST",value = "修改工程师基本信息")
    public Result<String> editEngineerBaseInfo(Engineer engineer) {
        if (engineer == null) {
            return ResultUtil.error("非法请求");
        }
        int row = engineerService.editEngineerBaseInfo(engineer);
        return row > 0 ? ResultUtil.success("修改成功") : ResultUtil.error("修改失败");
    }


    @GetMapping("/get-engineer-category/{engineerId}")
    @ApiOperation(httpMethod = "GET",value = "获取工程师技术模块列表")
    public Result<List<EngineerCategoryListVo>> getEngineerCategory(@PathVariable String engineerId) {
        if (StrUtil.hasEmpty(engineerId)) {
            log.error("获取工程师技术模块列表【engineerId】为空");
            return ResultUtil.error("非法请求");
        }
        List<EngineerCategoryListVo> voList = engineerService.loadEngineerCategoryList(engineerId);
        return ResultUtil.data(voList);
    }


    @PostMapping("/remove-engineer-category")
    @ApiOperation(httpMethod = "POST",value = "批量删除工程师技术模块")
    public Result<String> removeEngineerCategory(String engineerId,String batchId) {
        if (StrUtil.hasEmpty(engineerId) || StrUtil.hasEmpty(batchId)) {
            log.error("批量删除工程师技术模块【engineerId、batchIds】为空");
            return ResultUtil.error("非法请求");
        }
        int rows = engineerService.removeBatchEngineerCategory(engineerId, batchId);
        if (rows == StatusCodeUtil.NOT_DELETE_FLAG) {
            return ResultUtil.error("工程师还没有分配技术模块");
        }
        return rows > 0 ? ResultUtil.success("删除成功") : ResultUtil.error("删除失败");
    }


    @GetMapping("/get-engineer-have-not-category")
    @ApiOperation(httpMethod = "GET",value = "获取工程师没有的技术模块列表")
    public Result<List<KnowledgeDocumentThreeCategory>> getEngineerHaveNotCategory(String engineerId) {
        List<KnowledgeDocumentThreeCategory> list = engineerService.getEngineerHaveNotCategoryList(engineerId);
        return ResultUtil.data(list);
    }


    @PostMapping("/submit-add-engineer-category")
    @ApiOperation(httpMethod = "POST",value = "确定添加工程师技术模块")
    public Result<String> submitAddEngineerCategory(String engineerId,String[] productCategoryId) {
        if (StrUtil.hasEmpty(engineerId) || productCategoryId == null || productCategoryId.length == 0) {
            log.error("确定添加工程师技术模块【engineerId、productCategoryId】为空");
            return ResultUtil.error("非法请求");
        }
        int row = engineerService.addEngineerHasNotCategory(engineerId, productCategoryId);
        return row > 0 ? ResultUtil.success("添加成功") : ResultUtil.error("添加失败");
    }


    @RequestMapping(value = "/get-engineer-type-work-order",method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST",value = "获取工程师类型工单")
    public Result<List<WorkOrder>> getEngineerTypeWorkOrder(String engineerId,String tagName) {
        List<WorkOrder> workOrder = workOrderService.getEngineerTypeWorkOrder(engineerId, tagName);
        return ResultUtil.data(workOrder);
    }


}
