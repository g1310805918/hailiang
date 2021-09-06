package com.yunduan.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.CollectionEngineerDocument;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.entity.Engineer;
import com.yunduan.request.front.center.*;
import com.yunduan.service.CollectionEngineerDocumentService;
import com.yunduan.service.CollectionFavoritesService;
import com.yunduan.service.EngineerService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.RedisUtil;
import com.yunduan.utils.ResultUtil;
import com.yunduan.vo.ChangeUsernameReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/engineer/center")
@Api(tags = {"工程师个人中心接口"})
public class EngineerCenterController {

    private static final transient Logger log = LoggerFactory.getLogger(EngineerCenterController.class);

    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private CollectionEngineerDocumentService collectionEngineerDocumentService;
    @Autowired
    private CollectionFavoritesService collectionFavoritesService;



    @GetMapping("/base-info")
    @ApiOperation(httpMethod = "GET",value = "工程师获取基本数据【基本信息、收藏夹信息】")
    public ResultUtil<Engineer> baseInfo() {
        Engineer engineer = engineerService.engineerBaseInfo(ContextUtil.getUserId().toString());
        return engineer == null ? resultUtil.AesFAILError("非法请求") : resultUtil.AesJSONSuccess("SUCCESS",engineer);
    }


    @GetMapping("/favorites-page-init")
    @ApiOperation(httpMethod = "GET",value = "工程师收藏夹下的知识文档列表")
    public ResultUtil<Map<String, Object>> favoritesPageInit(FavoritesReq favoritesReq) {
        favoritesReq = AESUtil.decryptToObj(favoritesReq.getData(),FavoritesReq.class);
        Map<String, Object> map = collectionEngineerDocumentService.queryFavoritesInitPage(favoritesReq);
        return resultUtil.AesJSONSuccess("SUCCESS",map);
    }


    @GetMapping("/get-dialog-favorites")
    @ApiOperation(httpMethod = "GET",value = "获取工程师可操作收藏夹列表")
    public ResultUtil<List<CollectionFavorites>> getDialogFavorites() {
        Long userId = ContextUtil.getUserId();  //当前用户id
        //用户收藏夹列表
        List<CollectionFavorites> favorites = collectionFavoritesService.list(new QueryWrapper<CollectionFavorites>().eq("account_id", userId));
        if (favorites.size() > 0 && favorites != null) {
            for (CollectionFavorites favorite : favorites) {
                favorite.setStrId(favorite.getId().toString());
            }
        }
        return resultUtil.AesJSONSuccess("SUCCESS",favorites);
    }


