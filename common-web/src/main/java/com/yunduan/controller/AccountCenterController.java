package com.yunduan.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Account;
import com.yunduan.entity.CollectionAccountDocument;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.request.front.center.*;
import com.yunduan.service.AccountService;
import com.yunduan.service.BindingAccountCSIService;
import com.yunduan.service.CollectionAccountDocumentService;
import com.yunduan.service.CollectionFavoritesService;
import com.yunduan.utils.*;
import com.yunduan.vo.AccountBindingCSI;
import com.yunduan.vo.BindingOtherCSIAccountVo;
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
@RequestMapping("/api/center")
@Api(tags = {"账号中心接口"})
public class AccountCenterController {
    //日志门面
    private static final transient Logger log = LoggerFactory.getLogger(KnowledgeController.class);
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CollectionFavoritesService collectionFavoritesService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private BindingAccountCSIService bindingAccountCSIService;
    @Autowired
    private CollectionAccountDocumentService collectionAccountDocumentService;


    @GetMapping("/account-base-info")
    @ApiOperation(httpMethod = "GET", value = "账号中心基本数据")
    public ResultUtil<Account> accountBaseInfo() {
        Account account = accountService.queryAccountBaseInfo();
        return resultUtil.AesJSONSuccess("SUCCESS", account);
    }


