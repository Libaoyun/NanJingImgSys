package com.rdexpense.manager.dto.analysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "立项项目经费支出预算")
public class ProjectBudgetDto implements Serializable {

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "项目ID")
    private String creatorOrgId;

    @ApiModelProperty(value = "项目编号")
    private String serialNumber;

    @ApiModelProperty(value = "实际金额")
    private String actualPay;

    @ApiModelProperty(value = "支出金额")
    private String expenseBudget;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;


}
