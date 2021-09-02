package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("工单沟通记录")
public class WorkOrderCommunicationReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("排序方式  asc正序、desc倒序【小写字母】")
    private String sortWay;

    @ApiModelProperty("VDM标签【问题澄清、问题证据.......】")
    private String VDMCode;
}
