package com.yunduan.request.front.center;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户收藏夹信息")
public class FavoritesReq extends RequestPageReq {

    @ApiModelProperty("收藏夹id")
    private String favoritesId;
}
