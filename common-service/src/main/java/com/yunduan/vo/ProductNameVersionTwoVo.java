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
public class ProductNameVersionTwoVo implements Serializable {
    private static final long serialVersionUID = -2984014385750378274L;

    @ApiModelProperty("二级分类id")
    private String twoCategoryId;

    @ApiModelProperty("二级级分类名称")
    private String twoCategoryName;
}
