package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("工单列表")
@Accessors(chain = true)
public class WorkOrderListVo implements Serializable {
    private static final long serialVersionUID = 3721944564010769083L;

    @ApiModelProperty("工单id")
    private String id;

    @ApiModelProperty("问题概要")
    private String problemProfile;

    @ApiModelProperty("工单编号")
    private String outTradeNo;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("上次更新时间")
    private String updateTime;

    @ApiModelProperty("工单问题严重等级【1服务完全丢失、2服务严重丢失、3少量丢失、4未丢失服务】")
    private Integer problemSeverity;

    @ApiModelProperty("主要联系人姓名")
    private String mainContact;

    @ApiModelProperty("工单状态【1待处理、2进行中、3待反馈、4已关闭】")
    private Integer status;

    @ApiModelProperty("客户服务号")
    private String csiNumber;

    @ApiModelProperty("用户是否收藏了当前工单【0否、1是】")
    private Integer isCollection;

}
