package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("用户消息列表")
@Accessors(chain = true)
public class AccountMessageListVo implements Serializable {
    private static final long serialVersionUID = 797059197285566721L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("消息标题")
    private String messageTitle;

    @ApiModelProperty("接收时间")
    private String createTime;

    @ApiModelProperty("消息类型【1系统消息、2验证消息】")
    private Integer messageType;

}
