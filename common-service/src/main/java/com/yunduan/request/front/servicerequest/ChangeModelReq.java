package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("模块更改")
public class ChangeModelReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("产品名称、版本【前两级分类名称】")
    private String productNameVersion;

    @ApiModelProperty("问题类型【第三级分类名称】")
    private String productType;
}
