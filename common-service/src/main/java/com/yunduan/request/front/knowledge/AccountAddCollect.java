package com.yunduan.request.front.knowledge;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountAddCollect extends RequestBodyReq {

    @ApiModelProperty("文档id")
    private String docId;

    @ApiModelProperty("收藏夹id【取消收藏时为空、默认收藏夹也为空】")
    private String favoritesId;

}
