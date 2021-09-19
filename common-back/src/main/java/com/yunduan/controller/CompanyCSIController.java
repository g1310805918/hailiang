package com.yunduan.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.Account;
import com.yunduan.entity.BindingAccountCSI;
import com.yunduan.entity.CompanyCSI;
import com.yunduan.request.back.CompanyCSIInit;
import com.yunduan.service.BindingAccountCSIService;
import com.yunduan.service.CompanyCSIService;
import com.yunduan.utils.StatusCodeUtil;
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
@RequestMapping("/company")
@Api(tags = {"客户服务号CSI接口"})
public class CompanyCSIController {

    private static final transient Logger log = LoggerFactory.getLogger(CompanyCSIController.class);

    @Autowired
    private CompanyCSIService companyCSIService;
    @Autowired
    private BindingAccountCSIService bindingAccountCSIService;


    @PostMapping("/init-page-data")
    @ApiOperation(httpMethod = "POST",value = "初始化页面数据")
    public Result<Map<String, Object>> initPageData(CompanyCSIInit companyCSIInit) {
        Map<String, Object> map = companyCSIService.initPageData(companyCSIInit);
        return ResultUtil.data(map);
    }


    @PostMapping("/remove-batch/{batchIds}")
    @ApiOperation(httpMethod = "POST",value = "删除客户服务号")
    public Result<String> removeBatch(@PathVariable String batchIds) {
        if (StrUtil.hasEmpty(batchIds)) {
            log.error("删除客户服务号【batchIds】为空");
            return ResultUtil.error("非法请求");
        }
        List<String> list = Arrays.asList(batchIds.split(","));
        List<BindingAccountCSI> count = bindingAccountCSIService.list(new QueryWrapper<BindingAccountCSI>().in("csi_id", list));
        if (count != null && count.size() > 0) {
            return ResultUtil.error("存在绑定用户，删除失败");
        }
        boolean flag = companyCSIService.removeByIds(list);
        return flag ? ResultUtil.success("删除成功") : ResultUtil.error("删除失败");
    }


    @PostMapping("/submit-edit")
    @ApiOperation(httpMethod = "POST",value = "提交修改客户服务号信息")
    public Result<String> submitEdit(CompanyCSI companyCSI) {
        int row = companyCSIService.changeCompanyCSIInfo(companyCSI);
        return row > 0 ? ResultUtil.success("修改成功") : ResultUtil.error("删除失败");
    }


    @PostMapping("/commit-add-company-csi")
    @ApiOperation(httpMethod = "POST",value = "添加CSI编号")
    public Result<String> commitAddCompanyCSI(CompanyCSI companyCSI) {
        int rows = companyCSIService.createCompanyCSI(companyCSI);
        if (rows == StatusCodeUtil.HAS_EXIST) {
            return ResultUtil.error("CSI编号已存在。");
        }
        return rows > 0 ? ResultUtil.success("添加成功") : ResultUtil.error("添加失败");
    }


    @PostMapping("/commit-batch-company-csi")
    @ApiOperation(httpMethod = "POST",value = "批量导入CSI编号信息")
    public Result<String> commitBatchCompanyCSI(@RequestBody List<CompanyCSI> companyCSIS) {
        //得到可以导入的数据集合
        List<CompanyCSI> batch = companyCSIService.createBatch(companyCSIS);
        //导入
        boolean flag = companyCSIService.saveBatch(batch);
        return flag ? ResultUtil.success("导入成功") : ResultUtil.error("导入失败");
    }


    @RequestMapping(value = "/drop-account-list/{companyCSIId}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "获取CSI编号下绑定的用户列表")
    public Result<List<Account>> dropAccountList(@PathVariable String companyCSIId) {
        if (StrUtil.hasEmpty(companyCSIId)) {
            log.error("获取CSI编号下绑定的用户列表【companyCSIId】为空");
            return ResultUtil.error("非法请求");
        }
        List<Account> accountList = companyCSIService.queryCompanyDropCSIBindingRecord(companyCSIId);
        if (CollectionUtil.isEmpty(accountList)) {
            return ResultUtil.data(CollectionUtil.newArrayList());
        }
        return ResultUtil.data(accountList);
    }

}
