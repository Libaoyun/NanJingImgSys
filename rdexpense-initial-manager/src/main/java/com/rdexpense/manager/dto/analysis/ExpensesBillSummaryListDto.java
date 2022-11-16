package com.rdexpense.manager.dto.analysis;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "项目支出辅助账汇总表查询列表")
public class ExpensesBillSummaryListDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "完成情况")
    private String completionStatus;

    @ApiModelProperty(value = "支出类型")
    private String expensesType;

    @ApiModelProperty(value = "允许加计扣除金额合计")
    private String totalAmount;

    @ApiModelProperty(value = "人员人工费用")
    private String userAmount;

    @ApiModelProperty(value = "直接投入费用")
    private String inputAmount;

    @ApiModelProperty(value = "折旧费用")
    private String depreciationAmount;

    @ApiModelProperty(value = "无形资产摊销")
    private String amortizationAmount;

    @ApiModelProperty(value = "新产品设计费等")
    private String desginAmount;

    @ApiModelProperty(value = "前五项小计")
    private String itemSumAmount;

    @ApiModelProperty(value = "其他相关费用合计")
    private String otherAmount;

    @ApiModelProperty(value = "经限额调整后的其他相关费用")
    private String adjustOtherAmount;

    @ApiModelProperty(value = "境内活动费")
    private String territoryAmount;

    @ApiModelProperty(value = "允许加计后的境内活动费")
    private String adjustTerritoryAmount;

    @ApiModelProperty(value = "境外活动费")
    private String abroadAmount;

    @ApiModelProperty(value = "调整后的境外活动费")
    private String adjustAbroadAmount;

}