    @PostMapping("/remove-favorite/{favoriteId}")
    @ApiOperation(httpMethod = "POST",value = "工程师删除收藏夹")
    public ResultUtil<String> removeFavorite(@PathVariable String favoriteId) {
        if (StrUtil.hasEmpty(favoriteId)) {
            log.error("工程师删除收藏夹【favoriteId】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        //删除工程师收藏夹下收藏的文档
        boolean flag1 = collectionEngineerDocumentService.remove(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", ContextUtil.getUserId()).eq("favorites_id", favoriteId));
        //删除工程师收藏夹
        boolean flag2 = collectionFavoritesService.removeById(favoriteId);
        return flag1 && flag2 ? resultUtil.AesJSONSuccess("删除成功","") : resultUtil.AesFAILError("删除失败");
    }


    @PostMapping("/create-favorite")
    @ApiOperation(httpMethod = "POST",value = "工程师添加收藏夹")
    public ResultUtil<String> createFavorite(AddFavoritesReq addFavoritesReq) {
        addFavoritesReq = AESUtil.decryptToObj(addFavoritesReq.getData(),AddFavoritesReq.class);
        int row = collectionFavoritesService.createFavorite(ContextUtil.getUserId(), addFavoritesReq);
        return row > 0 ? resultUtil.AesJSONSuccess("添加成功","") : resultUtil.AesFAILError("添加失败");
    }


    @PostMapping("/change-favorites-name")
    @ApiOperation(httpMethod = "POST",value = "工程师更改收藏夹名称")
    public ResultUtil<String> changeFavoritesName(ChangeFavoritesNameReq changeFavoritesNameReq) {
        changeFavoritesNameReq = AESUtil.decryptToObj(changeFavoritesNameReq.getData(),ChangeFavoritesNameReq.class);
        int row = collectionFavoritesService.changeFavoritesName(changeFavoritesNameReq.getId(), changeFavoritesNameReq.getFavoritesName());
        return row > 0 ? resultUtil.AesJSONSuccess("修改成功","") : resultUtil.AesFAILError("修改失败");
    }


    @PostMapping("/change-head-pic")
    @ApiOperation(httpMethod = "POST",value = "工程师更换头像")
    public ResultUtil<String> changeHeadPic(ChangeHeadPicReq changeHeadPicReq) {
        changeHeadPicReq = AESUtil.decryptToObj(changeHeadPicReq.getData(),ChangeHeadPicReq.class);
        int row = engineerService.changeHeadPic(ContextUtil.getUserId(), changeHeadPicReq.getHeadPic());
        return row > 0 ? resultUtil.AesJSONSuccess("上传成功","") : resultUtil.AesFAILError("上传失败");
    }


    @PostMapping("/change-username")
    @ApiOperation(httpMethod = "POST",value = "工程师编辑用户名")
    public ResultUtil<String> changeUsername(ChangeUsernameReq changeUsernameReq) {
        changeUsernameReq = AESUtil.decryptToObj(changeUsernameReq.getData(),ChangeUsernameReq.class);
        int row = engineerService.changeUsername(ContextUtil.getUserId().toString(), changeUsernameReq.getUsername());
        return row > 0 ? resultUtil.AesJSONSuccess("修改成功","") : resultUtil.AesFAILError("修改失败");
    }


    @PostMapping("/change-mobile")
    @ApiOperation(httpMethod = "POST",value = "工程师手机换绑-验证原手机号和验证码")
    public ResultUtil<String> changeMobile(ChangeMobileReq changeMobileReq) {
        changeMobileReq = AESUtil.decryptToObj(changeMobileReq.getData(),ChangeMobileReq.class);
        //验证结果
        Boolean flag = redisUtil.checkMobileAuthCode(changeMobileReq.getMobile(), changeMobileReq.getYzm());
        return flag ? resultUtil.AesJSONSuccess("SUCCESS","") : resultUtil.AesFAILError("验证码错误，请重试！");
    }


    @PostMapping("/change-new-mobile")
    @ApiOperation(httpMethod = "POST",value = "工程师立即换绑手机号")
    public ResultUtil<String> changeNewMobile(ChangeMobileReq changeMobileReq) {
        changeMobileReq = AESUtil.decryptToObj(changeMobileReq.getData(),ChangeMobileReq.class);
        //验证结果
        Boolean flag = redisUtil.checkMobileAuthCode(changeMobileReq.getMobile(), changeMobileReq.getYzm());
        if (!flag) {
            return resultUtil.AesFAILError("验证码错误，请重试！");
        }
        int row = engineerService.changeMobile(ContextUtil.getUserId().toString(), changeMobileReq.getMobile());
        return row > 0 ? resultUtil.AesJSONSuccess("换绑成功","") : resultUtil.AesFAILError("换绑失败");
    }


    @PostMapping("/change-password")
    @ApiOperation(httpMethod = "POST",value = "工程师通过邮箱修改密码")
    public ResultUtil<String> changePassword(ChangePasswordReq changePasswordReq) {
        changePasswordReq = AESUtil.decryptToObj(changePasswordReq.getData(),ChangePasswordReq.class);
        Engineer one = engineerService.getOne(new QueryWrapper<Engineer>().eq("email", changePasswordReq.getEmail()));
        if (one == null) {
            log.error("工程师通过邮箱修改密码【Engineer】不存在");
            return resultUtil.AesFAILError("账号不存在");
        }
        String pass = changePasswordReq.getPassword().replaceAll(" ", "");
        one.setPassword(StrUtil.hasEmpty(pass) ? one.getPassword() : pass);
        boolean flag = engineerService.updateById(one);
        return flag ? resultUtil.AesJSONSuccess("修改成功","") : resultUtil.AesFAILError("修改失败");
    }





}
