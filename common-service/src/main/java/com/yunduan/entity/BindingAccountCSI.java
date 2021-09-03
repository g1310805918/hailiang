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
@ApiModel("用户绑定CSI记录表")
@Table(name = "tb_binding_account_csi")
@TableName("tb_binding_account_csi")
@Accessors(chain = true)
public class BindingAccountCSI implements Serializable {
    private static final long serialVersionUID = -6931898023411026357L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("主键id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    @ApiModelProperty("用户id")
    private Long accountId;

    @ApiModelProperty("客户CSI表id")
    private Long csiId;

    @ApiModelProperty("身份【1普通员工、2CAU管理员】")
    private Integer identity;

    @ApiModelProperty("状态【1待审核、2绑定成功、3审核失败】")
    private Integer status;

    @ApiModelProperty("添加时间")
    private String createTime;

}
