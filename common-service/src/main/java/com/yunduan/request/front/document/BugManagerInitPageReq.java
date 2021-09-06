package com.yunduan.request.front.document;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("bug管理页面初始化")
public class BugManagerInitPageReq extends RequestPageReq {

    @ApiModelProperty("工程师id")
    private String engineerId;

    @ApiModelProperty("三级分类id")
    private String categoryId;

    @ApiModelProperty("待我审核，布尔值【true/false】")
    private Boolean stayMyReview;

    @ApiModelProperty("审核通过，布尔值【true/false】")
    private Boolean reviewPass;

    @ApiModelProperty("bug标题")
    private String bugTitle;

    @ApiModelProperty("关联的工单编号")
    private String outTradeNo;

}
