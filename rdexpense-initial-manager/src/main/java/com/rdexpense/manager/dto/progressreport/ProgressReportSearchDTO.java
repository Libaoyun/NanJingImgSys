package com.rdexpense.manager.dto.progressreport;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author dengteng
 * @title: ProgressReportSearchDTO
 * @projectName rdexpense-back
 * @description: 项目进展报告查询入参
 * @date 2021/11/24
 */
@Data
@ApiModel(value = "项目进展报告查询入参")
public class ProgressReportSearchDTO {

    @ApiModelProperty(value = "创建者id")
    private String createUserId;

    @ApiModelProperty(value = "项目编号")
    private String creatorOrgId;

    @ApiModelProperty(value = "单据编号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "审批状态")
    private List<String> processStatus;

    @ApiModelProperty(value = "当前审批人")
    private String approveUserName;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    @ApiModelProperty(value = "岗位")
    private String leaderPostName;

    @ApiModelProperty(value = "联系电话")
    private String contactNumber;

    @ApiModelProperty(value = "所属单位名称")
    private String unitName;

    @ApiModelProperty(value = "起始年度")
    private String startDate;

    @ApiModelProperty(value = "结束年度")
    private String endDate;

    @ApiModelProperty(value = "报告人")
    private String reporterName;

    @ApiModelProperty(value = "报告日期")
    private String reporterDate ;

    @ApiModelProperty(value = "编制人")
    private String reatorUserName;

    @ApiModelProperty(value = "创建日期 ")
    private String create_time ;

    @ApiModelProperty(value = "创建日期 ")
    private String updateTime ;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
