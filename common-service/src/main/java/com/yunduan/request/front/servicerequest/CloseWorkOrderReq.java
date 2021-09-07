package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("关闭工单")
public class CloseWorkOrderReq extends RequestBodyReq {


    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("关闭工单理由")
    private String reason;

    @ApiModelProperty("结单反馈")
    private String feedback;

}
