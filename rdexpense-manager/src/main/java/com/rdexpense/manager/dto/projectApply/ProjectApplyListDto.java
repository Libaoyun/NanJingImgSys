package com.rdexpense.manager.dto.projectApply;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "立项申请查询列表")
public class ProjectApplyListDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "流程状态；可取字典表数据")
    private String processStatus;

    @ApiModelProperty(value = "流程状态名称；可取字典表数据")
    private String processName;

    @ApiModelProperty(value = "当前审批人")
    private String approveUserName;

    @ApiModelProperty(value = "当前审批人ID")
    private String approveUserId;

    @ApiModelProperty(value = "申请人")
    private String applyUserName;

    @ApiModelProperty(value = "申请人ID")
    private String applyUserId;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "职务编码")
    private String postCode;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "项目类型编码")
    private String projectTypeCode;

    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "专业类别编码")
    private String professionalCategoryCode;

    @ApiModelProperty(value = "专业类别")
    private String professionalCategory;

    @ApiModelProperty(value = "研究内容")
    private String researchContents;

    @ApiModelProperty(value = "申请经费")
    private String applyAmount;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;


}
