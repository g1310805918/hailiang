package com.yunduan.request.front.message;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("工程师端消息初始化")
public class EngineerInitPageReq extends RequestPageReq {

    @ApiModelProperty("消息类型，空表示全部消息、1表示工单消息、2表示文档消息、3表示bug消息")
    private Integer messageType;

    @ApiModelProperty("天数筛选，空表示所有、0表示今天、7表示最近七天、30表示最近三十天")
    private Integer days;

    @ApiModelProperty("是否已读【空表示全部消息、0表示未读消息、1表示已读消息】")
    private Integer isRead;

}
