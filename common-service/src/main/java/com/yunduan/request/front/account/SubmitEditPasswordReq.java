package com.yunduan.request.front.account;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("提交修改密码")
public class SubmitEditPasswordReq extends RequestBodyReq {

    @ApiModelProperty("手机号、邮箱")
    private String mobile;

    @ApiModelProperty("新密码")
    private String afterPassword;


}
