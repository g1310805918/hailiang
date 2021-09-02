package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("收藏夹列表")
@Accessors(chain = true)
public class FavoritesVo implements Serializable {
    private static final long serialVersionUID = -2203264730446826001L;

    @ApiModelProperty("收藏夹id")
    private String id;

    @ApiModelProperty("收藏夹名称")
    private String favoritesName;

    @ApiModelProperty("收藏夹下知识文档总数")
    private Integer count;

}
