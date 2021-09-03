package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@ApiModel("工程师获取工单基本信息")
public class EngineerWorkOrderBaseInfoVo extends WorkOrderDetailBaseInfoVo {
    private static final long serialVersionUID = -5684515928458295006L;

    @ApiModelProperty("当前工单处理流程【1-1、1-2、1-3、2-1、2-2......】")
    private String currentProcess;

}
