package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除相关信息")
public class EngineerRemoveRelatedDocumentReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("文档id")
    private String documentId;

    @ApiModelProperty("文档类型【1知识文档、2bug文档】")
    private Integer documentType;


}
