package com.yunduan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("沟通记录封装")
@Accessors(chain = true)
public class CommunicationResult implements Serializable {
    private static final long serialVersionUID = 3347991636267456060L;

    @ApiModelProperty("沟通记录id")
    private String recordId;

    @ApiModelProperty("用户是否可见【布尔值】")
    private Boolean isShow;

    @ApiModelProperty("头像")
    private String headPic;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("发送时间")
    private String createTime;

    @ApiModelProperty("VDM标签")
    private String VDMCode;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("错误代码")
    private String errorCode;

    @ApiModelProperty("反馈截图内容")
    private List<String> descImage;

}
