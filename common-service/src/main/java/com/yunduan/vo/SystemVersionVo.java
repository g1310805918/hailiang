package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("系统版本")
@Accessors(chain = true)
public class SystemVersionVo implements Serializable {
    private static final long serialVersionUID = -4161667806240247834L;

    @ApiModelProperty("版本id")
    private String id;

    @ApiModelProperty("版本")
    private String content;
}
