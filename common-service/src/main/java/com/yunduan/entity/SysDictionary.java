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
@ApiModel("用户表")
@Table(name = "tb_sys_dictionary")
@TableName("tb_sys_dictionary")
@Accessors(chain = true)
public class SysDictionary implements Serializable {
    private static final long serialVersionUID = -2396220751340862668L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("标志code")
    private String codeName;

    @ApiModelProperty("标志内容")
    private String content;

    @ApiModelProperty("添加时间")
    private String createTime = DateUtil.now();

    @ApiModelProperty("父id")
    private Long parentId;

    @ApiModelProperty("父、父id")
    private Long parentParentId;

}
