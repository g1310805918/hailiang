package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("客户绑定CSI记录")
@Accessors(chain = true)
public class AccountBindingCSI implements Serializable {
    private static final long serialVersionUID = -8257069085799122144L;

    @ApiModelProperty("用户绑定id")
    private String bindingId;

    @ApiModelProperty("CSI编号")
    private String csiNumber;

    @ApiModelProperty("公司名")
    private String companyName;

    @ApiModelProperty("购买产品名称")
    private String productName;

    @ApiModelProperty("身份【1普通员工、2CAU管理员】")
    private Integer identity;

    @ApiModelProperty("绑定状态【1待审核、2绑定成功、3审核失败】")
    private Integer status;

    @ApiModelProperty("CSI绑定人员列表【仅CAU管理员身份才有值】")
    private List<AccountBindingCSIPersonList> personLists;

}
