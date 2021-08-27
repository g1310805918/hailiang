package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@ApiModel("用户知识文档收藏表")
@Table(name = "tb_collection_account_document")
@TableName("tb_collection_account_document")
@Accessors(chain = true)
public class CollectionAccountDocument implements Serializable {
    private static final long serialVersionUID = -8287444881599660569L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long accountId;

    @ApiModelProperty("用户收藏夹id")
    private Long favoritesId;

    @ApiModelProperty("文档id")
    private Long documentId;

    @ApiModelProperty("添加时间")
    private String createTime;

}
