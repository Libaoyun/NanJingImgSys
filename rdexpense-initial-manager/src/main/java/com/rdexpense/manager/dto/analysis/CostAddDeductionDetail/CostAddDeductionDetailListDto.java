
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
@ApiModel(value = "研发费用加减扣除明细列表")
public class CostAddDeductionDetailListDto implements Serializable {

    @ApiModelProperty(value = "研发项目业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "研发立项项目名称")
    private String projectName;

    @ApiModelProperty(value = "开始月份")
    private String startMonth;

    @ApiModelProperty(value = "结束月份")
    private String endMonth;

    @ApiModelProperty(value = "项目编码")
    private String expenseAccountCode;

    @ApiModelProperty(value = "项目")
    private String expenseAccount;

    @ApiModelProperty(value = "金额(长度，18：2)")
    private String expenseCost;

}

