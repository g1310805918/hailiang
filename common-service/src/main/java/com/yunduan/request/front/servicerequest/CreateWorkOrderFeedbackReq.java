package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("添加工单反馈记录")
public class CreateWorkOrderFeedbackReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("反馈内容")
    private String feedbackContent;

    @ApiModelProperty("描述图片")
    private String descImage;
}
