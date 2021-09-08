package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("工程师消息详情")
@Accessors(chain = true)
public class EngineerMsgDetailVo implements Serializable {
    private static final long serialVersionUID = 4961473868129324508L;


    @ApiModelProperty("消息id")
    private String id;

    @ApiModelProperty("消息类型【1工单消息、2文档消息、3bug消息、4系统消息】")
    private Integer msgType;

    @ApiModelProperty("消息类型对应的id")
    private String typeId;

    @ApiModelProperty("消息标题")
    private String title;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("时间")
    private String createTime;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("问题概要")
    private String problemProfile;

}
