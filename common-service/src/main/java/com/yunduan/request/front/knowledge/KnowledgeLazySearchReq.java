package com.yunduan.request.front.knowledge;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("模糊搜索")
public class KnowledgeLazySearchReq extends RequestBodyReq {

    @ApiModelProperty("模糊搜索名称")
    private String searchContent;

    @ApiModelProperty("对内、对外可见【随便填入表示是从工程师端搜索、空不填表示从用户端搜索】")
    private String nullStr;

}
