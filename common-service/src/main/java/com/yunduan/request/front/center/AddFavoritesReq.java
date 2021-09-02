package com.yunduan.request.front.center;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("添加收藏夹")
public class AddFavoritesReq extends RequestBodyReq {

    @ApiModelProperty("收藏夹名称")
    private String favoritesName;
}
