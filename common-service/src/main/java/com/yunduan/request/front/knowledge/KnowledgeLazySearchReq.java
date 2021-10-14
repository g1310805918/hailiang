package com.yunduan.request.front.knowledge;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class KnowledgeLazySearchReq extends RequestBodyReq {

    @ApiModelProperty("模糊搜索名称")
    private String searchContent;

}
