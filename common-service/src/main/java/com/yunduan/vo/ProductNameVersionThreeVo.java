package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("三级分类")
public class ProductNameVersionThreeVo implements Serializable {
    private static final long serialVersionUID = 8031158591756104876L;

    @ApiModelProperty("三级分类名称")
    private String categoryName;
}
