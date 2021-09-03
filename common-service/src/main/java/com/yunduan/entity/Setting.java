package com.yunduan.entity;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Entity
@ApiModel("系统设置表")
@Table(name = "tb_setting")
@TableName("tb_setting")
@Accessors(chain = true)
public class Setting implements Serializable {
    private static final long serialVersionUID = -819453128032980046L;

    @Id
    @TableId(type = IdType.NONE)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @ApiModelProperty("主键id")
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("模板名称code")
    private String temCode;

    @ApiModelProperty("邮箱地址")
    private String emailAddress;

    @ApiModelProperty("邮箱密码")
    private String emailPassword;

    @ApiModelProperty("修改时间")
    private String updateTime;

    @ApiModelProperty("添加时间")
    private String createTime = DateUtil.now();

}
