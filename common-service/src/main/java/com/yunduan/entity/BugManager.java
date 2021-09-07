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
@ApiModel("bug审核管理表")
@Table(name = "tb_bug_manager")
@TableName("tb_bug_manager")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class BugManager implements Serializable {
    private static final long serialVersionUID = 6236910677599095967L;


    @Id
    @TableId(type = IdType.NONE)
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    @ApiModelProperty("主键id")
    private Long id = SnowFlakeUtil.getPrimaryKeyId();

    @ApiModelProperty("关联的工单id")
    private Long workOrderId;

    @ApiModelProperty("工程师id")
    private Long engineerId;

    @ApiModelProperty("所属分类id")
    private Long categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("bug标题")
    private String bugTitle;

    @ApiModelProperty("关联的工单编号")
    private String outTradeNo;

    @ApiModelProperty("bug具体内容【富文本】")
    private String bugContent;

    @ApiModelProperty("bug文档状态【1待审核、2审核通过、3拒绝】")
    private Integer bugStatus = StatusCodeUtil.BUG_DOC_NO_REVIEW_STATUS;

    @ApiModelProperty("添加时间")
    private String createTime = DateUtil.now();

    @TableLogic
    @ApiModelProperty("删除标志")
    private Integer delFlag = StatusCodeUtil.NOT_DELETE_FLAG;

    @ApiModelProperty("拒绝原因")
    private String refusedReason;

    @ApiModelProperty("附件地址")
    private String attachmentPath;

    @ApiModelProperty("附件描述")
    private String attachmentDescription;

    @ApiModelProperty("tabd反馈结果")
    private String tabdFeedback;

}
