package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("相关附件")
public class AddRelatedLinksReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("链接类型【1资料链接、2相关附件】")
    private Integer linkType;

    @ApiModelProperty("链接地址")
    private String path;

}
