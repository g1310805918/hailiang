package com.yunduan.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.common.constant.CommonConstant;
import com.yunduan.common.utils.Result;
import com.yunduan.common.utils.ResultUtil;
import com.yunduan.common.utils.SecurityUtil;
import com.yunduan.common.vo.ExcelImportResult;
import com.yunduan.entity.AdminAccount;
import com.yunduan.entity.AdminRole;
import com.yunduan.entity.Role;
import com.yunduan.service.AdminAccountService;
import com.yunduan.service.AdminRoleService;
import com.yunduan.utils.PageUtil;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.back.PageVo;
import com.yunduan.vo.back.SearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


@RestController
@Api(tags = {"管理员接口"})
@RequestMapping("/user")
public class UserController {

    private static final transient Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private AdminAccountService adminAccountService;
    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/info")
    @ApiOperation(httpMethod = "GET", value = "获取当前登录用户接口")
    public Result<AdminAccount> getUserInfo() {
        AdminAccount user = securityUtil.getCurrUser();
        user.setPassword(null);
        return new ResultUtil<AdminAccount>().setData(user);
    }

    @RequestMapping(value = "/searchByName/{username}", method = RequestMethod.GET)
    @ApiOperation(value = "通过用户名搜索用户")
    public Result<List<AdminAccount>> searchByName(@PathVariable String username) throws UnsupportedEncodingException {

        List<AdminAccount> list = adminAccountService.findByUsernameLikeAndStatus("%" + URLDecoder.decode(username, "utf-8") + "%", CommonConstant.STATUS_NORMAL);
        return new ResultUtil<List<AdminAccount>>().setData(list);
    }


    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public Result<Page<AdminAccount>> getByCondition(@ModelAttribute AdminAccount user,
                                                     @ModelAttribute SearchVo searchVo,
                                                     @ModelAttribute PageVo pageVo) {
        Page<AdminAccount> page = adminAccountService.findByCondition(user, searchVo, PageUtil.initPage(pageVo));
        for (AdminAccount u : page.getContent()) {
            // 关联角色
            List<Role> list = adminRoleService.findByUserId(u.getId());
            u.setRoles(list);
            u.setPassword(null);
        }
        return new ResultUtil<Page<AdminAccount>>().setData(page);
    }


    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    public Result<Object> regist(@ModelAttribute AdminAccount u,
                                 @RequestParam(required = false) String[] roles) {

        AdminAccount currUser = securityUtil.getCurrUser();

        if (StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())) {
            return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
        }

        //用户名获取用户
        AdminAccount username = adminAccountService.findByUsername(u.getUsername());
        if (username != null) {
            return new ResultUtil<Object>().setErrorMsg("该用户名已被注册");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass).setId(SnowFlakeUtil.getPrimaryKeyId().toString()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setCreateBy(currUser.getId()).setUpdateTime(DateUtil.now()).setUpdateBy(currUser.getId());
        boolean flag = adminAccountService.save(u);
        if (!flag) {
            return new ResultUtil<Object>().setErrorMsg("添加失败");
        }
        if (roles != null && roles.length > 0) {
            //添加角色
            for (String roleId : roles) {
                AdminRole ur = new AdminRole();
                ur.setUserId(u.getId());
                ur.setRoleId(roleId);
                adminRoleService.save(ur);
            }
        }
        return new ResultUtil<Object>().setSuccessMsg("添加成功");
    }


    @RequestMapping(value = "/admin/disable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台禁用用户")
    public Result<Object> disable(@ApiParam("用户唯一id标识") @PathVariable String userId) {

        AdminAccount user = adminAccountService.getById(userId);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_LOCK);
        adminAccountService.updateById(user);
        //手动更新缓存
        redisTemplate.delete("user::" + user.getUsername());
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/admin/enable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台启用用户")
    public Result<Object> enable(@ApiParam("用户唯一id标识") @PathVariable String userId) {

        AdminAccount user = adminAccountService.getById(userId);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_NORMAL);
        adminAccountService.updateById(user);
        //手动更新缓存
        redisTemplate.delete("user::" + user.getUsername());
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过ids删除")
    public Result<Object> delAllByIds(@PathVariable String[] ids) {

        for (String id : ids) {
            AdminAccount u = adminAccountService.getById(id);
            //删除相关缓存
            redisTemplate.delete("user::" + u.getUsername());
            redisTemplate.delete("userRole::" + u.getId());
            redisTemplate.delete("userRole::depIds:" + u.getId());
            redisTemplate.delete("permission::userMenuList:" + u.getId());
            adminAccountService.removeById(id);
            //删除关联角色
            adminRoleService.remove(new QueryWrapper<AdminRole>().eq("user_id",id));
        }
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }


    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    @ApiOperation(value = "解锁验证密码")
    public Result<Object> unLock(@RequestParam String password) {
        AdminAccount u = securityUtil.getCurrUser();
        if (!new BCryptPasswordEncoder().matches(password, u.getPassword())) {
            return new ResultUtil<Object>().setErrorMsg("密码不正确");
        }
        return new ResultUtil<Object>().setData(null);
    }


