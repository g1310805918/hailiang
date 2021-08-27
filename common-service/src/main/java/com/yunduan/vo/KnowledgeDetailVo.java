package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("知识文档详情")
@Accessors(chain = true)
public class KnowledgeDetailVo implements Serializable {
    private static final long serialVersionUID = 5174682743513740257L;

    @ApiModelProperty("文档id")
    private String id;

    @ApiModelProperty("所属分类名称")
    private String categoryName;

    @ApiModelProperty("文档编号")
    private String docNumber;

    @ApiModelProperty("添加时间")
    private String createTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("是否收藏【0否、1是】")
    private Integer isCollect;

    @ApiModelProperty("文档类型【1知识文档、2bug文档】")
    private Integer docType;

    @ApiModelProperty("文档标题")
    private String docTitle;

    @ApiModelProperty("文档内容")
    private String docContent;

}
