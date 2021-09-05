package com.yunduan.request.front.document;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加文档")
public class CreateDocumentReq extends RequestBodyReq {

    @ApiModelProperty("文档标题")
    private String documentTitle;

    @ApiModelProperty("文档内容")
    private String documentContent;

    @ApiModelProperty("三级分类id")
    private String threeCategoryId;

    @ApiModelProperty("文档类型【1知识文档、2bug文档】")
    private Integer documentType;

    @ApiModelProperty("对内、对外可见【1对外、2对内】")
    private Integer isShow;
}
