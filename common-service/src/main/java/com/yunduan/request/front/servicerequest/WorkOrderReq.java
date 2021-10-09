package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("工单请求")
public class WorkOrderReq extends RequestPageReq {

    @ApiModelProperty("工单类型【1非技术工单、2技术工单】")
    private Integer workOrderType;

    @ApiModelProperty("我收藏的工单【布尔值  true、false】")
    private Boolean myCollection;

    @ApiModelProperty("我受理的工单【布尔值  true、false】")
    private Boolean myAccept;

    @ApiModelProperty("我进行中的工单【布尔值  true、false】")
    private Boolean myOngoing;

    @ApiModelProperty("问题概要")
    private String problemProfile;

    @ApiModelProperty("技术请求编号")
    private String outTradeNo;

    @ApiModelProperty("客户服务号数组")
    private List<String> customerCSINumber;
}
