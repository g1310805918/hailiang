package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChangeWorkOrderContactInfoReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("联系人姓名")
    private String mainContact;

    @ApiModelProperty("联系人手机号")
    private String mainMobile;

    @ApiModelProperty("联系人邮箱")
    private String mainEmail;

    @ApiModelProperty("优先联系方式【1手机号、2邮箱号】")
    private Integer contactWay;

    @ApiModelProperty("修改类型【1主要联系人信息、2备用联系人信息】，默认为1")
    private Integer type = 1;

}
