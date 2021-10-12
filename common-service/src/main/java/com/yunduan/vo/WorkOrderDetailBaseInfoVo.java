package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("工单详情基本信息")
@Accessors(chain = true)
public class WorkOrderDetailBaseInfoVo implements Serializable {
    private static final long serialVersionUID = -6360059265624369645L;

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("工单类型【1非技术工单、2技术工单】")
    private Integer workOrderType;

    @ApiModelProperty("是否是自己的工单【0否、1是】")
    private Integer isMySelf;

    @ApiModelProperty("是否收藏工单【0否、1是】")
    private Integer isCollection;

    @ApiModelProperty("是否可以重开工单【0否、1是】")
    private Integer isCanOpenAgain;

    @ApiModelProperty("问题概要")
    private String problemProfile;

    @ApiModelProperty("工单状态【1待处理、2进行中、3待反馈、4已关闭】")
    private Integer status;

    @ApiModelProperty("工单编号")
    private String outTradeNo;

    @ApiModelProperty("工单问题严重等级【1服务完全丢失、2服务严重丢失、3少量丢失、4未丢失服务】")
    private Integer problemSeverity;

    @ApiModelProperty("工单升级状态【0未升级、1已升级】")
    private Integer upgradeStatus;

    @ApiModelProperty("工单升级原因")
    private String upgradeReason;

    @ApiModelProperty("提交时间")
    private String createTime;

    @ApiModelProperty("最后更新时间")
    private String lastUpdateTime;

    @ApiModelProperty("相关附件")
    private List<String> attachmentPath;

    @ApiModelProperty("相关链接")
    private List<String> relatedLinks;

    @ApiModelProperty("相关知识文档")
    private List<KnowledgeListVo> knowledgeDocId;

    @ApiModelProperty("相关bug文档")
    private List<KnowledgeListVo> knowLedgeBugDocId;

    @ApiModelProperty("客户服务号")
    private String csiNumber;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("主要联系人")
    private String mainContact;

    @ApiModelProperty("主要联系人手机号")
    private String mainMobile;

    @ApiModelProperty("主要联系人邮箱")
    private String mainEmail;

    @ApiModelProperty("备用联系人")
    private String standbyContact;

    @ApiModelProperty("备用联系人手机号")
    private String standbyMobile;

    @ApiModelProperty("备用联系人邮箱")
    private String standbyEmail;

    @ApiModelProperty("主要联系方式【1手机号、2邮箱号】")
    private Integer contactWay;

    @ApiModelProperty("硬件平台")
    private String hardwarePlatform;

    @ApiModelProperty("操作系统")
    private String operatingSystem;

    @ApiModelProperty("操作系统版本")
    private String operatingSystemVersion;

    @ApiModelProperty("语言")
    private String operatingSystemLanguage;

    @ApiModelProperty("部署方式")
    private String deploymentWay;

    @ApiModelProperty("产品名称、版本【前两级分类名称】")
    private String productNameVersion;

    @ApiModelProperty("问题类型【第三级分类名称】")
    private String problemType;

}
