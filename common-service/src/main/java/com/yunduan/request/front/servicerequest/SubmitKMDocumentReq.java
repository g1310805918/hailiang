package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("提交文档")
public class SubmitKMDocumentReq extends RequestBodyReq {

    @ApiModelProperty("工单id")
    private String workOrderId;

    @ApiModelProperty("当前流程【1-1、1-2、1-3、2-1、2-2......】")
    private String currentProcess;

    @ApiModelProperty("文档id")
    private String documentId;

}
