package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("添加普通反馈")
public class AddNormalFeedbackReq extends RequestBodyReq {


    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("文本内容")
    private String content;

    @ApiModelProperty("描述图片")
    private String descImage;

}
