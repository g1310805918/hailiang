package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 后台管理员
 * </p>
 *
 * @author Guo
 * @since 2021-08-21
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@TableName("tb_admin_account")
@Table(name = "tb_admin_account")
@Where(clause = "del_flag = 0")
@Accessors(chain = true)
@ApiModel(value="AdminAccount对象", description="后台管理员")
public class AdminAccount implements Serializable {
    private static final long serialVersionUID = 604064912537575012L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "状态 默认0正常 -1拉黑")
    private Integer status;

    @ApiModelProperty(value = "用户类型 0普通用户 1管理员")
    private Integer type;

    @ApiModelProperty(value = "所属部门id")
    private String departmentId;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "密码强度")
    private String passStrength;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @TableLogic
    @ApiModelProperty(value = "删除标志 默认0")
    private Integer delFlag;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "用户拥有角色")
    private List<Role> roles;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "用户拥有的权限")
    private List<Permission> permissions;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "导入数据时使用")
    private Integer defaultRole;

}
