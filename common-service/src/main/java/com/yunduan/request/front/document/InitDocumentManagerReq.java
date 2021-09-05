package com.yunduan.request.front.document;

import com.yunduan.request.front.RequestPageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("初始化文档管理")
public class InitDocumentManagerReq extends RequestPageReq {

    @ApiModelProperty("我创建的")
    private Boolean myCreate;

    @ApiModelProperty("我发布成功的")
    private Boolean myFb;

    @ApiModelProperty("我收藏的")
    private Boolean myCollection;

    @ApiModelProperty("文档id")
    private String documentId;

    @ApiModelProperty("文档标题")
    private String documentTitle;

    @ApiModelProperty("创建人id")
    private String engineerId;

    @ApiModelProperty("所属分类id")
    private String categoryId;

}
