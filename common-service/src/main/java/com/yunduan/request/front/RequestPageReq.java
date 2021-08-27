package com.yunduan.request.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RequestPageReq extends RequestBodyReq{

    @ApiModelProperty("页号")
    private Integer pageNo;

    @ApiModelProperty("一页显示数量")
    private Integer pageSize;

}
