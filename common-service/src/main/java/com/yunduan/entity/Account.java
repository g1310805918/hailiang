package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.vo.AccountBindingCSI;
import com.yunduan.vo.FavoritesVo;
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
import java.util.Date;
import java.util.List;

@Data
@Entity
@ApiModel("用户表")
@Table(name = "tb_account")
@TableName("tb_account")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class Account implements Serializable {
    private static final long serialVersionUID = -2257083485104008000L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("头像")
    private String headPic;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("添加时间")
    private Date createTime;

    @TableLogic
    @ApiModelProperty("删除标志 0否、1是")
    private Integer delFlag;

    @ApiModelProperty("微信openId")
    private String openId;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty("用户id，字符串形式")
    private String accountId;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty("添加时间yyyy-MM-dd HH:mm:ss")
    private String createDateTime;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty("修改时间yyyy-MM-dd HH:mm:ss")
    private String updateDateTime;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty("用户收藏夹列表")
    private List<FavoritesVo> favoritesVoList;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty("绑定记录")
    private List<AccountBindingCSI> bindingCSIList;
}
