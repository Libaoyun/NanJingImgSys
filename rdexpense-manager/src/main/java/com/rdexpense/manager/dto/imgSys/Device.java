package com.rdexpense.manager.dto.imgSys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 科室设备信息
 * @author: Libaoyun
 * @date: 2022-10-19 19:14
 **/
@Data
@ApiModel("科室设备信息")
public class Device implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("设备资产编码")
    private String deviceCode;

    @ApiModelProperty("设备序列号（设备Mac地址）")
    private String deviceSerialNum;

    @ApiModelProperty("设备类型")
    private String deviceType;

    @ApiModelProperty("设备功能描述")
    private String funcDescrip;

    @ApiModelProperty("设备所属部门")
    private String departmentCode;

    @ApiModelProperty("设备型号")
    private String deviceModel;

    @ApiModelProperty("设备状态")
    private String deviceStatus;

    @ApiModelProperty("设备可检查部位")
    List<BodyPart> bodyParts;

}
