package com.rdexpense.manager.dto.projectApply;

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
@ApiModel(value = "经费预算-每月预算")
public class BudgetMonthDto implements Serializable {

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

    @ApiModelProperty(value = "支出预算合计")
    private String totalExpenseBudget;

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

    @ApiModelProperty(value = "年度预算（按月填报")
    private List<BudgetMonthDetailDto> detailList;


}
