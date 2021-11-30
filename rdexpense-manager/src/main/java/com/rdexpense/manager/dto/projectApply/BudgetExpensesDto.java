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
@ApiModel(value = "经费支出预算")
public class BudgetExpensesDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "支出预算合计")
    private String totalBudget;

    @ApiModelProperty(value = "一、人员费")
    private String staffCost;

    @ApiModelProperty(value = "二、设备费")
    private String equipmentCost;

    @ApiModelProperty(value = "三、材料费")
    private String materialCost;

    @ApiModelProperty(value = "四、燃料及动力费")
    private String fuelCost;

    @ApiModelProperty(value = "五、测试及化验费")
    private String assayCost;

    @ApiModelProperty(value = "六、差旅费")
    private String travelCost;

    @ApiModelProperty(value = "七、会议费")
    private String meetingCost;

    @ApiModelProperty(value = "八、课题管理费")
    private String managementCost;

    @ApiModelProperty(value = "九、其他费用")
    private String otherCost;

    @ApiModelProperty(value = "1、国际合作交流费")
    private String exchangeCost;

    @ApiModelProperty(value = "2、出版/文献/信息传播")
    private String communicationCost;

    @ApiModelProperty(value = "3、知识产权事务")
    private String propertyCost;

    @ApiModelProperty(value = "4、专家费")
    private String expertCost;

    @ApiModelProperty(value = "5、其他")
    private String other;

    @ApiModelProperty(value = "十、新产品设计费")
    private String designCost;

    @ApiModelProperty(value = "十一、委托研发费用")
    private String expensesCost;

    @ApiModelProperty(value = "类型 1：预算数 2：变更数 3：每月预算(查询时显示)")
    private String status;
}
