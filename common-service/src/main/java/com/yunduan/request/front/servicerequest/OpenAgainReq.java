package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("重开工单")
public class OpenAgainReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("重开工单原因")
    private String openAgainReason;

    @ApiModelProperty("重开工单描述")
    private String openAgainDesc;

}
