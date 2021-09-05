package com.yunduan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@ApiModel("知识文档")
@Table(name = "tb_knowledge_document_no_pass")
@TableName("tb_knowledge_document_no_pass")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class KnowledgeDocumentNoPass extends KnowledgeDocument {
    private static final long serialVersionUID = -3806921325145450171L;


    @ApiModelProperty("文档状态【1待审核、2审核通过、3审核拒绝】")
    private Integer docStatus;

}
