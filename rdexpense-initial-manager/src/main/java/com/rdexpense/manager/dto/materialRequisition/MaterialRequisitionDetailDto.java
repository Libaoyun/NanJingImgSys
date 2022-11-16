package com.rdexpense.manager.dto.materialRequisition;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(value = "领料单详情")
public class MaterialRequisitionDetailDto implements Serializable {

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

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeader;

    @ApiModelProperty(value = "所属月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date belongMonth;

    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "领料单项目")
    private String billProject;

    @ApiModelProperty(value = "领料单月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date billMonth;


    @ApiModelProperty(value = "领料单明细")
    private List<MaterialRequisitionDetailedDto> detailList;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
