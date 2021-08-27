package com.yunduan.request.front.account;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("验证手机号以及验证码")
public class SecurityCheckMobileVerificationCodeReq extends RequestBodyReq {

    @ApiModelProperty("手机号/邮箱 【手机修改密码为手机、否则为邮箱】")
    private String mobile;

    @ApiModelProperty("验证码")
    private String yzm;

}
