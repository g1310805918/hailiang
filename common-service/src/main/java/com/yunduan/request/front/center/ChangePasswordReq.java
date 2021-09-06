package com.yunduan.request.front.center;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改密码")
public class ChangePasswordReq extends RequestBodyReq {

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("新密码")
    private String password;
}
