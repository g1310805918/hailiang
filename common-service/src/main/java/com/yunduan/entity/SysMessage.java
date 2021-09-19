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
@TableName("tb_sys_message")
@Table(name = "tb_sys_message")
@Where(clause = "del_flag=0")
@Accessors(chain = true)
@ApiModel(value="系统消息", description="系统消息表")
public class SysMessage implements Serializable {
    private static final long serialVersionUID = 1067137471205772753L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("")
    private String title;

    @ApiModelProperty("")
    private String content;

    @ApiModelProperty("")
    private String createTime = DateUtil.now();

    @TableLogic
    @ApiModelProperty("删除标志")
    private Integer delFlag = StatusCodeUtil.NOT_DELETE_FLAG;

    @ApiModelProperty("更新时间")
    private String updateTime;

}
