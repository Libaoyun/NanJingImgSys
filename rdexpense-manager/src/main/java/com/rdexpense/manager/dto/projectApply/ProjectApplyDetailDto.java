package com.rdexpense.manager.dto.projectApply;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:00
 */
@Data
@ApiModel(value = "项目立项申请详情")
public class ProjectApplyDetailDto extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编制日期")
    private String  createdDate;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "单位地址")
    private String unitAddress;

    @ApiModelProperty(value = "申请人")
    private String applyUserName;

    @ApiModelProperty(value = "申请人ID")
    private String applyUserId;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "性别编码")
    private String genderCode;

    @ApiModelProperty(value = "年龄")
    private String age;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "职务编码")
    private String postCode;

    @ApiModelProperty(value = "电话号码")
    private String telephone;

    @ApiModelProperty(value = "申请经费")
    private String applyAmount;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

    @ApiModelProperty(value = "项目类型编码")
    private String projectTypeCode;

    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "专业类别编码")
    private String professionalCategoryCode;

    @ApiModelProperty(value = "专业类别")
    private String professionalCategory;

    @ApiModelProperty(value = "是否鉴定 0：否1：是")
    private String identify;

    @ApiModelProperty(value = "研究内容")
    private String researchContents;

    @ApiModelProperty(value = "审查意见")
    private String reviewComments;

    @ApiModelProperty(value = "国内外现状")
    private String currentSituation;

    @ApiModelProperty(value = "研发目的和意义")
    private String purposeSignificance;

    @ApiModelProperty(value = "主要研究内容及研究方法")
    private String contentMethod;

    @ApiModelProperty(value = "要达到的目标、成果形式及主要技术指标")
    private String targetResults;

    @ApiModelProperty(value = "现有研发条件和工作基础")
    private String basicConditions;

    @ApiModelProperty(value = "研发项目创新点")
    private String innovationPoints;

    @ApiModelProperty(value = "成果转化的可行性分析")
    private String feasibilityAnalysis;

    @ApiModelProperty(value = "进度计划")
    private List<ProgressPlanDetailDto> progressPlan;

    @ApiModelProperty(value = "参加单位")
    private List<AttendUnitDetailDto> attendUnit;

    @ApiModelProperty(value = "研究人员（初始）")
    private List<ResearchUserDetailDto> researchUser;

    @ApiModelProperty(value = "研究人员（变更）")
    private List<ResearchUserDetailDto> researchUserChange;

    @ApiModelProperty(value = "经费来源预算(只有一个元素)")
    private List<BudgetSourceDto> budgetSource;

    @ApiModelProperty(value = "经费支出预算(只有一个元素)")
    private List<BudgetExpensesDto> budgetExpenses;

    @ApiModelProperty(value = "来源预算合计(经费预算-每月预算)")
    private String totalSourceBudget;

    @ApiModelProperty(value = "一、股份公司计划拨款(经费预算-每月预算)")
    private String companyAppropriation;

    @ApiModelProperty(value = "二、国家拨款(经费预算-每月预算)")
    private String stateAppropriation;

    @ApiModelProperty(value = "三、省市拨款(经费预算-每月预算)")
    private String provincesAppropriation;

    @ApiModelProperty(value = "四、单位自筹款(经费预算-每月预算)")
    private String unitFunds;

    @ApiModelProperty(value = "五、银行贷款(经费预算-每月预算)")
    private String bankLoans;

    @ApiModelProperty(value = "六、其他来源款(经费预算-每月预算)")
    private String otherSource;

    @ApiModelProperty(value = "支出预算合计(经费预算-每月预算)")
    private String totalExpenseBudget;

    @ApiModelProperty(value = "一、人员费(经费预算-每月预算)")
    private String staffCost;

    @ApiModelProperty(value = "二、设备费(经费预算-每月预算)")
    private String equipmentCost;

    @ApiModelProperty(value = "三、材料费(经费预算-每月预算)")
    private String materialCost;

    @ApiModelProperty(value = "四、燃料及动力费(经费预算-每月预算)")
    private String fuelCost;

    @ApiModelProperty(value = "五、测试及化验费(经费预算-每月预算)")
    private String assayCost;

    @ApiModelProperty(value = "六、差旅费(经费预算-每月预算)")
    private String travelCost;

    @ApiModelProperty(value = "七、会议费(经费预算-每月预算)")
    private String meetingCost;

    @ApiModelProperty(value = "八、课题管理费(经费预算-每月预算)")
    private String managementCost;

    @ApiModelProperty(value = "九、其他费用(经费预算-每月预算)")
    private String otherCost;

    @ApiModelProperty(value = "1、国际合作交流费(经费预算-每月预算)")
    private String exchangeCost;

    @ApiModelProperty(value = "2、出版/文献/信息传播(经费预算-每月预算)")
    private String communicationCost;

    @ApiModelProperty(value = "3、知识产权事务(经费预算-每月预算)")
    private String propertyCost;

    @ApiModelProperty(value = "4、专家费(经费预算-每月预算)")
    private String expertCost;

    @ApiModelProperty(value = "5、其他(经费预算-每月预算)")
    private String other;

    @ApiModelProperty(value = "十、新产品设计费(经费预算-每月预算)")
    private String designCost;

    @ApiModelProperty(value = "十一、委托研发费用(经费预算-每月预算)")
    private String expensesCost;

    @ApiModelProperty(value = "年度预算（按月填报")
    private List<BudgetMonthDetailDto> monthList;

    @ApiModelProperty(value = "拨款计划")
    private List<AppropriationPlanDetailDto> appropriationPlan;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
