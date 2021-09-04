package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 角色权限关系表
 * </p>
 *
 * @author Guo
 * @since 2021-08-21
 */
@Data
@Entity
@TableName("tb_role_permission")
@Table(name = "tb_role_permission")
@Where(clause = "del_flag=0")
@Accessors(chain = true)
@ApiModel(value="RolePermission对象", description="角色权限关系表")
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "创建人	")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @TableLogic
    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "权限id")
    private String permissionId;

    @ApiModelProperty(value = "角色id")
    private String roleId;

}
