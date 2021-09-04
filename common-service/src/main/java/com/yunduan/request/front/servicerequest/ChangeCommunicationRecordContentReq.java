package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("工程师编辑沟通记录")
public class ChangeCommunicationRecordContentReq extends RequestBodyReq {

    @ApiModelProperty("沟通记录id")
    private String recordId;

    @ApiModelProperty("编辑后的内容")
    private String content;
}
