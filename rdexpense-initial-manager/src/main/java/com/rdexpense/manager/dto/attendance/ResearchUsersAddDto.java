package com.rdexpense.manager.dto.attendance;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "研究人员")
public class ResearchUsersAddDto implements Serializable {

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
    private String startDate;

    @ApiModelProperty(value = "参与研究结束日期")
    private String endDate;

}
