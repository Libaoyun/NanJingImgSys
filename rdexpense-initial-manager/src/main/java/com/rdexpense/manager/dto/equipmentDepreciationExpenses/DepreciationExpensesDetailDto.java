package com.rdexpense.manager.dto.equipmentDepreciationExpenses;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "设备折旧支出单明细")
public class DepreciationExpensesDetailDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String Id;

    @ApiModelProperty(value = "业务主键ID(编辑时传参)")
    private String businessId;

    @ApiModelProperty(value = "类型编码(长度：128)")
    private String typeCode;

    @ApiModelProperty(value = "资产编号(长度：128)")
    private String financialCode;

    @ApiModelProperty(value = "设备名称编码(长度：128)")
    private String equipCode;

    @ApiModelProperty(value = "设备名称(长度：128)")
    private String equipmentName;

    @ApiModelProperty(value = "设备原值（元）(长度：24:2)")
    private String originalValue;

    @ApiModelProperty(value = "购入日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date dateIn;

    @ApiModelProperty(value = "本期折旧金额（元(长度：24:2)")
    private String thisMonthDepreciation;

    @ApiModelProperty(value = "累计折旧(长度：24:2)")
    private String accumulatedDepreciation;

    @ApiModelProperty(value = "本年计提折旧（元）(长度：24:2)")
    private String thisYearDepreciation;

    @ApiModelProperty(value = "本年累计发生(长度：24:2)")
    private String thisYearAccumulated;

    @ApiModelProperty(value = "资产原值(长度：24:2)")
    private String assetOriginalValue;

    @ApiModelProperty(value = "资产数量(长度：24:2)")
    private String assetNumber;

    @ApiModelProperty(value = "资产净值(长度：24:2)")
    private String assetNetValue;

    @ApiModelProperty(value = "净残值(长度：24:2)")
    private String residualValue;

    @ApiModelProperty(value = "单位编码(长度：255)")
    private String orgCode;

    @ApiModelProperty(value = "使用单位(长度：255)")
    private String orgUse;

    @ApiModelProperty(value = "使用部门(长度：100)")
    private String useDepartment;

    @ApiModelProperty(value = "使用部门编码(长度：100)")
    private String useDepartmentCode;

    @ApiModelProperty(value = "导入模块数据标识，0：导入数据；1：页面新增数据")
    private String paramSource;
}
