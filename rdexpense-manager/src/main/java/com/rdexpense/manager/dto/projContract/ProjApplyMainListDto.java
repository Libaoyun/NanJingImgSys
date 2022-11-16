package com.rdexpense.manager.dto.projContract;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "项目立项管理列表")
public class ProjApplyMainListDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务ID")
    private String businessId;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "申请单号")
    private String serialNumber;

    @ApiModelProperty(value = "编制单位ID")
    private String creatorOrgId;

    @ApiModelProperty(value = "编制单位名称")
    private String creatorOrgName;

    @ApiModelProperty(value = "编制人ID")
    private String creatorUserId;

    @ApiModelProperty(value = "编制人名称")
    private String creatorUser;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createdDate;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "单位地址")
    private String unitAddress;

    @ApiModelProperty(value = "申请人")
    private String applyUserName;

    @ApiModelProperty(value = "申请人ID")
    private String applyUserId;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "性别编码")
    private String genderCode;

    @ApiModelProperty(value = "职务编码")
    private String postCode;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "数据生成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "数据更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更新人")
    private String updateUser;

    @ApiModelProperty(value = "更新人ID")
    private String updateUserId;

    @ApiModelProperty(value = "电话号码")
    private String telephone;

    @ApiModelProperty(value = "申请经费")
    private String applyAmount;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "终止年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

    @ApiModelProperty(value = "专业类别编码")
    private String professionalCategoryCode;

    @ApiModelProperty(value = "专业类别")
    private String professionalCategory;

    @ApiModelProperty(value = "项目类别编码")
    private String projectTypeCode;

    @ApiModelProperty(value = "项目类别")
    private String projectType;

    @ApiModelProperty(value = "是否鉴别 0:否, 1:是")
    private String identify;

    @ApiModelProperty(value = "研究内容")
    private String researchContents;

    @ApiModelProperty(value = "审查意见")
    private String reviewComments;

    @ApiModelProperty(value = "审批人ID")
    private String approveUserId;

    @ApiModelProperty(value = "审批人")
    private String approveUserName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date approveTime;

    @ApiModelProperty(value = "审批流程实例ID")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date processInstId;

    @ApiModelProperty(value = "审批流程状态")
    private String processStatus;

    @ApiModelProperty(value = "下一个审批人ID")
    private String nextApproveUserId;

    @ApiModelProperty(value = "下一个审批人")
    private String nextApproveUserName;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
