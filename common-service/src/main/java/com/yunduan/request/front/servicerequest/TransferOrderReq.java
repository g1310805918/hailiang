package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("工单转单")
public class TransferOrderReq extends RequestBodyReq {


    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("需要转给的工程师id")
    private String engineerId;

}
