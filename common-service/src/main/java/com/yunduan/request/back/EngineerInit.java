package com.yunduan.request.back;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EngineerInit extends CommonPage{

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("身份")
    private Integer identity;

    @ApiModelProperty("账号状态")
    private Integer accountStatus;

}
