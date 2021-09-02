package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("三级分类列表")
@Accessors(chain = true)
public class KnowledgeLevel3CategoryList implements Serializable {
    private static final long serialVersionUID = 3477356976495184651L;


    @ApiModelProperty("一级标题名")
    private String oneTitle;

    @ApiModelProperty("子类列表")
    private List<KnowledgeTwoThreeCategoryVo> voList;

}
