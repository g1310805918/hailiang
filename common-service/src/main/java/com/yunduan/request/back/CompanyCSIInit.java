package com.yunduan.request.back;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyCSIInit extends CommonPage{


    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

}
