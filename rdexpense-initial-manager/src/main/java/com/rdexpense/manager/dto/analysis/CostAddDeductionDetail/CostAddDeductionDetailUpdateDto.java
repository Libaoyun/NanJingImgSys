package com.rdexpense.manager.dto.analysis.CostAddDeductionDetail;

import com.rdexpense.manager.dto.base.BaseEntity;
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
@ApiModel(value = "研发费用加减扣除明细列表")
public class CostAddDeductionDetailUpdateDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "研发立项项目名称")
    private String projectName;

    @ApiModelProperty(value = "开始月份")
    private String startMonth;

    @ApiModelProperty(value = "结束月份")
    private String endMonth;

    @ApiModelProperty(value = "研发费用加减扣除明细")
    private List<CostAddDeductionDetailListDto> costAddDeductionDetailList;

}