    @RequestMapping(value = "/resetPass", method = RequestMethod.POST)
    @ApiOperation(value = "重置密码")
    public Result<Object> resetPass(@RequestParam String[] ids) {

        for (String id : ids) {
            AdminAccount u = adminAccountService.getById(id);
            // 管理员不能重置
            if ("admin".equals(u.getUsername())) {
                log.error("【重置密码】测试账号及管理员账号不得重置");
                throw new RuntimeException("测试账号及管理员账号不得重置");
            }
            u.setPassword(new BCryptPasswordEncoder().encode("123456"));
            adminAccountService.updateById(u);
            redisTemplate.delete("user::" + u.getUsername());
        }
        return ResultUtil.success("操作成功");
    }



    /**
     * 线上demo不允许测试账号改密码
     *
     * @param password 旧密码
     * @param newPass 新密码
     * @return
     */
    @RequestMapping(value = "/modifyPass", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public Result<Object> modifyPass(@ApiParam("旧密码") @RequestParam String password,
                                     @ApiParam("新密码") @RequestParam String newPass,
                                     @ApiParam("密码强度") @RequestParam String passStrength) {

        AdminAccount user = securityUtil.getCurrUser();

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return new ResultUtil<Object>().setErrorMsg("旧密码不正确");
        }

        String newEncryptPass = new BCryptPasswordEncoder().encode(newPass);
        user.setPassword(newEncryptPass);
        user.setPassStrength(passStrength);
        adminAccountService.updateById(user);
        //手动更新缓存
        redisTemplate.delete("user::" + user.getUsername());
        return new ResultUtil<Object>().setSuccessMsg("修改密码成功");
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部用户数据")
    public Result<List<AdminAccount>> getByCondition() {

        List<AdminAccount> list = adminAccountService.list();
        for (AdminAccount u : list) {
            u.setPassword(null);
        }
        return new ResultUtil<List<AdminAccount>>().setData(list);
    }


    @RequestMapping(value = "/importData", method = RequestMethod.POST)
    @ApiOperation(value = "导入用户数据")
    public Result<Object> importData(@RequestBody List<AdminAccount> users) {

        List<Integer> errors = new ArrayList<>();
        List<ExcelImportResult.Error> errorList = new ArrayList<>();
        int count = 0;
        for (AdminAccount u : users) {
            List<String> errorMsg = CollectionUtil.newArrayList();
            // 验证用户名密码不为空
            if (StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())) {
                errorMsg.add("用户名或密码为空");
            }else if  (adminAccountService.findByUsername(u.getUsername()) != null) {
                // 验证用户名唯一
                errorMsg.add("用户名已存在");
            }

            // 验证昵称不为空
            if (StrUtil.isBlank(u.getNickName())) {
                errorMsg.add("昵称为空");
            }

            // 验证手机不为空
            if (StrUtil.isBlank(u.getMobile())) {
                errorMsg.add("手机为空");
            }else if  (adminAccountService.findByMobile(u.getMobile()) != null) {
                // 验证手机唯一
                errorMsg.add("手机已存在");
            }

            // 验证邮箱不为空
            if (StrUtil.isBlank(u.getEmail())) {
                errorMsg.add("邮箱为空");
            }else if  (adminAccountService.findByEmail(u.getEmail()) != null) {
                // 验证手机唯一
                errorMsg.add("邮箱已存在");
            }
            if (errorMsg.size() > 0) {
                errors.add(count);
                errorList.add(new ExcelImportResult.Error(count, String.join(";", errorMsg)));
            } else {
                // 加密密码、id、创建时间、删除标志
                u.setPassword(new BCryptPasswordEncoder().encode(u.getPassword())).setId(SnowFlakeUtil.getPrimaryKeyId().toString()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG);
                //默认状态
                u.setStatus(CommonConstant.USER_STATUS_NORMAL);
                // 保存数据
                adminAccountService.save(u);
                // 分配默认角色
                if (u.getDefaultRole() != null && u.getDefaultRole() == 1) {
                    List<Role> roleList = adminRoleService.findByDefaultRole(true);
                    if (roleList != null && roleList.size() > 0) {
                        for (Role role : roleList) {
                            AdminRole ur = new AdminRole();
                            ur.setUserId(u.getId());
                            ur.setRoleId(role.getId());
                            adminRoleService.save(ur);
                        }
                    }
                }
            }
            count++;
        }
        ExcelImportResult result = new ExcelImportResult(users.size() - errors.size(), errorList);
        return new ResultUtil<Object>().setData(result);
    }


    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    @ApiOperation(value = "管理员修改资料", notes = "需要通过id获取原用户信息 需要username更新缓存")
    public Result<Object> edit(@ModelAttribute AdminAccount u,
                               @RequestParam(required = false) String[] roles) {

        AdminAccount currUser = securityUtil.getCurrUser();

        AdminAccount old = adminAccountService.getById(u.getId());
        //若修改了用户名
        if (!old.getUsername().equals(u.getUsername())) {
            //若修改用户名删除原用户名缓存
            redisTemplate.delete("user::" + old.getUsername());
            redisTemplate.delete("user::getid:" + old.getUsername());
            //判断新用户名是否存在
            if (adminAccountService.findByUsername(u.getUsername()) != null) {
                return new ResultUtil<Object>().setErrorMsg("该用户名已存在");
            }
            //删除缓存
            redisTemplate.delete("user::" + u.getUsername());
            redisTemplate.delete("user::getid:" + u.getUsername());

        }

        // 若修改了手机和邮箱判断是否唯一
        if (!old.getMobile().equals(u.getMobile()) && adminAccountService.findByMobile(u.getMobile()) != null) {
            return new ResultUtil<Object>().setErrorMsg("该手机号已绑定其他账户");
        }
        if (!old.getEmail().equals(u.getEmail()) && adminAccountService.findByEmail(u.getEmail()) != null) {
            return new ResultUtil<Object>().setErrorMsg("该邮箱已绑定其他账户");
        }

        u.setPassword(old.getPassword());
        //JPA更新操作
        AdminAccount user = adminAccountService.createAccount(u);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        //删除该用户角色
        adminRoleService.remove(new QueryWrapper<AdminRole>().eq("user_id",u.getId()));
        if (roles != null && roles.length > 0) {
            //新角色
            for (String roleId : roles) {
                AdminRole ur = new AdminRole();
                ur.setId(SnowFlakeUtil.getPrimaryKeyId().toString()).setCreateBy(currUser.getId()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG).setUpdateTime(DateUtil.now()).setUpdateBy(currUser.getId());
                ur.setRoleId(roleId);
                ur.setUserId(u.getId());
                adminRoleService.save(ur);
            }
        }
        //手动删除缓存
        redisTemplate.delete("userRole::" + u.getId());
        redisTemplate.delete("userRole::depIds:" + u.getId());
        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户自己资料", notes = "用户名密码不会修改 需要username更新缓存")
    public Result<Object> editOwn(@ModelAttribute AdminAccount u) {

        AdminAccount old = securityUtil.getCurrUser();
        u.setUsername(old.getUsername());
        u.setPassword(old.getPassword());
        //JPA更新用户信息
        AdminAccount user = adminAccountService.createAccount(u);
        if (user == null) {
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }


    @RequestMapping(value = "/changeMobile", method = RequestMethod.POST)
    @ApiOperation(value = "修改绑定手机")
    public Result<Object> changeMobile(@RequestParam String mobile) {

        if (adminAccountService.findByMobile(mobile) != null) {
            return new ResultUtil<Object>().setErrorMsg("该手机号已绑定其他账户");
        }
        AdminAccount u = securityUtil.getCurrUser();
        u.setMobile(mobile);
        adminAccountService.updateById(u);
        // 删除缓存
        redisTemplate.delete("user::" + u.getUsername());
        // 已验证清除key
        redisTemplate.delete(CommonConstant.PRE_SMS + mobile);
        return new ResultUtil<Object>().setSuccessMsg("修改手机号成功");
    }



}
