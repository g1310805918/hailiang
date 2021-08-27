package com.yunduan.request.front.knowledge;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分类下的知识文档")
public class KnowledgeListReq extends RequestPageReq {

    @ApiModelProperty("三级分类id")
    private String threeCategoryId;

    @ApiModelProperty("文档类型【1知识文档、2bug文档、空表示所有】")
    private Integer docType;
}
