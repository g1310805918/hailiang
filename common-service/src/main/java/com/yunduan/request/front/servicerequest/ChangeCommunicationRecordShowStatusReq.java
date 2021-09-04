package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("修改工程师沟通记录可见状态")
public class ChangeCommunicationRecordShowStatusReq extends RequestBodyReq {

    @ApiModelProperty("沟通记录id")
    private String recordId;

    @ApiModelProperty("用户可见状态【0不可见、1可见】")
    private Integer isShowStatus;
}
