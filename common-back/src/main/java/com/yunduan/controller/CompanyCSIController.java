package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.CompanyCSI;
import com.yunduan.request.back.CompanyCSIInit;
import com.yunduan.service.CompanyCSIService;
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
@RequestMapping("/company")
@Api(tags = {"客户服务号CSI接口"})
public class CompanyCSIController {

    private static final transient Logger log = LoggerFactory.getLogger(CompanyCSIController.class);

    @Autowired
    private CompanyCSIService companyCSIService;


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


}
