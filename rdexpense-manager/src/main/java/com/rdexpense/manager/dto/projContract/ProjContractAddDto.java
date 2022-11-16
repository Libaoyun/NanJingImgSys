package com.rdexpense.manager.dto.projContract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "添加项目合同入参")
public class ProjContractAddDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "合同编号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称", required = true)
    private String projectName;

    @ApiModelProperty(value = "编制单位ID")
    private String CreatorOrgId;

    @ApiModelProperty(value = "编制单位名称")
    private String CreatorOrgName;

    @ApiModelProperty(value = "编制人ID")
    private String CreatorUserId;

    @ApiModelProperty(value = "编制人名称")
    private String CreatorUserName;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String createdDate;

    @ApiModelProperty(value = "所属单位名称")
    private String unitName;

    @ApiModelProperty(value = "项目负责人ID")
    private String projectLeaderId;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    @ApiModelProperty(value = "负责人岗位ID")
    private String leaderPostId;

    @ApiModelProperty(value = "负责人岗位")
    private String leaderPostName;

    @ApiModelProperty(value = "起始年度")
    private String startDate;

    @ApiModelProperty(value = "终止年度")
    private String endDate;

    @ApiModelProperty(value = "密级编码")
    private String secretsCode;

    @ApiModelProperty(value = "密级")
    private String secretsName;

    @ApiModelProperty(value = "合同状态码")
    private String contractStatusCode;

    @ApiModelProperty(value = "合同状态", required = true)
    private String contractStatusName;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;
}
