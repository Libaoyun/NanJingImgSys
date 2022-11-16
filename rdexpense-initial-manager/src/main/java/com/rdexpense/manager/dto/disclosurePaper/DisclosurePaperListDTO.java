package com.rdexpense.manager.dto.disclosurePaper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "项目交底书列表")
public class DisclosurePaperListDTO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "单据编号")
    private  String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private  String projectName;

    @ApiModelProperty(value = "所属单位名称")
    private  String unitName;

    @ApiModelProperty(value = "项目负责人")
    private  String projectLeaderName;

    @ApiModelProperty(value = "岗位")
    private  String leaderPostName;

    @ApiModelProperty(value = "联系电话")
    private  String contactNumber;

    @ApiModelProperty(value = "交底书编制人")
    private  String preparedName;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private  Date preparedDate;

    @ApiModelProperty(value = "编制年度")
    private  String preparedYear;

    @ApiModelProperty(value = "编制季度")
    private  String preparedQuarter;

    @ApiModelProperty(value = "编制人")
    private  String creatorUserName;

    @ApiModelProperty(value = "主要技术")
    private  String keyTechnology;

    @ApiModelProperty(value = "工作安排")
    private  String workPlan;

    @ApiModelProperty(value = "人工安排")
    private  String artificialPlan;

    @ApiModelProperty(value = "设备安排")
    private  String equipmentPlan;

    @ApiModelProperty(value = "费用安排")
    private  String costPlan;

    @ApiModelProperty(value = "其他安排")
    private  String otherPlan;

    @ApiModelProperty(value = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private  Date createTime;

    @ApiModelProperty(value = "更新日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private  Date updateTime;

    @ApiModelProperty(value = "创建人id")
    private  String creatorUserId;

    @ApiModelProperty(value = "更新人")
    private  String updateUser;

    @ApiModelProperty(value = "更新人id")
    private  String updateUserId;
}
