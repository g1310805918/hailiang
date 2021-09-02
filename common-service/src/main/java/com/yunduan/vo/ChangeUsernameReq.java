package com.yunduan.vo;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("编辑用户名")
public class ChangeUsernameReq extends RequestBodyReq {

    @ApiModelProperty("用户名")
    private String username;
}
