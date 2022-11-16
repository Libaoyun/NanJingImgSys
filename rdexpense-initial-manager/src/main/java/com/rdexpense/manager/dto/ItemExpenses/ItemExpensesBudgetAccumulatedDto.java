package com.rdexpense.manager.dto.ItemExpenses;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "查询预算总额、已支出累计")
public class ItemExpensesBudgetAccumulatedDto implements Serializable {

    @ApiModelProperty(value = "预算总额(18,2)")
    private String budgetAmount;

    @ApiModelProperty(value = "已累计支出(18,2)")
    private String accumulatedExpenditure;

}
