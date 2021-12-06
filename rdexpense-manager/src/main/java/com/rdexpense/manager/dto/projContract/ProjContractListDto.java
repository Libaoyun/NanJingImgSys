package com.rdexpense.manager.dto.projContract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "项目合同管理列表")
public class ProjContractListDto extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务Id")
    private String businessId;

    @ApiModelProperty(value = "合同编号")
    private String serialNumber;

    @ApiModelProperty(value = "编制单位Id")
    private String creatorOrgId;

    @ApiModelProperty(value = "编制单位名称")
    private String creatorOrgName;

    @ApiModelProperty(value = "编制人Id")
    private String creatorUserId;

    @ApiModelProperty(value = "编制人名称")
    private String creatorUserName;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String createdDate;

    @ApiModelProperty(value = "项目Id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "所属单位Id")
    private String unitId;

    @ApiModelProperty(value = "所属单位名称")
    private String unitName;

    @ApiModelProperty(value = "项目负责人Id")
    private String projectLeaderId;

    @ApiModelProperty(value = "项目负责人名称")
    private String projectLeaderName;

    @ApiModelProperty(value = "负责人岗位Id")
    private String leaderPostId;

    @ApiModelProperty(value = "负责人岗位名称")
    private String leaderPostName;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String startDate;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String endDate;

    @ApiModelProperty(value = "密级编码")
    private String secretsCode;

    @ApiModelProperty(value = "密级名称")
    private String secretsName;

    @ApiModelProperty(value = "合同状态编码")
    private String contractStatusCode;

    @ApiModelProperty(value = "合同状态")
    private String contractStatusName;
}
