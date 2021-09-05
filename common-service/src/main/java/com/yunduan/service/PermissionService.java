package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.Permission;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    /**
     * 通过用户id获取
     *
     * @param userId
     * @return
     */
    List<Permission> findByUserId(String userId);

    /**
     * 通过层级查找
     * 默认升序
     *
     * @param level
     * @return
     */
    List<Permission> findByLevelOrderBySortOrder(Integer level);

    /**
     * 通过parendId查找
     *
     * @param parentId
     * @return
     */
    List<Permission> findByParentIdOrderBySortOrder(String parentId);

    /**
     * 通过类型和状态获取
     *
     * @param type
     * @param status
     * @return
     */
    List<Permission> findByTypeAndStatusOrderBySortOrder(Integer type, Integer status);

    /**
     * 通过名称获取
     *
     * @param title
     * @return
     */
    List<Permission> findByTitle(String title);

    /**
     * 模糊搜索
     *
     * @param title
     * @return
     */
    List<Permission> findByTitleLikeOrderBySortOrder(String title);


    /**
     * 添加权限
     * @param permission 权限
     * @return permission
     */
    Permission createPermission(Permission permission);


    /**
     * 更新权限
     * @param permission 权限
     * @return permission
     */
    Permission updatePermission(Permission permission);
}
