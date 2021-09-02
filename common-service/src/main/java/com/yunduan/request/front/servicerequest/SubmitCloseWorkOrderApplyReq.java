package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("提交关闭工单申请")
public class SubmitCloseWorkOrderApplyReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("关闭原因")
    private String closeReason;

    @ApiModelProperty("关闭反馈")
    private String closeFeedback;
}
