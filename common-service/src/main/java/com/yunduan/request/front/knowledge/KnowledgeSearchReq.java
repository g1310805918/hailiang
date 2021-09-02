package com.yunduan.request.front.knowledge;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("模糊搜索分页")
public class KnowledgeSearchReq extends RequestPageReq {

    @ApiModelProperty("搜索的内容")
    private String searchContent;

    @ApiModelProperty("三级分类id")
    private String threeCategoryId;

    @ApiModelProperty("知识文档类型【1知识文档、2bug文档】空表示所有")
    private String knowledgeType;

}
