package com.rdexpense.manager.dto.equipDepreciation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import com.rdexpense.manager.dto.projectApply.ResearchUserDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "新增设备折旧表项")
public class EquipDepreciationAddDto extends OrganizationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "主键ID(编辑时传参)")
    private String id;

    @ApiModelProperty(value = "业务主键ID(编辑时传参)")
    private String businessId;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "归属单位名称")
    private String unitName;

    @ApiModelProperty(value = "项目起始时间")
    private String projectStartDate;

    @ApiModelProperty(value = "项目结束时间")
    private String projectEndDate;

    @ApiModelProperty(value = "项目负责人ID")
    private String projectLeaderId;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    @ApiModelProperty(value = "项目编制单位ID")
    private String projectCreatorOrgId;

    @ApiModelProperty(value = "项目编制单位")
    private String projectCreatorOrgName;

    @ApiModelProperty(value = "项目编制者ID")
    private String projectCreatorUserId;

    @ApiModelProperty(value = "项目编制者")
    private String projectCreatorUserName;

    @ApiModelProperty(value = "项目编制日期")
    private String projectCreatedDate;

    @ApiModelProperty(value = "审批流程实例ID")
    private String processInstId;

    @ApiModelProperty(value = "审批流程实例ID")
    private String processStatus;

    @ApiModelProperty(value = "审批人ID")
    private String approveUserId;

    @ApiModelProperty(value = "审批人")
    private String approveUserName;

    @ApiModelProperty(value = "审批时间")
    private String approveUserTime;

    @ApiModelProperty(value = "下一个审批人ID")
    private String nextApproveUserId;

    @ApiModelProperty(value = "下一个审批人")
    private String nextApproveUserName;

    @ApiModelProperty(value = "不含税总金额")
    private String amountExcludingTax;

    @ApiModelProperty(value = "归属月份")
    private String monthBelongTo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "单据创建人ID")
    private String creatorUserId;

    @ApiModelProperty(value = "单据创建人")
    private String creatorUserName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "设备折旧详情单")
    private List<EquipDepreciationDetailListDto> detailList;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;
}
