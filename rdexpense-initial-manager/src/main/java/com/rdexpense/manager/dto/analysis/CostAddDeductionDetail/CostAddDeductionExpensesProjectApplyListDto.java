
package com.rdexpense.manager.dto.analysis.CostAddDeductionDetail;

import com.rdexpense.manager.dto.base.BaseEntity;
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
@ApiModel(value = "查询研发费用支出已审批完成的研发项目申请列表")
public class CostAddDeductionExpensesProjectApplyListDto  implements Serializable {

    @ApiModelProperty(value = "右上角项目ID", required = true)
    private String creatorOrgId;

    @ApiModelProperty(value = "右上角项目名称", required = true)
    private String creatorOrgName;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "项目名称(搜索时需要传此参数)")
    private String projectName;


}

