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
@ApiModel("工程师收藏工单表")
@Table(name = "tb_collection_engineer")
@TableName("tb_collection_engineer")
@Accessors(chain = true)
public class CollectionEngineer implements Serializable {
    private static final long serialVersionUID = 4678773051628775222L;

    @Id
    @TableId(type = IdType.NONE)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @ApiModelProperty("主键id")
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("工程师id")
    private Long engineerId;

    @ApiModelProperty("工单id")
    private Long workOrderId;

    @ApiModelProperty("添加时间")
    private String createTime = DateUtil.now();

}
