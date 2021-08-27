package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@ApiModel("知识文档三级分类")
@Table(name = "tb_knowledge_document_three_category")
@TableName("tb_knowledge_document_three_category")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class KnowledgeDocumentThreeCategory implements Serializable {
    private static final long serialVersionUID = -4306443679548246933L;


    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("所属一级分类id")
    private Long oneCategoryId;

    @ApiModelProperty("所属二级分类id")
    private Long twoCategoryId;

    @ApiModelProperty("三级分类标题")
    private String categoryTitle;

    @TableLogic
    @ApiModelProperty("删除标志")
    private Integer delFlag;

    @ApiModelProperty("添加时间")
    private String createTime;

}
