package com.yunduan.entity;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@ApiModel("用户工单表")
@Table(name = "tb_work_order")
@TableName("tb_work_order")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class WorkOrder implements Serializable {
    private static final long serialVersionUID = -7922806788685128144L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("用户id")
    private Long accountId;

    @ApiModelProperty("处理的工程师id")
    private Long engineerId;

    @ApiModelProperty("工单编号")
    private String outTradeNo;

    @ApiModelProperty("工单问题严重等级【1服务完全丢失、2服务严重丢失、3少量丢失、4未丢失服务】")
    private Integer problemSeverity;

    @ApiModelProperty("问题概要")
    private String problemProfile;

    @ApiModelProperty("问题描述")
    private String problemDescription;

    @ApiModelProperty("问题描述图片【逗号分割】")
    private String problemDesImage;

    @ApiModelProperty("错误代码")
    private String errorCode;

    @ApiModelProperty("附件地址")
    private String attachmentPath;

    @ApiModelProperty("附件注释")
    private String attachmentDescription;

    @ApiModelProperty("主要联系人姓名")
    private String mainContact;

    @ApiModelProperty("主要联系人手机号")
    private String mainMobile;

    @ApiModelProperty("主要联系人邮箱")
    private String mainEmail;

    @ApiModelProperty("优先联系方式【1手机号、2邮箱号】")
    private Integer contactWay;

    @ApiModelProperty("备用联系人姓名【u1严重等级才有值】")
    private String standbyContact;

    @ApiModelProperty("备用联系人手机号【u1严重等级才有值】")
    private String standbyMobile;

    @ApiModelProperty("备用联系人邮箱【u1严重等级才有值】")
    private String standbyEmail;

    @ApiModelProperty("工单类型【1非技术工单、2技术工单】")
    private Integer type;

    @ApiModelProperty("硬件平台")
    private String hardwarePlatform;

    @ApiModelProperty("操作系统")
    private String operatingSystem;

    @ApiModelProperty("操作系统版本")
    private String operatingSystemVersion;

    @ApiModelProperty("系统语言")
    private String operatingSystemLanguage;

    @ApiModelProperty("部署方式")
    private String deploymentWay;

    @ApiModelProperty("产品名称、版本【前两级分类名称】")
    private String productNameVersion;

    @ApiModelProperty("问题类型【第三级分类名称】")
    private String problemType;

    @ApiModelProperty("客户服务号")
    private String csiNumber;

    @ApiModelProperty("用户是否收藏了当前工单【0否、1是】")
    private Integer isCollection = 0;

    @ApiModelProperty("工单状态【1待处理、2进行中、3待反馈、4已关闭、5用户提交关闭申请】")
    private Integer status;

    @TableLogic
    @ApiModelProperty("删除标志【0未删除、1已删除】")
    private Integer delFlag = 0;

    @ApiModelProperty("最后更新时间【yyyy-MM-dd HH:mm:ss】")
    private String lastUpdateTime;

    @ApiModelProperty("提交工单时间【yyyy-MM-dd HH:mm:ss】")
    private String createTime = DateUtil.now();

    @ApiModelProperty("工单升级状态【0未升级、1已升级】")
    private Integer upgradeStatus = 0;

    @ApiModelProperty("升级原因")
    private String upgradeReason;

    @ApiModelProperty("相关链接")
    private String relatedLinks;

    @ApiModelProperty("相关知识文档")
    private String relatedDoc;

    @ApiModelProperty("相关bug文档")
    private String relatedBugDoc;

    @ApiModelProperty("用户关闭工单理由")
    private String closeReason;

    @ApiModelProperty("用户关闭工单反馈")
    private String closeFeedback;

    @ApiModelProperty("当前工单流程【1-1、1-2、2-1、2-2、。。。。。。】")
    private String currentProcess;

    @ApiModelProperty("工程师关闭工单时所关联的结单文档")
    private Long closeAssDocument;

    @ApiModelProperty("工程师关闭工单理由")
    private String engineerCloseReason;

    @ApiModelProperty("工程师关闭工单反馈")
    private String engineerCloseFeedback;

    @ApiModelProperty("用户重开工单原因")
    private String openAgainReason;

    @ApiModelProperty("用户重开工单描述")
    private String openAgainDesc;

    @ApiModelProperty("工程师重开工单原因")
    private String engineerOpenAgainReason;

    @ApiModelProperty("工程师重开工单描述")
    private String engineerOpenAgainDesc;

    @ApiModelProperty("三级分类id")
    private String categoryId;


}
