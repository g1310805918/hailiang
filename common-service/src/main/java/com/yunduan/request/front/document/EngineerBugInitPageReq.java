package com.yunduan.request.front.document;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("工程师Bug反馈列表初始化")
public class EngineerBugInitPageReq extends RequestPageReq {

    @ApiModelProperty("分类id")
    private String categoryId;

    @ApiModelProperty("待审核")
    private Boolean noReview;

    @ApiModelProperty("审核通过")
    private Boolean passReview;

    @ApiModelProperty("审核拒绝")
    private Boolean refusedReview;

    @ApiModelProperty("bug标题")
    private String bugTitle;

    @ApiModelProperty("关联的工单编号")
    private String outTradeNo;

}
