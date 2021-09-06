package com.yunduan.entity;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
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
import java.util.List;


@Data
@Entity
@ApiModel("工程师表")
@Table(name = "tb_engineer")
@TableName("tb_engineer")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class Engineer implements Serializable {
    private static final long serialVersionUID = -799764731367365816L;


    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private String headPic;

    @ApiModelProperty("电话")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("工程师身份【1海量员工、2技术支持工程师、3COE工程师、4Bde工程师】")
    private Integer identity;

    @ApiModelProperty("身份名称【海量员工、技术支持工程师、COE工程师、Bde工程师】")
    private String identityName;

    @ApiModelProperty("所属产品模块【3级分类id。逗号分割】")
    private String productCategoryId;

    @ApiModelProperty("产品模块名称")
    private String productCategoryName;

    @ApiModelProperty("账号状态【1正常、2冻结】")
    private Integer accountStatus = StatusCodeUtil.ENGINEER_ACCOUNT_NORMAL_STATUS;

    @ApiModelProperty("在线状态【1在线、2离线】")
    private Integer onlineStatus = StatusCodeUtil.ENGINEER_ACCOUNT_OFFLINE_STATUS;

    @TableLogic
    @ApiModelProperty("删除标志【0否、1是】")
    private Integer delFlag = StatusCodeUtil.NOT_DELETE_FLAG;

    @ApiModelProperty("创建时间")
    private String createTime = DateUtil.now();

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("最后登录时间")
    private String lastLoginTime;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty("用户收藏夹列表")
    private List<FavoritesVo> favoritesVoList;

}
