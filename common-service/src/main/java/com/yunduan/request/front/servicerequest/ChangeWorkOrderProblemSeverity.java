package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("调整工单严重等级")
public class ChangeWorkOrderProblemSeverity extends RequestBodyReq {


    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("工单问题严重等级【1服务完全丢失、2服务严重丢失、3少量丢失、4未丢失服务】")
    private Integer problemSeverity;

    @ApiModelProperty("备用联系人【严重等级为1时才有值】")
    private String standbyContact;

    @ApiModelProperty("备用联系人手机【严重等级为1时才有值】")
    private String standbyMobile;

    @ApiModelProperty("备用联系人邮箱【严重等级为1时才有值】")
    private String standbyEmail;

}
