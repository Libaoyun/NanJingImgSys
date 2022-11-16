
package com.rdexpense.manager.dto.analysis.CostAddDeductionDetail;

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
@ApiModel(value = "查询研发费用支出已审批完成的最大月份值")
public class ExpensesMaxMonthDto implements Serializable {

    @ApiModelProperty(value = "最大月份值")
    private String belongingMonth;

}

