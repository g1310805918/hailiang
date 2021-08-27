package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("知识文档库二三级分类")
@Accessors(chain = true)
public class KnowledgeTwoThreeCategoryVo implements Serializable {
    private static final long serialVersionUID = 4521796447932800507L;

    @ApiModelProperty("二级分类id")
    private String id;

    @ApiModelProperty("二级分类标题")
    private String title;

    @ApiModelProperty("三级分类列表")
    private List<KnowledgeOneCategoryVo> threeCategoryList;

}
