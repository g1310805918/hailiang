package com.yunduan.vo.back;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchVo implements Serializable {
    private static final long serialVersionUID = 3754654846924453958L;

    @ApiModelProperty(value = "起始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

}
