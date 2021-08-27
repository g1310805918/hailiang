package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("消息详情")
@Accessors(chain = true)
public class MsgDetailVo implements Serializable {
    private static final long serialVersionUID = 2439805328544462206L;

    @ApiModelProperty("消息id【为系统消息才有值】")
    private String id;

    @ApiModelProperty("消息标题【为系统消息才有值】")
    private String messageTitle;

    @ApiModelProperty("接收消息时间【为系统消息才有值】")
    private String createTime;

    @ApiModelProperty("消息内容【富文本】【为系统消息才有值】")
    private String content;

    @ApiModelProperty("用户名【为验证消息类型才有值】")
    private String username;

    @ApiModelProperty("手机号【为验证消息类型才有值】")
    private String mobile;

    @ApiModelProperty("ID账号【为验证消息类型才有值】")
    private String accountId;

    @ApiModelProperty("申请时间【为验证消息类型才有值】")
    private String applyTime;

    @ApiModelProperty("申请状态【1待审核、2审核通过、3审核失败】【为验证消息类型才有值】")
    private String applyStatus;
}
