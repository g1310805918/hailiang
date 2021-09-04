package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author Guo
 * @since 2021-08-21
 */
@Data
@Entity
@TableName("tb_permission")
@Table(name = "tb_permission")
@Where(clause = "del_flag=0")
@Accessors(chain = true)
@ApiModel(value="Permission对象", description="菜单权限表")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "说明备注")
    private String description;

    @ApiModelProperty(value = "菜单/权限名称")
    private String name;

    @ApiModelProperty(value = "父id")
    private String parentId;

    @ApiModelProperty(value = "类型 -1顶部菜单 0页面 1具体操作")
    private Integer type;

    @ApiModelProperty(value = "排序值")
    private BigDecimal sortOrder;

    @ApiModelProperty(value = "前端组件")
    private String component;

    @ApiModelProperty(value = "页面路径/资源链接url")
    private String path;

    @ApiModelProperty(value = "菜单标题")
    private String title;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "层级")
    private Integer level;

    @ApiModelProperty(value = "按钮权限类型")
    private String buttonType;

    @ApiModelProperty(value = "是否启用 0启用 -1禁用")
    private Integer status;

    @ApiModelProperty(value = "网页链接")
    private String url;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @TableLogic
    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;

    @ApiModelProperty(value = "始终显示 默认是")
    private Boolean showAlways;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "子菜单/权限")
    private List<Permission> children;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "页面拥有的权限类型")
    private List<String> permTypes;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "节点展开 前端所需")
    private Boolean expand = true;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "是否勾选 前端所需")
    private Boolean checked = false;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty(value = "是否选中 前端所需")
    private Boolean selected = false;

}
