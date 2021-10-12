package com.yunduan.request.front.center;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ChangeMobileNow extends RequestBodyReq {


    @ApiModelProperty("旧手机号")
    private String oldMobile;

    @ApiModelProperty("新手机号")
    private String newMobile;

    @ApiModelProperty("验证码")
    private String yzm;

}
