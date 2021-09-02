package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("工程师首页初始化")
public class EngineerIndexInitReq extends RequestPageReq {

    @ApiModelProperty("受理人id【公司其他工程师id】")
    private String acceptPerson;

    @ApiModelProperty("三级分类id")
    private String threeCategoryId;

    @ApiModelProperty("我受理中的工单")
    private Boolean myAcceptOrder;

    @ApiModelProperty("我已完结工单")
    private Boolean myCloseOrder;

    @ApiModelProperty("我收藏的工单")
    private Boolean myCollectionOrder;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("工单编号【工单编号、问题概要  二选一、也可以都为空】")
    private String outTradeNo;

    @ApiModelProperty("问题概要【工单编号、问题概要  二选一、也可以都为空】")
    private String problemProfile;
}
