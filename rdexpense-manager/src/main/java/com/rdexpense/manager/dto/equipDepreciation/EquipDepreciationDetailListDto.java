package com.rdexpense.manager.dto.equipDepreciation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "设备折旧单明细列表")
// 设备折旧单列表展示
public class EquipDepreciationDetailListDto implements Serializable {
    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务ID")
    private String businessId;

    @ApiModelProperty(value = "明细序列号")
    private String serialNumber;

    @ApiModelProperty(value = "类别编号")
    private String CategoryCode;

    @ApiModelProperty(value = "资产编号")
    private String assetCode;

    @ApiModelProperty(value = "设备名称")
    private String equipName;

    @ApiModelProperty(value = "设备原值")
    private float equipOriginalValue;

    @ApiModelProperty(value = "购入日期")
    private Date equipAcquisitionDate;

    @ApiModelProperty(value = "本期折旧")
    private float periodDepreciation;

    @ApiModelProperty(value = "累积折旧")
    private float accumulatedDepreciation;

    @ApiModelProperty(value = "本年计提折旧")
    private float accrualDepreciation;

    @ApiModelProperty(value = "本年累积发生")
    private float annualAccumulation;

    @ApiModelProperty(value = "资产原值")
    private float assetOriginalValue;

    @ApiModelProperty(value = "资产原值")
    private int assetNumber;

    @ApiModelProperty(value = "资产净值")
    private float assetNetValue;

    @ApiModelProperty(value = "资产净值")
    private float assetNetResidualValue;

    @ApiModelProperty(value = "单位编号")
    private float unitCode;

    @ApiModelProperty(value = "使用单位")
    private float userUnitName;

    @ApiModelProperty(value = "使用人")
    private float userName;

    @ApiModelProperty(value = "编制人ID")
    private String creatorUserId;

    @ApiModelProperty(value = "编制人")
    private String creatorUser;

    @ApiModelProperty(value = "编制时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
