package com.yunduan.request.front.center;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加绑定CSI")
public class AddBindingCSIReq extends RequestBodyReq {

    @ApiModelProperty("csi编号")
    private String csiNumber;
}
