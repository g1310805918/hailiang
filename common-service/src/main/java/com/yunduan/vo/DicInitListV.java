package com.yunduan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
public class DicInitListV implements Serializable {
    private static final long serialVersionUID = 8725003160028166575L;


    @ApiModelProperty("标签名")
    private String codeName;

    @ApiModelProperty("子类名称")
    private String content;

}
