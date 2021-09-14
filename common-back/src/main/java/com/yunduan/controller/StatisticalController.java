package com.yunduan.controller;


import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.service.WorkOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = {"统计接口"})
@RequestMapping("/statistical")
public class StatisticalController {

    private static final transient Logger log = LoggerFactory.getLogger(StatisticalController.class);

    @Autowired
    private WorkOrderService workOrderService;


    @RequestMapping(value = "/init-info-card-count",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "获取infoCard统计数据")
    public Result<Map<String, Integer>> initInfoCardCount() {
        Map<String, Integer> map = workOrderService.statisticalInitInfoCardCount();
        return ResultUtil.data(map);
    }


    @RequestMapping(value = "/init-table-count",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "统计图标信息")
    public Result<List<String>> initTableCount() {
        return ResultUtil.data(workOrderService.queryInitTableInfo());
    }



}
