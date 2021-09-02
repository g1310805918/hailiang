package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@ApiModel("知识文档模糊搜索")
@Accessors(chain = true)
public class KnowledgeLazySearchVo implements Serializable {
    private static final long serialVersionUID = -9039410836852794058L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("标题")
    private String categoryTitle;

    @ApiModelProperty("标题类型【1一级分类、2二级分类、3三级分类、4知识文档】")
    private Integer type;

}
