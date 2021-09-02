package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@ApiModel("用户消息表")
@Table(name = "tb_account_msg")
@TableName("tb_account_msg")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class AccountMsg implements Serializable {
    private static final long serialVersionUID = 1172675463407515196L;


    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    @ApiModelProperty("用户id")
    private Long accountId;

    @ApiModelProperty("消息标题")
    private String msgTitle;

    @ApiModelProperty("消息内容")
    private String msgContent;

    @ApiModelProperty("消息类型【1系统消息【富文本】、2验证消息{json}字符串】")
    private Integer msgType;

    @ApiModelProperty("是否已读【0否、1是】")
    private Integer isRead;

    @TableLogic
    @ApiModelProperty("删除标志【0否、1是】")
    private Integer delFlag;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("添加时间")
    private String createTime;
}
