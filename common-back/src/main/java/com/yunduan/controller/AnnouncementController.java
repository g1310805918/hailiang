package com.yunduan.controller;

import cn.hutool.core.util.StrUtil;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.entity.SysMessage;
import com.yunduan.service.AsyncTasks;
import com.yunduan.service.SysMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/announcement")
@Api(tags = {"公告管理接口"})
public class AnnouncementController {
    private static final transient Logger log = LoggerFactory.getLogger(AnnouncementController.class);

    @Autowired
    private SysMessageService sysMessageService;
    @Autowired
    private AsyncTasks asyncTasks;


    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "公告列表初始化")
    public Result<Map<String, Object>> init(String title, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            log.error("公告列表初始化【pageNo、pageSize】");
            return ResultUtil.error("非法请求");
        }
        Map<String, Object> map = sysMessageService.initPageData(title, pageNo, pageSize);
        return ResultUtil.data(map);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "发布系统公告")
    public Result<String> add(String inputTitle, String content) {
        if (StrUtil.hasEmpty(inputTitle) || StrUtil.hasEmpty(content)) {
            return ResultUtil.error("非法请求");
        }
        //系统消息
        SysMessage message = sysMessageService.createSysMessage(inputTitle, content);
        if (message != null) {
            //向用户发送系统消息
            asyncTasks.doSaveSysMessage(message);
            return ResultUtil.success("发布成功");
        }
        return ResultUtil.error("发布失败");
    }


    @RequestMapping(value = "/get-one/{id}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "获取某条公告详情")
    public Result<SysMessage> getOne(@PathVariable String id) {
        if (StrUtil.hasEmpty(id)) {
            return ResultUtil.error("非法请求");
        }
        SysMessage message = sysMessageService.getById(id);
        return ResultUtil.data(message);
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "修改公告内容")
    public Result<String> edit(String id, String title, String content) {
        if (StrUtil.hasEmpty(id) || StrUtil.hasEmpty(title) || StrUtil.hasEmpty(content)) {
            return ResultUtil.error("非法请求");
        }
        String oldTitle = "";
        SysMessage sysMessage = sysMessageService.getById(id);
        if (sysMessage != null) {
            oldTitle = sysMessage.getTitle();
            sysMessage.setTitle(title).setContent(content);
            boolean flag = sysMessageService.updateById(sysMessage);
            if (flag) {
                //修改用户下的系统消息
                asyncTasks.doEditAccountSysMessage(oldTitle,sysMessage);
            }
            return ResultUtil.success("修改成功");
        }
        return ResultUtil.error("修改失败");
    }


    @RequestMapping(value = "/remove/{batchId}",method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "批量删除")
    public Result<String> remove(@PathVariable String batchId) {
        if (StrUtil.hasEmpty(batchId)) {
            return ResultUtil.error("非法请求");
        }
        List<String> idList = Arrays.asList(batchId.split(","));
        boolean flag = sysMessageService.removeByIds(idList);
        return flag ? ResultUtil.success("删除成功") : ResultUtil.error("删除失败");
    }

}
