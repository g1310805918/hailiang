package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.SysDictionary;
import com.yunduan.service.SysDictionaryService;
import com.yunduan.vo.DicInitListV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dictionary")
@Api(tags = {"数据字典信息接口"})
public class DictionaryController {

    private static final transient Logger log = LoggerFactory.getLogger(DictionaryController.class);

    @Autowired
    private SysDictionaryService sysDictionaryService;


    @GetMapping("/get-init-list")
    @ApiOperation(httpMethod = "GET",value = "问题数据字典初始化")
    public Result<List<DicInitListV>> getInitList() {
        List<DicInitListV> voList = sysDictionaryService.queryDictionaryInit();
        return ResultUtil.data(voList);
    }


    @PostMapping("/get-code-name-list")
    @ApiOperation(httpMethod = "POST",value = "获取标签名下的数据列表")
    public Result<List<SysDictionary>> getCodeNameList(String codeName) {
        if (StrUtil.hasEmpty(codeName)) {
            log.error("获取标签名下的数据列表【codeName】为空");
            return ResultUtil.error("非法请求");
        }
        List<SysDictionary> sysDictionaryList = sysDictionaryService.list(new QueryWrapper<SysDictionary>().eq("code_name", codeName));
        return ResultUtil.data(sysDictionaryList);
    }


    @PostMapping("/add-one")
    @ApiOperation(httpMethod = "POST",value = "添加属性值")
    public Result<String> addOne(String codeName,String content) {
        if (StrUtil.hasEmpty(codeName) || StrUtil.hasEmpty(content)) {
            log.error("添加属性值【codeName、content】为空");
            return ResultUtil.error("非法请求");
        }
        int row = sysDictionaryService.createSysDictionary(codeName, content);
        return row > 0 ? ResultUtil.success("添加成功") : ResultUtil.error("添加失败");
    }

    @PostMapping("/submit-edit-code")
    @ApiOperation(httpMethod = "POST",value = "编辑属性提交修改")
    public Result<String> submitEditCode(String id,String content) {
        if (StrUtil.hasEmpty(id) || StrUtil.hasEmpty(content)) {
            log.error("编辑属性提交修改【id、content】为空");
            return ResultUtil.error("非法请求");
        }
        boolean flag = false;
        SysDictionary sysDictionary = sysDictionaryService.getById(id);
        if (sysDictionary != null) {
            sysDictionary.setContent(content);
            flag = sysDictionaryService.updateById(sysDictionary);
        }
        return flag ? ResultUtil.success("修改成功") : ResultUtil.error("修改失败");
    }



}
