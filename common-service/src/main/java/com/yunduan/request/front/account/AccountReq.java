package com.yunduan.request.front.account;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("登录")
public class AccountReq extends RequestBodyReq {

    @ApiModelProperty("手机、邮箱")
    private String mobileOrEmail;

    @ApiModelProperty("密码")
    private String password;
}
