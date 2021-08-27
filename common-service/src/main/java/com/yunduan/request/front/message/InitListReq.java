package com.yunduan.request.front.message;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("初始化")
public class InitListReq extends RequestPageReq {

    @ApiModelProperty("消息类型【1系统消息、2验证消息、全部消息为空】")
    private Integer messageType;
}