    @GetMapping("/account-favorites")
    @ApiOperation(httpMethod = "GET", value = "用户收藏夹信息")
    public ResultUtil<Map<String, Object>> accountFavorites(FavoritesReq favoritesReq) {
        favoritesReq = AESUtil.decryptToObj(favoritesReq.getData(), FavoritesReq.class);
        if (favoritesReq.getPageNo() == null || favoritesReq.getPageSize() == null) {
            log.error("用户收藏夹信息【pageNo、pageSize】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        Map<String, Object> map = collectionFavoritesService.queryAccountFavoritesVos(favoritesReq);
        return resultUtil.AesJSONSuccess("SUCCESS", map);
    }


    @PostMapping("/unbind-csi/{bindingId}")
    @ApiOperation(httpMethod = "POST", value = "解除绑定CSI号")
    public ResultUtil<String> unbindCSI(@PathVariable("bindingId") String bindingId) {
        if (StrUtil.hasEmpty(bindingId)) {
            log.error("解除绑定CSI号 【bindingId】 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = accountService.unBindingCSI(bindingId);
        return row > 0 ? resultUtil.AesJSONSuccess("解绑成功", "") : resultUtil.AesFAILError("解绑失败");
    }


    @PostMapping("/add-binding-csi")
    @ApiOperation(httpMethod = "POST", value = "新增绑定CSI号")
    public ResultUtil<String> addBindingCSI(AddBindingCSIReq addBindingCSIReq) {
        addBindingCSIReq = AESUtil.decryptToObj(addBindingCSIReq.getData(), AddBindingCSIReq.class);
        int row = bindingAccountCSIService.addBindingCSI(ContextUtil.getUserId().toString(), addBindingCSIReq.getCsiNumber());
        if (row == StatusCodeUtil.NOT_FOUND_FLAG) {
            return resultUtil.AesFAILError("CSI编号不存在");
        } else if (row == StatusCodeUtil.COMPANY_CSI_CAU_NO_BINDING) {
            return resultUtil.AesFAILError("公司CAU管理员未绑定，请联系负责人绑定。");
        }
        return row > 0 ? resultUtil.AesJSONSuccess("绑定申请提交成功，等待管理员审核。", "") : resultUtil.AesFAILError("绑定申请提交失败");
    }


    @GetMapping("/binding-csi-detail-info/{bindingId}")
    @ApiOperation(httpMethod = "GET", value = "绑定CSI详情")
    public ResultUtil<AccountBindingCSI> bindingCSIDetailInfo(@PathVariable("bindingId") String bindingId) {
        if (StrUtil.hasEmpty(bindingId)) {
            log.error("绑定CSI详情 bindingId 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        //详情
        AccountBindingCSI bindingCSI = bindingAccountCSIService.queryCSIBindingPersonInfoList(bindingId);
        if (bindingCSI == null) {
            log.error("绑定CSI详情【bindingCSI】为空");
            return resultUtil.AesFAILError("非法请求");
        }
        return resultUtil.AesJSONSuccess("SUCCESS", bindingCSI);
    }


    @PostMapping("/cau-operation-binding-account/{bindingId}/{type}")
    @ApiOperation(httpMethod = "POST", value = "CAU操作绑定用户【bindingId绑定id、type类型1同意、2拒绝、3删除】")
    public ResultUtil<String> cauOperationBindingAccount(@PathVariable("bindingId") String bindingId, @PathVariable("type") String type) {
        if (StrUtil.hasEmpty(bindingId) || StrUtil.hasEmpty(type)) {
            log.error("CAU删除绑定用户 bindingId、type 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        //删除绑定记录
        int row = bindingAccountCSIService.operationBindingAccountRecord(bindingId, type);
        return row > 0 ? resultUtil.AesJSONSuccess("操作成功", "") : resultUtil.AesFAILError("操作失败");
    }


    @GetMapping("/distribution-others-binding-csi-account-list/{bindingId}")
    @ApiOperation(httpMethod = "GET", value = "分配CAU给其他人--->用户列表")
    public ResultUtil<List<BindingOtherCSIAccountVo>> otherCSIAccount(@PathVariable("bindingId") String bindingId) {
        if (StrUtil.hasEmpty(bindingId)) {
            log.error("分配CAU给其他人的用户列表 bindingId 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        List<BindingOtherCSIAccountVo> voList = bindingAccountCSIService.queryOtherCSIAccountList(bindingId);
        return resultUtil.AesJSONSuccess("SUCCESS", voList);
    }


    @PostMapping("/distribution-others/{bindingId}/{accountId}")
    @ApiOperation(httpMethod = "POST", value = "CAU确认分配给他人")
    public ResultUtil<String> distributionOthers(@PathVariable("bindingId") String bindingId, @PathVariable("accountId") String accountId) {
        if (StrUtil.hasEmpty(accountId) || StrUtil.hasEmpty(bindingId)) {
            log.error("CAU确认分配给他人 accountId、bindingId 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        int row = bindingAccountCSIService.distributionCAUToOtherAccount(bindingId, accountId);
        return row > 0 ? resultUtil.AesJSONSuccess("分配成功", "") : resultUtil.AesFAILError("分配失败");
    }


    @PostMapping("/change-username")
    @ApiOperation(httpMethod = "POST", value = "编辑用户名")
    public ResultUtil<String> changeUsername(ChangeUsernameReq changeUsernameReq) {
        changeUsernameReq = AESUtil.decryptToObj(changeUsernameReq.getData(), ChangeUsernameReq.class);
        int row = accountService.changeUsername(ContextUtil.getUserId().toString(), changeUsernameReq.getUsername());
        return row > 0 ? resultUtil.AesJSONSuccess("修改成功", "") : resultUtil.AesFAILError("修改失败");
    }


    @PostMapping("/change-mobile")
    @ApiOperation(httpMethod = "POST", value = "手机换绑-验证原手机号和验证码")
    public ResultUtil<String> changeMobile(ChangeMobileReq changeMobileReq) {
        changeMobileReq = AESUtil.decryptToObj(changeMobileReq.getData(), ChangeMobileReq.class);
        //验证结果
        Boolean flag = redisUtil.checkMobileAuthCode(changeMobileReq.getMobile(), changeMobileReq.getYzm());
        return flag ? resultUtil.AesJSONSuccess("SUCCESS", "") : resultUtil.AesFAILError("验证码错误，请重试！");
    }


    @PostMapping("/change-new-mobile")
    @ApiOperation(httpMethod = "POST", value = "手机换绑-立即绑定新手机号")
    public ResultUtil<String> changeNewMobile(ChangeMobileNow changeMobileNow) {
        changeMobileNow = AESUtil.decryptToObj(changeMobileNow.getData(), ChangeMobileNow.class);
        //验证结果
        Boolean flag = redisUtil.checkMobileAuthCode(changeMobileNow.getNewMobile(), changeMobileNow.getYzm());
        if (!flag) {
            return resultUtil.AesFAILError("验证码错误，请重试！");
        }
        int row = accountService.changeMobile(ContextUtil.getUserId().toString(), changeMobileNow.getOldMobile(), changeMobileNow.getNewMobile());
        return row > 0 ? resultUtil.AesJSONSuccess("换绑成功", "") : resultUtil.AesFAILError("换绑失败");
    }


    @PostMapping("/change-head-pic")
    @ApiOperation(httpMethod = "POST", value = "更换头像")
    public ResultUtil<String> changeHeadPic(ChangeHeadPicReq changeHeadPicReq) {
        changeHeadPicReq = AESUtil.decryptToObj(changeHeadPicReq.getData(), ChangeHeadPicReq.class);
        int row = accountService.changeHeadPic(ContextUtil.getUserId().toString(), changeHeadPicReq.getHeadPic());
        return row > 0 ? resultUtil.AesJSONSuccess("上传成功", "") : resultUtil.AesFAILError("上传失败");
    }


    @GetMapping("/get-account-favorites")
    @ApiOperation(httpMethod = "GET", value = "获取用户可操作的收藏夹列表")
    public ResultUtil<List<CollectionFavorites>> getAccountFavorites() {
        //用户收藏夹列表
        List<CollectionFavorites> favorites = collectionFavoritesService.list(new QueryWrapper<CollectionFavorites>().eq("account_id", ContextUtil.getUserId()));
        if (favorites.size() > 0 && favorites != null) {
            for (CollectionFavorites favorite : favorites) {
                favorite.setStrId(favorite.getId().toString());
            }
        }
        return resultUtil.AesJSONSuccess("SUCCESS", favorites);
    }


    @PostMapping("/add-favorites")
    @ApiOperation(httpMethod = "POST", value = "添加收藏夹")
    public ResultUtil<String> addFavorites(AddFavoritesReq addFavoritesReq) {
        addFavoritesReq = AESUtil.decryptToObj(addFavoritesReq.getData(), AddFavoritesReq.class);
        CollectionFavorites favorites = new CollectionFavorites().setId(SnowFlakeUtil.getPrimaryKeyId()).setAccountId(ContextUtil.getUserId()).setFavoritesName(addFavoritesReq.getFavoritesName()).setCreateTime(DateUtil.now());
        boolean flag = collectionFavoritesService.save(favorites);
        return flag ? resultUtil.AesJSONSuccess("添加成功", "") : resultUtil.AesFAILError("添加失败");
    }


    @PostMapping("/remove-favorites/{id}")
    @ApiOperation(httpMethod = "POST", value = "删除收藏夹")
    public ResultUtil<String> removeFavorites(@PathVariable("id") String id) {
        if (StrUtil.hasEmpty(id)) {
            log.error("删除收藏夹 id 为空");
            return resultUtil.AesFAILError("非法请求");
        }
        //删除收藏夹下的收藏知识文档
        collectionAccountDocumentService.remove(new QueryWrapper<CollectionAccountDocument>().eq("account_id", ContextUtil.getUserId()).eq("favorites_id", id));
        //删除收藏夹
        collectionFavoritesService.removeById(id);
        return resultUtil.AesJSONSuccess("删除成功", "");
    }


    @PostMapping("/change-favorites-name")
    @ApiOperation(httpMethod = "POST", value = "更改收藏夹名称")
    public ResultUtil<String> changeFavoritesName(ChangeFavoritesNameReq changeFavoritesNameReq) {
        changeFavoritesNameReq = AESUtil.decryptToObj(changeFavoritesNameReq.getData(), ChangeFavoritesNameReq.class);
        int row = collectionFavoritesService.changeFavoritesName(changeFavoritesNameReq.getId(), changeFavoritesNameReq.getFavoritesName());
        return row > 0 ? resultUtil.AesJSONSuccess("修改成功", "") : resultUtil.AesFAILError("修改失败");
    }


}
