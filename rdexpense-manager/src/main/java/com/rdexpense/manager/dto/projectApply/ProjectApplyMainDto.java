package com.rdexpense.manager.dto.projectApply;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:00
 */
@Data
@ApiModel(value = "项目立项申请详情")
public class ProjectApplyMainDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
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

    @ApiModelProperty(value = "年龄")
    private String age;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "职务编码")
    private String postCode;

    @ApiModelProperty(value = "电话号码")
    private String telephone;

    @ApiModelProperty(value = "申请经费")
    private String applyAmount;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

    @ApiModelProperty(value = "项目类型编码")
    private String projectTypeCode;

    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "专业类别编码")
    private String professionalCategoryCode;

    @ApiModelProperty(value = "专业类别")
    private String professionalCategory;

    @ApiModelProperty(value = "是否鉴定 0：否1：是")
    private String identify;

    @ApiModelProperty(value = "研究内容")
    private String researchContents;

    @ApiModelProperty(value = "审查意见")
    private String reviewComments;


}
