package com.rdexpense.manager.dto.attendance;

import com.common.entity.PageData;
import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import com.rdexpense.manager.dto.projectApply.*;
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
@ApiModel(value = "新增人员考勤")
public class AttendanceAddDto extends OrganizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "主键ID(编辑时传参)")
    private String id;

    @ApiModelProperty(value = "业务主键ID(编辑时传参)")
    private String businessId;

    @ApiModelProperty(value = "编制人ID")
    private String  creatorUserId;

    @ApiModelProperty(value = "编制人名称")
    private String  creatorUserName;

    @ApiModelProperty(value = "编制日期")
    private String  createdDate;

    @ApiModelProperty(value = "考勤月份")
    private String attendanceMonth;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeader;

    @ApiModelProperty(value = "项目单位")
    private String unitName;

    @ApiModelProperty(value = "考勤表单位")
    private String  attendUnit;

    @ApiModelProperty(value = "考勤表月份")
    private String  attendDate;

    @ApiModelProperty(value = "考勤表天数")
    private String  attendDays;

    @ApiModelProperty(value = "考勤表人数")
    private String attendPeoples;

    @ApiModelProperty(value = "考勤表课题")
    private String attendProject;

    @ApiModelProperty(value = "工资表单位")
    private String salaryUnit;

    @ApiModelProperty(value = "工资表月份")
    private String salaryDate;

    @ApiModelProperty(value = "工资表项目")
    private String salaryProject;

    @ApiModelProperty(value = "研发人员名单")
    private List<ResearchUsersAddDto> researchUser;

    @ApiModelProperty(value = "研发人员考勤表（传合计）")
    private List<AttendanceListAddDto> attendanceList;

    @ApiModelProperty(value = "研发人员工资表")
    private List<SalaryListDto> salaryList;

    @ApiModelProperty(value = "研发人员分摊表(传合计)")
    private List<ShareListAddDto> shareList;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
