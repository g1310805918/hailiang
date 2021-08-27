package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("知识文档库一级分类")
@Accessors(chain = true)
public class KnowledgeOneCategoryVo implements Serializable {
    private static final long serialVersionUID = 4521796447932800507L;

    @ApiModelProperty("一级分类id")
    private String id;

    @ApiModelProperty("标题")
    private String title;

}
