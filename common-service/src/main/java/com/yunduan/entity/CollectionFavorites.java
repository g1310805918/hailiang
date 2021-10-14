package com.yunduan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.config.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;



@Data
@Entity
@ApiModel("用户收藏夹表")
@Table(name = "tb_collection_favorites")
@TableName("tb_collection_favorites")
@Accessors(chain = true)
public class CollectionFavorites implements Serializable {
    private static final long serialVersionUID = 6446671104579953288L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;


    @Transient
    @TableField(exist = false)
    @ApiModelProperty("字符串id")
    private String strId;

    @ApiModelProperty("用户id")
    private Long accountId;

    @ApiModelProperty("收藏夹名称")
    private String favoritesName;

    @ApiModelProperty("添加时间")
    private String createTime;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty("收藏夹文件数量")
    private Integer collectionCount;

    @Transient
    @TableField(exist = false)
    @ApiModelProperty("该文件夹是否收藏了当前文档【0否、1是】")
    private Integer isCollect;

}
