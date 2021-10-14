package com.yunduan.request.front.knowledge;

import com.yunduan.request.front.RequestBodyReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class AddKnowledgeReq extends RequestBodyReq {

    @ApiModelProperty("文档标志【normal表示普通知识文档、bug表示BUG文档】必填，默认normal")
    private String docFlag = "normal";

    @ApiModelProperty("文档id【必填】")
    private String docId;

    @ApiModelProperty("文档标题【必填】")
    private String docTitle;

}
