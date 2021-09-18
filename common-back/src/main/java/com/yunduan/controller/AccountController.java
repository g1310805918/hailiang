package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.WorkOrder;
import com.yunduan.service.AccountService;
import com.yunduan.service.BindingAccountCSIService;
import com.yunduan.service.WorkOrderService;
import com.yunduan.vo.CustomerServiceNoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
@Api(tags = {"会员信息接口"})
public class AccountController {


    @Autowired
    private AccountService accountService;
    @Autowired
    private BindingAccountCSIService bindingAccountCSIService;
    @Autowired
    private WorkOrderService workOrderService;


    @RequestMapping(value = "/registered-list", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取已经注册的用户列表")
    public Result<Map<String, Object>> registeredList(String username, String mobile, String email, Integer pageNo, Integer pageSize) {
        Map<String, Object> map = accountService.queryAllRegisteredAccountList(username, mobile, email, pageNo, pageSize);
        return ResultUtil.data(map);
    }


    @RequestMapping(value = "/binding-csi-list/{accountId}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "获取用户绑定的CSI记录")
    public Result<List<CustomerServiceNoVo>> bindingCSIList(@PathVariable String accountId) {
        if (StrUtil.hasEmpty(accountId)) {
            return ResultUtil.error("非法请求");
        }
        List<CustomerServiceNoVo> voList = bindingAccountCSIService.queryCustomerServiceList(accountId);
        return ResultUtil.data(voList);
    }


    @RequestMapping(value = "/history-work-order-list/{accountId}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "获取用户历史工单记录")
    public Result<List<WorkOrder>> historyWorkOrderList(@PathVariable String accountId) {
        if (StrUtil.hasEmpty(accountId)) {
            return ResultUtil.error("非法请求");
        }
        List<WorkOrder> workOrderList = workOrderService.getAccountHistoryWorkOrderList(accountId);
        return ResultUtil.data(workOrderList);
    }


}
