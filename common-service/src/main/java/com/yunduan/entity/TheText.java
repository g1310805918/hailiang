package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@ApiModel("协议表")
@Table(name = "tb_the_text")
@TableName("tb_the_text")
@Accessors(chain = true)
public class TheText implements Serializable {

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("文本标题")
    private String codeTitle;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("添加时间")
    private Date createTime;
}
