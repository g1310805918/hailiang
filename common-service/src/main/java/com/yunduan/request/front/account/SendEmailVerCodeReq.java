package com.yunduan.request.front.account;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发送邮件验证码")
public class SendEmailVerCodeReq extends RequestBodyReq {

    @ApiModelProperty("邮箱")
    private String email;
}
