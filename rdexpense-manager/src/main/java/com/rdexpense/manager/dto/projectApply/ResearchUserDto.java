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
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "研究人员")
public class ResearchUserDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "年龄")
    private String age;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "所属部门")
    private String belongDepartment;

    @ApiModelProperty(value = "所属职务")
    private String belongPost;

    @ApiModelProperty(value = "所学专业")
    private String majorStudied;

    @ApiModelProperty(value = "从事专业")
    private String majorWorked;

    @ApiModelProperty(value = "所在单位")
    private String belongUnit;

    @ApiModelProperty(value = "研究任务及分工")
    private String taskDivision;

    @ApiModelProperty(value = "全时率")
    private String workRate;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "参与研究开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "参与研究结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "编制人ID")
    private String creatorUserId;

    @ApiModelProperty(value = "编制人")
    private String creatorUser;

    @ApiModelProperty(value = "编制时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
