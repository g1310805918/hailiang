package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("bug反馈详情")
@Accessors(chain = true)
public class BudDetailVo implements Serializable {
    private static final long serialVersionUID = -5789781274332810530L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("分类名")
    private String categoryName;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("添加时间")
    private String createTime;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("关联工单编号")
    private String outTradeNo;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("内容")
    private String content;

}
