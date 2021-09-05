package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("工程师文档详情")
public class DocumentDetailVo extends InitDocumentListVo {
    private static final long serialVersionUID = -4595801474421592667L;

    @ApiModelProperty("知识文档详情【富文本】")
    private String documentContent;

}
