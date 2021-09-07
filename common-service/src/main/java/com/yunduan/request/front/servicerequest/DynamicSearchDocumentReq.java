package com.yunduan.request.front.servicerequest;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("KM文档管理动态搜索文档")
public class DynamicSearchDocumentReq extends RequestBodyReq {

    @ApiModelProperty("类型【1文档编号、2文档标题】")
    private Integer type;

    @ApiModelProperty("搜索的内容")
    private String searchContent;

}
