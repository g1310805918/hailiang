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
@ApiModel("客户CSI表")
@Table(name = "tb_company_csi")
@TableName("tb_company_csi")
@Accessors(chain = true)
@Where(clause = "del_flag = 0")
public class CompanyCSI implements Serializable {
    private static final long serialVersionUID = 4227538793300665829L;

    @Id
    @TableId(type = IdType.NONE)
    @ApiModelProperty("id")
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("购买产品名称")
    private String productName;

    @ApiModelProperty("CAU手机号")
    private String cauMobile;

    @ApiModelProperty("CAU邮箱")
    private String cauEmail;

    @ApiModelProperty("CSI编号")
    private String csiNumber;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("添加时间")
    private String createTime;

    @TableLogic
    @ApiModelProperty("删除标志")
    private Integer delFlag;

}
