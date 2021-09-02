package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加工单")
public class CreateWorkOrderReq extends RequestBodyReq {

    @ApiModelProperty("工单类型【1非技术工单、2技术工单】")
    private Integer workOrderType;

    @ApiModelProperty("工单问题严重等级【1服务完全丢失、2服务严重丢失、3少量丢失、4未丢失服务】")
    private Integer problemSeverity;

    @ApiModelProperty("问题概要")
    private String problemProfile;

    @ApiModelProperty("问题描述")
    private String problemDescription;

    @ApiModelProperty("问题描述图片路径")
    private String problemDesImagePath;

    @ApiModelProperty("错误代码")
    private String errorCode;

    @ApiModelProperty("硬件平台")
    private String hardware;

    @ApiModelProperty("操作系统")
    private String operationSystem;

    @ApiModelProperty("操作系统版本")
    private String operationSystemVersion;

    @ApiModelProperty("操作系统版本语言")
    private String operationSystemVersionLanguage;

    @ApiModelProperty("部署方式")
    private String deploymentType;

    @ApiModelProperty("产品名称、版本")
    private String productNameVersion;

    @ApiModelProperty("问题类型")
    private String problemType;

    @ApiModelProperty("客户服务号")
    private String csiNumber;

    @ApiModelProperty("附件地址")
    private String attachmentPath;

    @ApiModelProperty("附件描述")
    private String attachmentDescription;

    @ApiModelProperty("主要联系人")
    private String mainContact;

    @ApiModelProperty("主要联系人手机号")
    private String mainMobile;

    @ApiModelProperty("主要联系人邮箱")
    private String mainEmail;

    @ApiModelProperty("优先联系方式【1手机号、2邮箱号】")
    private Integer contactWay;

    @ApiModelProperty("备用联系人")
    private String standbyContact;

    @ApiModelProperty("备用联系人手机")
    private String standbyMobile;

    @ApiModelProperty("备用联系人邮箱")
    private String standbyEmail;

}

