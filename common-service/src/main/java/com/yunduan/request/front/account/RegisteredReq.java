package com.yunduan.request.front.account;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("注册用户")
public class RegisteredReq extends RequestBodyReq {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("短信验证码（6位数）")
    private String yzm;

    @ApiModelProperty("客户服务号（CSI）")
    private String CSINumber;

}
