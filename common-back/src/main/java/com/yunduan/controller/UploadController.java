package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.common.utils.SecurityUtil;
import com.yunduan.service.AdminAccountService;
import com.yunduan.utils.Base64DecodeMultipartFile;
import com.yunduan.utils.QNiuUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(tags = {"文件上传接口"})
@RequestMapping("/upload")
public class UploadController {

    private static final transient Logger log = LoggerFactory.getLogger(UploadController.class);


    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ApiOperation(value = "文件上传")
    public Result<String> upload(@RequestParam(required = false) MultipartFile file, @RequestParam(required = false) String base64) {
        if (StrUtil.isNotBlank(base64)) {
            // base64上传
            file = Base64DecodeMultipartFile.base64Convert(base64);
        }
        String httpPath = QNiuUtil.uploadMultipartFile(file);
        return ResultUtil.data(httpPath);
    }


}
