package com.yunduan.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunduan.common.constant.CommonConstant;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.common.utils.SecurityUtil;
import com.yunduan.config.security.permission.MySecurityMetadataSource;
import com.yunduan.entity.AdminAccount;
import com.yunduan.entity.Permission;
import com.yunduan.entity.RolePermission;
import com.yunduan.service.PermissionService;
import com.yunduan.service.RolePermissionService;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.back.MenuVo;
import com.yunduan.vo.back.VoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Victor
 */
@RestController
@Api(tags = {"菜单/权限管理接口"})
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private MySecurityMetadataSource mySecurityMetadataSource;

    @RequestMapping(value = "/getMenuList", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户页面菜单数据")
    public Result<List<MenuVo>> getAllMenuList() {

        List<MenuVo> menuList = new ArrayList<>();
        // 读取缓存
        AdminAccount u = securityUtil.getCurrUser();
        String key = "permission::userMenuList:" + u.getId();
        String v = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(v)) {
            menuList = new Gson().fromJson(v, new TypeToken<List<MenuVo>>() {
            }.getType());
            return new ResultUtil<List<MenuVo>>().setData(menuList);
        }

        // 用户所有权限 已排序去重
        List<Permission> list = permissionService.findByUserId(u.getId());

        // 筛选0级页面
        for (Permission p : list) {
            if (CommonConstant.PERMISSION_NAV.equals(p.getType()) && CommonConstant.LEVEL_ZERO.equals(p.getLevel())) {
                menuList.add(VoUtil.permissionToMenuVo(p));
            }
        }
        // 筛选一级页面
        List<MenuVo> firstMenuList = new ArrayList<>();
        for (Permission p : list) {
            if (CommonConstant.PERMISSION_PAGE.equals(p.getType()) && CommonConstant.LEVEL_ONE.equals(p.getLevel())) {
                firstMenuList.add(VoUtil.permissionToMenuVo(p));
            }
        }
        // 筛选二级页面
        List<MenuVo> secondMenuList = new ArrayList<>();
        for (Permission p : list) {
            if (CommonConstant.PERMISSION_PAGE.equals(p.getType()) && CommonConstant.LEVEL_TWO.equals(p.getLevel())) {
                secondMenuList.add(VoUtil.permissionToMenuVo(p));
            }
        }
        // 筛选二级页面拥有的按钮权限
        List<MenuVo> buttonPermissions = new ArrayList<>();
        for (Permission p : list) {
            if (CommonConstant.PERMISSION_OPERATION.equals(p.getType()) && CommonConstant.LEVEL_THREE.equals(p.getLevel())) {
                buttonPermissions.add(VoUtil.permissionToMenuVo(p));
            }
        }

        // 匹配二级页面拥有权限
        for (MenuVo m : secondMenuList) {
            List<String> permTypes = new ArrayList<>();
            for (MenuVo me : buttonPermissions) {
                if (m.getId().equals(me.getParentId())) {
                    permTypes.add(me.getButtonType());
                }
            }
            m.setPermTypes(permTypes);
        }
        // 匹配一级页面拥有二级页面
        for (MenuVo m : firstMenuList) {
            List<MenuVo> secondMenu = new ArrayList<>();
            for (MenuVo me : secondMenuList) {
                if (m.getId().equals(me.getParentId())) {
                    secondMenu.add(me);
                }
            }
            m.setChildren(secondMenu);
        }
        // 匹配0级页面拥有一级页面
        for (MenuVo m : menuList) {
            List<MenuVo> firstMenu = new ArrayList<>();
            for (MenuVo me : firstMenuList) {
                if (m.getId().equals(me.getParentId())) {
                    firstMenu.add(me);
                }
            }
            m.setChildren(firstMenu);
        }

        // 缓存
        redisTemplate.opsForValue().set(key, new Gson().toJson(menuList));
        return new ResultUtil<List<MenuVo>>().setData(menuList);
    }

    @RequestMapping(value = "/getAllList", method = RequestMethod.GET)
    @ApiOperation(value = "获取权限菜单树")
    public Result<List<Permission>> getAllList() {

        // 0级
        List<Permission> list0 = permissionService.findByLevelOrderBySortOrder(CommonConstant.LEVEL_ZERO);
        for (Permission p0 : list0) {
            // 一级
            List<Permission> list1 = permissionService.findByParentIdOrderBySortOrder(p0.getId());
            p0.setChildren(list1);
            // 二级
            for (Permission p1 : list1) {
                List<Permission> children1 = permissionService.findByParentIdOrderBySortOrder(p1.getId());
                p1.setChildren(children1);
                // 三级
                for (Permission p2 : children1) {
                    List<Permission> children2 = permissionService.findByParentIdOrderBySortOrder(p2.getId());
                    p2.setChildren(children2);
                }
            }
        }
        return new ResultUtil<List<Permission>>().setData(list0);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public Result<Permission> add(@ModelAttribute Permission permission) {
        //当前用户
        AdminAccount currUser = securityUtil.getCurrUser();

        // 判断拦截请求的操作权限按钮名是否已存在
        if (CommonConstant.PERMISSION_OPERATION.equals(permission.getType())) {
            List<Permission> list = permissionService.findByTitle(permission.getTitle());
            if (list != null && list.size() > 0) {
                return new ResultUtil<Permission>().setErrorMsg("名称已存在");
            }
        }
        //设置id、删除标志、创建人、创建时间
        permission.setId(SnowFlakeUtil.getPrimaryKeyId().toString()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setCreateBy(currUser.getId()).setCreateTime(DateUtil.now());
        Permission u = permissionService.createPermission(permission);
        //重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        //手动删除缓存
        redisTemplate.delete("permission::allList");
        return new ResultUtil<Permission>().setData(u);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public Result<Permission> edit(@ModelAttribute Permission permission) {

        // 判断拦截请求的操作权限按钮名是否已存在
        if (CommonConstant.PERMISSION_OPERATION.equals(permission.getType())) {
            // 若名称修改
            Permission p = permissionService.getById(permission.getId());
            if (!p.getTitle().equals(permission.getTitle())) {
                List<Permission> list = permissionService.findByTitle(permission.getTitle());
                if (list != null && list.size() > 0) {
                    return new ResultUtil<Permission>().setErrorMsg("名称已存在");
                }
            }
        }
        //设置未删除标志、设置更新人、更新时间
        permission.setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setUpdateBy(permission.getUpdateBy()).setUpdateTime(DateUtil.now());
        Permission u = permissionService.createPermission(permission);

        //重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        //手动批量删除缓存
        Set<String> keys = redisTemplate.keys("userPermission:" + "*");
        redisTemplate.delete(keys);
        Set<String> keysUser = redisTemplate.keys("user:" + "*");
        redisTemplate.delete(keysUser);
        Set<String> keysUserMenu = redisTemplate.keys("permission::userMenuList:*");
        redisTemplate.delete(keysUserMenu);
        redisTemplate.delete("permission::allList");
        return new ResultUtil<Permission>().setData(u);
    }


    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过id删除")
    public Result<Object> delByIds(@PathVariable String[] ids) {

        for (String id : ids) {
            List<RolePermission> list = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("permission_id",id));
            if (list != null && list.size() > 0) {
                return new ResultUtil<Object>().setErrorMsg("删除失败，包含正被角色使用关联的菜单或权限");
            }
        }
        permissionService.removeByIds(Arrays.asList(ids));
        //重新加载权限
        mySecurityMetadataSource.loadResourceDefine();
        //手动删除缓存
        redisTemplate.delete("permission::allList");
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "搜索菜单")
    public Result<List<Permission>> searchPermissionList(@RequestParam String title) {

        List<Permission> list = permissionService.findByTitleLikeOrderBySortOrder("%" + title + "%");
        return new ResultUtil<List<Permission>>().setData(list);
    }

}
