package com.yunduan.request.front.account;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发送短信验证码")
public class SendVerificationCodeReq extends RequestBodyReq {

    @ApiModelProperty("手机号")
    private String mobile;
}
