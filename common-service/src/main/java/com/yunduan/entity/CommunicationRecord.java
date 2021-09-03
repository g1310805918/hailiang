package com.yunduan.entity;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@ApiModel("工单沟通记录表")
@Table(name = "tb_communication_record")
@TableName("tb_communication_record")
@Accessors(chain = true)
public class CommunicationRecord implements Serializable {
    private static final long serialVersionUID = -891402970227265673L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("工单id")
    private Long workOrderId;

    @ApiModelProperty("用户id")
    private Long accountId;

    @ApiModelProperty("工程师id")
    private Long engineerId;

    @ApiModelProperty("VDM标签")
    private String codeFlag;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("是否用户可见【0否、1是】")
    private Integer isShow;

    @ApiModelProperty("发送时间")
    private String createTime = DateUtil.now();

    @ApiModelProperty("反馈图片列表【逗号分割】")
    private String descImage;

}
