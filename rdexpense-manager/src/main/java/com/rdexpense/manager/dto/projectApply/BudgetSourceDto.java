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
@ApiModel(value = "经费来源预算")
public class BudgetSourceDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "来源预算合计")
    private String totalSourceBudget;

    @ApiModelProperty(value = "一、股份公司计划拨款")
    private String companyAppropriation;

    @ApiModelProperty(value = "二、国家拨款")
    private String stateAppropriation;

    @ApiModelProperty(value = "三、省市拨款")
    private String provincesAppropriation;

    @ApiModelProperty(value = "四、单位自筹款")
    private String unitFunds;

    @ApiModelProperty(value = "五、银行贷款")
    private String bankLoans;

    @ApiModelProperty(value = "六、其他来源款")
    private String otherSource;

    @ApiModelProperty(value = "类型 1：预算数 2：变更数 3：每月预算(查询时显示)")
    private String status;
}
