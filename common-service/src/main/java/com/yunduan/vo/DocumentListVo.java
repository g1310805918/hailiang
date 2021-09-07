package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@ApiModel("KM文档管理动态搜索文档")
@Accessors(chain = true)
public class DocumentListVo implements Serializable {
    private static final long serialVersionUID = -7295598017726981123L;

    @ApiModelProperty("文档id")
    private String id;

    @ApiModelProperty("文档标题")
    private String title;

}
