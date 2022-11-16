package com.rdexpense.manager.dto.materialRequisition;

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
@ApiModel(value = "新增领料单")
public class MaterialRequisitionAddDto extends OrganizationDto implements Serializable {

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

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeader;

    @ApiModelProperty(value = "所属月份")
    private String belongMonth;

    @ApiModelProperty(value = "申请日期")
    private String applyDate;

    @ApiModelProperty(value = "备注")
    private String  remark;

    @ApiModelProperty(value = "领料单项目")
    private String billProject;

    @ApiModelProperty(value = "领料单月份")
    private String billMonth;


    @ApiModelProperty(value = "领料单明细")
    private List<MaterialRequisitionDetailedDto> detailList;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
