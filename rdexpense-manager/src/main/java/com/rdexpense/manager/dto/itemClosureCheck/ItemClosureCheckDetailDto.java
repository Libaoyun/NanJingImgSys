package com.rdexpense.manager.dto.itemClosureCheck;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "查询研发项目结题验收申请详情")
public class ItemClosureCheckDetailDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "成果名称")
    private String jobTitle;

    @ApiModelProperty(value = "流程状态；可取字典表数据")
    private String processStatus;

    @ApiModelProperty(value = "流程状态名称；可取字典表数据")
    private String processName;

    @ApiModelProperty(value = "当前审批人")
    private String approveUserName;

    @ApiModelProperty(value = "当前审批人ID")
    private String approveUserId;

    @ApiModelProperty(value = "申请评审验收单位ID")
    private String creatorOrgId;

    @ApiModelProperty(value = "申请评审验收单位")
    private String creatorOrg;

    @ApiModelProperty(value = "结题申报人ID")
    private String creatorUserId;

    @ApiModelProperty(value = "结题申报人")
    private String creatorUser;

    @ApiModelProperty(value = "申请评审日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createdDate;

    @ApiModelProperty(value = "申请人")
    private String applyUserName;

    @ApiModelProperty(value = "申请人ID")
    private String applyUserId;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "职务编码")
    private String postCode;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;
}
