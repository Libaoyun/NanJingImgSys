package com.rdexpense.manager.dto.projectApply;

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
@ApiModel(value = "经费支出预算")
public class BudgetDetailDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "来源科目")
    private String sourceAccount;

    @ApiModelProperty(value = "来源预算数")
    private String sourceBudget;

    @ApiModelProperty(value = "来源变更数")
    private String sourceBudgetChange;

    @ApiModelProperty(value = "支出科目")
    private String expenseAccount;

    @ApiModelProperty(value = "支出预算数")
    private String expenseBudget;

    @ApiModelProperty(value = "来源变更数")
    private String expenseBudgetChange;

}
