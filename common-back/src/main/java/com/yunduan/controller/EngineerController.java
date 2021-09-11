package com.yunduan.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.Engineer;
import com.yunduan.request.back.EngineerInit;
import com.yunduan.service.EngineerService;
import com.yunduan.utils.StatusCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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





}
