package com.yunduan.request.front.review;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("文档审核页面初始化")
public class ReviewInitReq extends RequestPageReq {

    @ApiModelProperty("待审核")
    private Boolean noPassReview;

    @ApiModelProperty("审核已通过")
    private Boolean passReview;

    @ApiModelProperty("已拒绝")
    private Boolean refusedReview;

    @ApiModelProperty("发布人id")
    private String engineerId;

    @ApiModelProperty("三级分类id")
    private String threeCategoryId;

    @ApiModelProperty("文档编号")
    private String documentId;

    @ApiModelProperty("文档标题")
    private String documentTitle;

}
