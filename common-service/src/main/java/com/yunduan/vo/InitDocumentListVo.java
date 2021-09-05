package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@ApiModel("初始化工程师文档列表")
@Accessors(chain = true)
public class InitDocumentListVo implements Serializable {
    private static final long serialVersionUID = -4055515732102640702L;

    @ApiModelProperty("文档id")
    private String documentId;

    @ApiModelProperty("文档标题")
    private String documentTitle;

    @ApiModelProperty("文档编号")
    private String documentNum;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("文档类型【1知识文档、2bug文档】")
    private Integer documentType;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("上次更新时间")
    private String lastUpdateTime;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("文档状态【1待审核、2审核通过、3审核拒绝】")
    private Integer documentStatus;

    @ApiModelProperty("是否收藏【0否。1是】")
    private Integer isCollect;
}
