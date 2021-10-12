package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@Data
@ApiModel("产品名称、版本")
@Accessors(chain = true)
public class ProductNameVersionVo implements Serializable {
    private static final long serialVersionUID = -2984014385750378274L;

    @ApiModelProperty("一级id")
    private String id;

    @ApiModelProperty("一级分类名称")
    private String categoryName;

    @ApiModelProperty("下级分类列表")
    private List<ProductNameVersionTwoVo> twoCategoryList;

}
