package com.yunduan.request.front.review;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("审核拒绝")
public class PassReviewReq extends RequestBodyReq {

    @ApiModelProperty("文档id")
    private String id;

    @ApiModelProperty("拒绝原因")
    private String reason;
}
