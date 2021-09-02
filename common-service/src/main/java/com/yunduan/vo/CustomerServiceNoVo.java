package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("客户服务号列表")
@Accessors(chain = true)
public class CustomerServiceNoVo implements Serializable {
    private static final long serialVersionUID = 5195452850851029589L;

    @ApiModelProperty("客户服务号")
    private String csiNumber;

}
