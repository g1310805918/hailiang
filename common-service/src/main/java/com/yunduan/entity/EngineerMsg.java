package com.yunduan.entity;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Entity
@ApiModel("工程师消息表")
@Table(name = "tb_engineer_msg")
@TableName("tb_engineer_msg")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class EngineerMsg implements Serializable {
    private static final long serialVersionUID = -1833796751993827763L;


    @Id
    @TableId(type = IdType.NONE)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @ApiModelProperty("主键id")
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("消息类型【1工单消息、2文档消息、3bug消息、4系统消息】")
    private Integer msgType;

    @ApiModelProperty("接收消息的工程师id")
    private Long engineerId;

    @ApiModelProperty("消息类型对应的id【1工单id、2文档id、3bugID】")
    private Long typeId;

    @ApiModelProperty("消息标题")
    private String msgTitle;

    @ApiModelProperty("消息内容")
    private String msgContent;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("添加时间、发送消息时间")
    private String createTime = DateUtil.now();

    @ApiModelProperty("是否已读【0否、1是】")
    private Integer isRead = StatusCodeUtil.MESSAGE_NO_READ_FLAG;

    @TableLogic
    @ApiModelProperty("是否删除【0否、1是】")
    private Integer delFlag = StatusCodeUtil.NOT_DELETE_FLAG;


}
