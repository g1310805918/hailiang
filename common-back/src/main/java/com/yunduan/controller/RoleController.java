package com.yunduan.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.common.utils.SecurityUtil;
import com.yunduan.entity.AdminAccount;
import com.yunduan.entity.AdminRole;
import com.yunduan.entity.Role;
import com.yunduan.entity.RolePermission;
import com.yunduan.service.AdminRoleService;
import com.yunduan.service.RolePermissionService;
import com.yunduan.service.RoleService;
import com.yunduan.utils.PageUtil;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.back.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Victor
 */
@RestController
@Api(tags = {"角色管理接口"})
@RequestMapping("/role")
public class RoleController {

    private static final transient Logger log = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SecurityUtil securityUtil;



    @RequestMapping(value = "/getAllList", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部角色")
    public Result<List<Role>> roleGetAll() {
        List<Role> list = roleService.list();
        return new ResultUtil<List<Role>>().setData(list);
    }

    @RequestMapping(value = "/getAllByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取角色")
    public Result<Page<Role>> getRoleByPage(@ModelAttribute PageVo page) {

        Page<Role> list = roleService.findAll(PageUtil.initPage(page));
        for (Role role : list.getContent()) {
            // 角色拥有权限
            List<RolePermission> permissions = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("role_id",role.getId()));
            role.setPermissions(permissions);
        }
        return new ResultUtil<Page<Role>>().setData(list);
    }

    @RequestMapping(value = "/setDefault", method = RequestMethod.POST)
    @ApiOperation(value = "设置或取消默认角色")
    public Result<Object> setDefault(@RequestParam String id,
                                     @RequestParam Boolean isDefault) {

        Role role = roleService.getById(id);
        if (role == null) {
            return new ResultUtil<Object>().setErrorMsg("角色不存在");
        }
        role.setDefaultRole(isDefault);
        roleService.updateById(role);
        return new ResultUtil<Object>().setSuccessMsg("设置成功");
    }

    @RequestMapping(value = "/editRolePerm", method = RequestMethod.POST)
    @ApiOperation(value = "编辑角色分配菜单权限")
    public Result<Object> editRolePerm(@RequestParam String roleId,
                                       @RequestParam(required = false) String[] permIds) {

        AdminAccount currUser = securityUtil.getCurrUser();

        //删除其关联权限
        rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id",roleId));
        //分配新权限
        for (String permId : permIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setCreateBy(currUser.getId()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setUpdateTime(DateUtil.now()).setUpdateBy(currUser.getId());
            rolePermission.setPermissionId(permId);
            rolePermissionService.save(rolePermission);
        }
        //手动批量删除缓存
        Set<String> keysUser = redisTemplate.keys("user:" + "*");
        redisTemplate.delete(keysUser);
        Set<String> keysUserRole = redisTemplate.keys("userRole:" + "*");
        redisTemplate.delete(keysUserRole);
        Set<String> keysUserPerm = redisTemplate.keys("userPermission:" + "*");
        redisTemplate.delete(keysUserPerm);
        Set<String> keysUserMenu = redisTemplate.keys("permission::userMenuList:*");
        redisTemplate.delete(keysUserMenu);
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/editRoleDep", method = RequestMethod.POST)
    @ApiOperation(value = "编辑角色分配数据权限")
    public Result<Object> editRoleDep(@RequestParam String roleId,
                                      @RequestParam Integer dataType,
                                      @RequestParam(required = false) String[] depIds) {

        Role r = roleService.getById(roleId);
        r.setDataType(dataType);
        roleService.updateById(r);
        Set<String> keysUserRole = redisTemplate.keys("userRole:" + "*");
        redisTemplate.delete(keysUserRole);

        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ApiOperation(value = "保存数据")
    public Result<Role> save(@ModelAttribute Role role) {
        AdminAccount currUser = securityUtil.getCurrUser();
        role.setId(SnowFlakeUtil.getPrimaryKeyId().toString()).setCreateBy(currUser.getId()).setCreateTime(DateUtil.now()).setUpdateBy(currUser.getId()).setUpdateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG);
        Role r = roleService.createRole(role);
        return new ResultUtil<Role>().setData(r);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "更新数据")
    public Result<Role> edit(@ModelAttribute Role entity) {
        //更新角色
        Role r = roleService.createRole(entity);
        //手动批量删除缓存
        Set<String> keysUser = redisTemplate.keys("user:" + "*");
        redisTemplate.delete(keysUser);
        Set<String> keysUserRole = redisTemplate.keys("userRole:" + "*");
        redisTemplate.delete(keysUserRole);
        return new ResultUtil<Role>().setData(r);
    }

    @RequestMapping(value = "/delAllByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过ids删除")
    public Result<Object> delByIds(@PathVariable String[] ids) {

        for (String id : ids) {
            List<AdminRole> list = adminRoleService.list(new QueryWrapper<AdminRole>().eq("role_id",id));
            if (list != null && list.size() > 0) {
                return new ResultUtil<Object>().setErrorMsg("删除失败，包含正被用户使用关联的角色");
            }
        }
        for (String id : ids) {
            roleService.removeById(id);
            //删除关联菜单权限
            rolePermissionService.remove(new QueryWrapper<RolePermission>().eq("role_id",id));
        }
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }


}
