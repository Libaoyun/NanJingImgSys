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
@ApiModel(value = "新增编辑经费预算")
public class BudgetAddDto implements Serializable {

    @ApiModelProperty(value = "来源科目")
    private String sourceAccount;

    @ApiModelProperty(value = "来源预算数")
    private String sourceBudget;

    @ApiModelProperty(value = "支出科目")
    private String expenseAccount;

    @ApiModelProperty(value = "支出预算数")
    private String expenseBudget;

}
