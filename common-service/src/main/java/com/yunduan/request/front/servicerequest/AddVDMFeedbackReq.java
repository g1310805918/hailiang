package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("添加VDM流程反馈")
public class AddVDMFeedbackReq extends RequestBodyReq {


    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("当前工单处理流程【1-1、1-2、1-3、2-1、2-2......】")
    private String currentProcess;

    @ApiModelProperty("回复标志【VDM问题澄清......】")
    private String VDMCode;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("描述图片")
    private String descImage;

}
