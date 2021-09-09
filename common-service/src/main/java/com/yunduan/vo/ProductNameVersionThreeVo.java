package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("三级分类")
@Accessors(chain = true)
public class ProductNameVersionThreeVo implements Serializable {
    private static final long serialVersionUID = 8031158591756104876L;

    @ApiModelProperty("三级分类id")
    private String id;

    @ApiModelProperty("三级分类名称")
    private String categoryName;
}
