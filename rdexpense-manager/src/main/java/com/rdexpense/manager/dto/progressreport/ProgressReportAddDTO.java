package com.rdexpense.manager.dto.progressreport;

import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import com.rdexpense.manager.dto.system.user.UserDepartmentDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author dengteng
 * @title: ProgressReportDTO
 * @projectName rdexpense-back
 * @description: 研发项目进展报告新增类
 * @date 2021/11/24
 */
@Data
@ApiModel(value = "研发项目进展报告新增类")
public class ProgressReportAddDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "状态 0为保存 1为提交")
    private String saveStaus;

    @ApiModelProperty(value = "单据编号")
    private String serialNumber;

    @ApiModelProperty(value = "编制人")
    private String creatorUserName;

    @ApiModelProperty(value = "创建日期")
    private String createdDate;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "负责单位（所属单位名称")
    private String unitName;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    @ApiModelProperty(value = "联系电话")
    private String contactNumber;

    @ApiModelProperty(value = "岗位")
    private String leaderPostName;

    @ApiModelProperty(value = "起始年度")
    private String startDate;

    @ApiModelProperty(value = "结束年度")
    private String endDate;

    @ApiModelProperty(value = "报告人")
    private String reporterName;

    @ApiModelProperty(value = "报告日期")
    private String reporterDate;

    @ApiModelProperty(value = "项目概况")
    private String projectOverview;

    @ApiModelProperty(value = "研发过程")
    private String developmentProcess;

    @ApiModelProperty(value = "关键技术及创新")
    private String keyTechnology ;

    @ApiModelProperty(value = "取得的阶段性成果")
    private String achieveResults;

    @ApiModelProperty(value = "经济效益和社会、环保效益")
    private String beneficialResult;

    @ApiModelProperty(value = "成果报告说明")
    private String reportDescription;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
