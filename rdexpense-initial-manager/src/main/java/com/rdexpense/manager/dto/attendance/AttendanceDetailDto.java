package com.rdexpense.manager.dto.attendance;

import com.common.entity.PageData;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(value = "人员考勤详情")
public class AttendanceDetailDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "右上角项目ID", required = true)
    private String creatorOrgId;

    @ApiModelProperty(value = "右上角项目名称", required = true)
    private String creatorOrgName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "创建人ID")
    private String createUserId;

    @ApiModelProperty(value = "修改人ID")
    private String updateUserId;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createdDate;

    @ApiModelProperty(value = "考勤月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date attendanceMonth;

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
    private List<ResearchUsersDetailDto> researchUser;

    @ApiModelProperty(value = "研发人员考勤表")
    private List<PageData> attendanceList;

    @ApiModelProperty(value = "研发人员工资表")
    private List<SalaryListDto> salaryList;

    @ApiModelProperty(value = "研发人员分摊表")
    private List<PageData> shareList;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
