package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("其他全部受理人")
@Accessors(chain = true)
public class OtherEngineerListVo implements Serializable {
    private static final long serialVersionUID = -9169474083515241427L;

    @ApiModelProperty("工程师id")
    private String engineerId;

    @ApiModelProperty("工程师名称")
    private String username;

}
