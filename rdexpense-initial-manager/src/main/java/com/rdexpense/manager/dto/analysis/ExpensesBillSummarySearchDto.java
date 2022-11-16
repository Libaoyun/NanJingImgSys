package com.rdexpense.manager.dto.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "项目支出辅助账汇总表查询入参")
public class ExpensesBillSummarySearchDto implements Serializable {

    @ApiModelProperty(value = "右上角项目ID", required = true)
    private String creatorOrgId;

    @ApiModelProperty(value = "右上角项目名称", required = true)
    private String creatorOrgName;

    @ApiModelProperty(value = "年度", required = true)
    private String years;

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

}
