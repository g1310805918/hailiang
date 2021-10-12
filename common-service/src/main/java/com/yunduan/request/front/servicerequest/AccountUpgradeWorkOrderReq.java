package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class AccountUpgradeWorkOrderReq extends RequestBodyReq {


    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("升级原因")
    private String reason;

}
