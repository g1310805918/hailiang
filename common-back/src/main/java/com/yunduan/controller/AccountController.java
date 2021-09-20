package com.yunduan.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.Account;
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

import java.util.ArrayList;
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


    @RequestMapping(value = "/info/{accountId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取已经注册的用户列表")
    public Result<List<Account>> info(@PathVariable String accountId) {
        Account account = accountService.getById(accountId);
        account.setCreateDateTime(DateUtil.formatDateTime(account.getCreateTime()));
        ArrayList<Account> list = CollectionUtil.newArrayList();
        list.add(account);
        return ResultUtil.data(list);
    }


    @RequestMapping(value = "/binding-csi-list/{accountId}/{pageNo}/{pageSize}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "获取用户绑定的CSI记录")
    public Result<Map<String, Object>> bindingCSIList(@PathVariable("accountId") String accountId,
                                                            @PathVariable("pageNo") String pageNo,
                                                            @PathVariable("pageSize") String pageSize) {
        if (StrUtil.hasEmpty(accountId)) {
            return ResultUtil.error("非法请求");
        }
        Map<String, Object> map = bindingAccountCSIService.initUserAccountCSIRecord(accountId, Convert.toInt(pageNo), Convert.toInt(pageSize));
        return ResultUtil.data(map);
    }


    @RequestMapping(value = "/history-work-order-list/{accountId}/{pageNo}/{pageSize}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "获取用户历史工单记录")
    public Result<Map<String, Object>> historyWorkOrderList(@PathVariable("accountId") String accountId,
                                                        @PathVariable("pageNo") String pageNo,
                                                        @PathVariable("pageSize") String pageSize) {
        if (StrUtil.hasEmpty(accountId)) {
            return ResultUtil.error("非法请求");
        }
        Map<String, Object> map = workOrderService.getAccountHistoryWorkOrderList(accountId, Convert.toInt(pageNo), Convert.toInt(pageSize));
        return ResultUtil.data(map);
    }


}
