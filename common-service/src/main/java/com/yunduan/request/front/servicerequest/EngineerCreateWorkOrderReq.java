package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加工单")
public class EngineerCreateWorkOrderReq extends RequestBodyReq {

    @ApiModelProperty("当前需要分裂的工单id")
    private String workOrderId;

    @ApiModelProperty("问题概要")
    private String problemProfile;

    @ApiModelProperty("问题描述")
    private String problemDescription;

    @ApiModelProperty("问题描述图片路径")
    private String problemDesImagePath;

    @ApiModelProperty("错误代码")
    private String errorCode;

    @ApiModelProperty("产品名称、版本")
    private String productNameVersion;

    @ApiModelProperty("问题类型")
    private String problemType;

    @ApiModelProperty("三级分类id == 问题类型的id")
    private String categoryId;

    @ApiModelProperty("附件地址")
    private String attachmentPath;

}

