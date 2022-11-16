package com.rdexpense.manager.dto.analysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
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
@ApiModel(value = "项目支出辅助账查询列表")
public class ExpensesBillListDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "年度")
    private String years;

    @ApiModelProperty(value = "完成情况")
    private String completionStatus;

    @ApiModelProperty(value = "完成情况编码")
    private String completionStatusCode;

    @ApiModelProperty(value = "支出类型")
    private String expensesType;

    @ApiModelProperty(value = "支出类型编码")
    private String expensesTypeCode;

    @ApiModelProperty(value = "合计金额")
    private String amount;


}
