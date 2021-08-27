package com.yunduan.request.front.account;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("验证手机号以及图片验证码是否正确")
public class SecurityCheckReq extends RequestBodyReq {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("图片验证码id")
    private String captchaId;

    @ApiModelProperty("图片验证码")
    private String captchaCode;

}
