package com.yunduan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
public class EngineerCategoryListVo implements Serializable {
    private static final long serialVersionUID = 5736020634276899580L;

    @ApiModelProperty("三级分类id")
    private String categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

}
