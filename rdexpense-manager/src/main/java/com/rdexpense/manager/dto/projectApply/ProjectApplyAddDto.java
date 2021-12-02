package com.rdexpense.manager.dto.projectApply;

import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:00
 */
@Data
@ApiModel(value = "新增项目立项申请")
public class ProjectApplyAddDto extends OrganizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "主键ID(编辑时传参)")
    private String id;

    @ApiModelProperty(value = "业务主键ID(编辑时传参)")
    private String businessId;

    @ApiModelProperty(value = "操作类型 1:保存 2:提交")
    private String operationType;

    @ApiModelProperty(value = "编制人ID")
    private String  creatorUserId;

    @ApiModelProperty(value = "编制人名称")
    private String  creatorUserName;

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
    private String startYear;

    @ApiModelProperty(value = "结束年度")
    private String endYear;

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
    private List<ProgressPlanDto> progressPlan;

    @ApiModelProperty(value = "参加单位")
    private List<AttendUnitDto> attendUnit;

    @ApiModelProperty(value = "研究人员")
    private List<ResearchUserDto> researchUser;

    @ApiModelProperty(value = "经费预算")
    private List<BudgetAddDto> budgetList;

    @ApiModelProperty(value = "经费预算（每月预算）")
    private List<BudgetMonthDto> monthList;

    @ApiModelProperty(value = "拨款计划")
    private List<AppropriationPlanDto> appropriationPlan;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
