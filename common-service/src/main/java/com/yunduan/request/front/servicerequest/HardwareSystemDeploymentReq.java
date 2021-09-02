package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("硬件平台、操作系统、部署方式")
public class HardwareSystemDeploymentReq extends RequestBodyReq {

    @ApiModelProperty("标志名称【硬件平台、操作系统、部署方式】")
    private String codeName;
}
