package com.yunduan.request.front;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("请求对象")
public class RequestBodyReq {

    @ApiModelProperty("加密后传递数据")
    private String data;
}
