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


@Data
@Entity
@ApiModel("知识文档二级分类")
@Table(name = "tb_knowledge_document_two_category")
@TableName("tb_knowledge_document_two_category")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class KnowledgeDocumentTwoCategory implements Serializable {
    private static final long serialVersionUID = 3759434855728752459L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    @ApiModelProperty("一级分类id")
    private Long oneCategoryId;

    @ApiModelProperty("二级分类标题")
    private String categoryTitle;

    @TableLogic
    @ApiModelProperty("删除标志")
    private Integer delFlag;

    @ApiModelProperty("添加时间")
    private String createTime;
}
