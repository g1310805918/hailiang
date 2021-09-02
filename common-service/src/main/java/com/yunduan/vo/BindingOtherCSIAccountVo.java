package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("绑定CSI下的其他用户列表")
@Accessors(chain = true)
public class BindingOtherCSIAccountVo implements Serializable {
    private static final long serialVersionUID = -4539864866757195513L;

    @ApiModelProperty("账号id")
    private String accountId;

    @ApiModelProperty("用户名")
    private String username;

}
