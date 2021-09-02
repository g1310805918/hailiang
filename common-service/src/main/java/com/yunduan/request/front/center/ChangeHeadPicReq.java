package com.yunduan.request.front.center;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("修改头像")
public class ChangeHeadPicReq extends RequestBodyReq {

    @ApiModelProperty("头像地址")
    private String headPic;
}
