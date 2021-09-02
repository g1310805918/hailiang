package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加附件")
public class CreateWorkOrderAttachReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("附件地址，多个附件间使用英文逗号分割")
    private String attachPath;

    @ApiModelProperty("附件描述")
    private String attachDescription;
}
