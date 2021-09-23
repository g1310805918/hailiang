package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.Engineer;
import com.yunduan.service.AccountService;
import com.yunduan.service.EngineerService;
import com.yunduan.service.WorkOrderService;
import com.yunduan.utils.RedisUtil;
import com.yunduan.utils.StatusCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = {"统计接口"})
@RequestMapping("/statistical")
public class StatisticalController {

    private static final transient Logger log = LoggerFactory.getLogger(StatisticalController.class);

    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation(httpMethod = "GET",value = "工单概况统计")
    @RequestMapping(value = "/get-work-order-info",method = RequestMethod.GET)
    public Result<Map<String, Integer>> getWorkOrderInfo() {
        Map<String, Integer> resultMap = workOrderService.getWorkOrderSurvey();
        return ResultUtil.data(resultMap);
    }


    @ApiOperation(httpMethod = "GET",value = "用户概况--基本信息")
    @RequestMapping(value = "/account-base-info",method = RequestMethod.GET)
    public Result<Map<String, Integer>> accountBaseInfo() {
        Map<String, Integer> map = accountService.getAccountSurveyBaseInfo();
        return ResultUtil.data(map);
    }


    @ApiOperation(httpMethod = "GET",value = "用户概况--柱状图信息")
    @RequestMapping(value = "/account-bar-info",method = RequestMethod.GET)
    public Result<Map<String, Object>> accountBarInfo() {
        String redisValue = redisUtil.getKeyValue(StatusCodeUtil.ACCOUNT_BAR_DATA);
        if (StrUtil.isNotEmpty(redisValue)) {
            Map<String,Object> map = JSONObject.parseObject(redisValue, Map.class);
            return ResultUtil.data(map);
        }
        Map<String, Object> resultMap = accountService.getAccountSurveyBarInfo();
        redisUtil.setStringKeyValue(StatusCodeUtil.ACCOUNT_BAR_DATA,JSONObject.toJSONString(resultMap),1, TimeUnit.HOURS);
        return ResultUtil.data(resultMap);
    }


    @ApiOperation(httpMethod = "GET",value = "获取工程师概况--统计数据")
    @RequestMapping(value = "/engineer-base-info",method = RequestMethod.GET)
    public Result<List<Integer>> engineerBaseInfo() {
        List<Integer> list = engineerService.engineerBaseCountInfo();
        return ResultUtil.data(list);
    }


    @ApiOperation(httpMethod = "GET",value = "获取工程师概况--排行榜")
    @RequestMapping(value = "/engineer-rank-info",method = RequestMethod.GET)
    public Result<List<Engineer>> engineerRankInfo() {
        List<Engineer> engineerList = engineerService.engineerRankList();
        return ResultUtil.data(engineerList);
    }

}
