package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@Data
@ApiModel("工单问题概要")
@Accessors(chain = true)
public class WorkOrderProblemProfile implements Serializable {
    private static final long serialVersionUID = -4943708940756862632L;

    @ApiModelProperty("头像")
    private String headPic;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("发送时间")
    private String createTime;

    @ApiModelProperty("CDM标签")
    private String VDMCode;

    @ApiModelProperty("问题概要")
    private String problemProfile;

    @ApiModelProperty("问题描述")
    private String problemDescription;

    @ApiModelProperty("问题截图")
    private List<String> problemImages;

    @ApiModelProperty("附件地址")
    private String attachmentPath;
}
