package com.yunduan.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("设备来源信息")
@Data
public class EquipmentSourceInformation {

    public EquipmentSourceInformation(String equipment, String edition, String uuid, Long timestamp) {
        this.equipment = equipment;
        this.edition = edition;
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    public EquipmentSourceInformation() {
    }

    @ApiModelProperty("来源 1 android 2 ios 3 小程序 4 pc")
    private String equipment;

    @ApiModelProperty("版本")
    private String edition;

    @ApiModelProperty("设备号")
    private String uuid;

    @ApiModelProperty("时间戳")
    private Long timestamp;

}
