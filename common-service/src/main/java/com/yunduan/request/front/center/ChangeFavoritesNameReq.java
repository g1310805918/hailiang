package com.yunduan.request.front.center;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("修改收藏夹名称")
public class ChangeFavoritesNameReq extends RequestBodyReq {

    @ApiModelProperty("收藏夹id")
    private String id;

    @ApiModelProperty("收藏夹名称")
    private String favoritesName;
}
