package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("关联文档")
public class AddKnowledgeDocReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("文档类型【1相关知识文档、2相关bug文档】")
    private Integer documentType;

    @ApiModelProperty("文档编号")
    private String documentNumber;

}
