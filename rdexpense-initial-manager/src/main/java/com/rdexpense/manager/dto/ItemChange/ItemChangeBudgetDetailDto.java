package com.rdexpense.manager.dto.ItemChange;

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
@ApiModel(value = "研发项目变更经费支出预算")
public class ItemChangeBudgetDetailDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "来源科目")
    private String sourceAccount;

    @ApiModelProperty(value = "变更前，来源预算数")
    private String sourceBudget;

    @ApiModelProperty(value = "变更后，来源变更数")
    private String sourceBudgetChange;

    @ApiModelProperty(value = "支出科目")
    private String expenseAccount;

    @ApiModelProperty(value = "变更前，支出预算数")
    private String expenseBudget;

    @ApiModelProperty(value = "变更后，来源变更数")
    private String expenseBudgetChange;

    @ApiModelProperty(value = "支出科目编码")
    private String expenseAccountCode;

}
