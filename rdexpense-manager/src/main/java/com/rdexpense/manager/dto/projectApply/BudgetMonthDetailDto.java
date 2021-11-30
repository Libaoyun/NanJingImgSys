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
@ApiModel(value = "研究人员")
public class BudgetMonthDetailDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "1月")
    private String january;

    @ApiModelProperty(value = "2月")
    private String february;

    @ApiModelProperty(value = "3月")
    private String march;

    @ApiModelProperty(value = "4月")
    private String april;

    @ApiModelProperty(value = "5月")
    private String may;

    @ApiModelProperty(value = "6月")
    private String june;

    @ApiModelProperty(value = "7月")
    private String july;

    @ApiModelProperty(value = "8月")
    private String august;

    @ApiModelProperty(value = "9月")
    private String september;

    @ApiModelProperty(value = "10月")
    private String october;

    @ApiModelProperty(value = "11月")
    private String november;

    @ApiModelProperty(value = "12月")
    private String december;

    @ApiModelProperty(value = "年份")
    private String years;

}
