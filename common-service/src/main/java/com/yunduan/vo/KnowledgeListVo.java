package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("知识文档列表")
@Accessors(chain = true)
public class KnowledgeListVo implements Serializable {
    private static final long serialVersionUID = -6583353645267636345L;


    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("文档id")
    private String docId;

    @ApiModelProperty("文档编号")
    private String documentNumber;

    @ApiModelProperty("文档标题")
    private String docTitle;

    @ApiModelProperty("文档概要")
    private String docProfile;

    @ApiModelProperty("来自：分类名称")
    private String categoryName;
}
