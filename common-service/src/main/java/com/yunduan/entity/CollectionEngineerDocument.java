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
@ApiModel("工程师知识文档收藏表")
@Table(name = "tb_collection_engineer_document")
@TableName("tb_collection_engineer_document")
@Accessors(chain = true)
public class CollectionEngineerDocument implements Serializable {
    private static final long serialVersionUID = -8287444881599660569L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("工程师id")
    private Long engineerId;

    @ApiModelProperty("用户收藏夹id")
    private Long favoritesId;

    @ApiModelProperty("文档id")
    private Long documentId;

    @ApiModelProperty("添加时间")
    private String createTime = DateUtil.now();

}
