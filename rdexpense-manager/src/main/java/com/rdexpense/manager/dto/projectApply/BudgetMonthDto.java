package com.rdexpense.manager.dto.projectApply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "经费预算-每月预算")
public class BudgetMonthDto implements Serializable {

    @ApiModelProperty(value = "来源科目")
    private String sourceAccount;

    @ApiModelProperty(value = "来源预算数")
    private String sourceBudget;

    @ApiModelProperty(value = "支出科目")
    private String expenseAccount;

    @ApiModelProperty(value = "支出预算数")
    private String expenseBudget;

    @ApiModelProperty(value = "1月")
    private String month_2021_1;

    @ApiModelProperty(value = "2月")
    private String month_2021_2;

    @ApiModelProperty(value = "3月")
    private String month_2021_3;

    @ApiModelProperty(value = "4月")
    private String month_2021_4;

    @ApiModelProperty(value = "5月")
    private String month_2021_5;

    @ApiModelProperty(value = "6月")
    private String month_2021_6;

    @ApiModelProperty(value = "7月")
    private String month_2021_7;

    @ApiModelProperty(value = "8月")
    private String month_2021_8;

    @ApiModelProperty(value = "9月")
    private String month_2021_9;

    @ApiModelProperty(value = "10月")
    private String month_2021_10;

    @ApiModelProperty(value = "11月")
    private String month_2021_11;

    @ApiModelProperty(value = "12月")
    private String month_2021_12;

}
