package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("CSI下的人员列表")
@Accessors(chain = true)
public class AccountBindingCSIPersonList implements Serializable {
    private static final long serialVersionUID = 409734839916333618L;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("账号id")
    private String accountId;

    @ApiModelProperty("申请时间")
    private String createTime;

    @ApiModelProperty("状态")
    private String status;
}
