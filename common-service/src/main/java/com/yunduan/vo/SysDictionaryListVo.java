package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel("数据字典")
@Accessors(chain = true)
public class SysDictionaryListVo implements Serializable {
    private static final long serialVersionUID = 2150462359942569127L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("内容")
    private String content;

}
