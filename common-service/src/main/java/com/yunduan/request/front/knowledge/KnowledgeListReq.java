package com.yunduan.request.front.knowledge;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分类下的知识文档")
public class KnowledgeListReq extends RequestPageReq {

    @ApiModelProperty("是否是工程师端查看文档列表，布尔值【true、false】，空表示false")
    private Boolean engineerFlag;

    @ApiModelProperty("三级分类id")
    private String threeCategoryId;

    @ApiModelProperty("文档类型【1知识文档、2bug文档、空表示所有】")
    private Integer docType;
}
