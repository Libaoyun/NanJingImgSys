package com.rdexpense.manager.dto.progressreport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dengteng
 * @title: ProgressReportListDTO
 * @projectName rdexpense-back
 * @description: 项目进展报告列表展示类
 * @date 2021/11/24
 */
@Data
@ApiModel(description = "项目进展报告列表展示类")
public class ProgressReportListDTO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;


    @ApiModelProperty(value = "单据编号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "审批状态")
    private String processStatus;

    @ApiModelProperty(value = "审批状态")
    private String processName;

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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "报告人")
    private String reporterName;

    @ApiModelProperty(value = "报告日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date reporterDate ;

    @ApiModelProperty(value = "编制人id")
    private String creatorUserId;

    @ApiModelProperty(value = "编制人")
    private String creatorUserName;

    @ApiModelProperty(value = "创建日期 ")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime ;

    @ApiModelProperty(value = "创建日期 ")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime ;

}
