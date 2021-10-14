package com.yunduan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yunduan.config.LongJsonDeserializer;
import com.yunduan.config.LongJsonSerializer;
import com.yunduan.config.ToStringSerializer;
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
@ApiModel("知识文档")
@Table(name = "tb_knowledge_document")
@TableName("tb_knowledge_document")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class KnowledgeDocument implements Serializable {
    private static final long serialVersionUID = -5347761263924199279L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("发布工程师id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long engineerId;

    @ApiModelProperty("三级分类id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long threeCategoryId;

    @ApiModelProperty("文档编号")
    private String docNumber;

    @ApiModelProperty("文档标题")
    private String docTitle;

    @ApiModelProperty("文档类型【1知识文档、2bug文档】")
    private Integer docType;

    @ApiModelProperty("对内对外可见【1对外、2对内】")
    private Integer isShow;

    @ApiModelProperty("知识文档内容【富文本】")
    private String docContent;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("添加时间")
    private String createTime;

    @TableLogic
    @ApiModelProperty("删除标志")
    private Integer delFlag;
}
