package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@ApiModel("bug初始化列表")
public class BugInitPageVo implements Serializable {
    private static final long serialVersionUID = 7238455378696362512L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("bug标题")
    private String bugTitle;

    @ApiModelProperty("关联的工单编号")
    private String outTradeNo;

    @ApiModelProperty("分组名称")
    private String categoryName;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("文档状态")
    private Integer docStatus;

}
