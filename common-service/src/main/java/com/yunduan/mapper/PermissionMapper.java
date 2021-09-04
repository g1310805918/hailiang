package com.yunduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunduan.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {


    /**
     * 通过用户id获取
     *
     * @param userId
     * @return
     */
    List<Permission> findByUserId(@Param("userId") String userId);


}
