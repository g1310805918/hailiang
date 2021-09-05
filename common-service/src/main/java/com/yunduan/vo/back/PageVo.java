package com.yunduan.vo.back;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageVo implements Serializable {
    private static final long serialVersionUID = 7755867239705104459L;

    @ApiModelProperty(value = "页号")
    private int pageNumber = 1;

    @ApiModelProperty(value = "页面大小")
    private int pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "排序方式 asc/desc")
    private String order;

}
