package com.yunduan.request.front.document;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("添加bug反馈")
public class CreateBugFeedbackReq extends RequestBodyReq {

    @ApiModelProperty("关联的工单编号")
    private String outTradeNo;

    @ApiModelProperty("分类id")
    private String categoryId;

    @ApiModelProperty("bug标题")
    private String bugTitle;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("附件地址")
    private String attachmentPatch;

    @ApiModelProperty("附件注释")
    private String attachmentDescription;


}
