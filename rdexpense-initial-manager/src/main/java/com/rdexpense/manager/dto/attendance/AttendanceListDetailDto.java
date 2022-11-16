package com.rdexpense.manager.dto.attendance;


import com.common.entity.PageData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "考勤表")
public class AttendanceListDetailDto implements Serializable {

    @ApiModelProperty(value = "考勤表单位")
    private String  attendUnit;

    @ApiModelProperty(value = "考勤表月份")
    private String  attendDate;

    @ApiModelProperty(value = "考勤表天数")
    private String  attendDays;

    @ApiModelProperty(value = "考勤表人数")
    private String attendPeoples;

    @ApiModelProperty(value = "考勤表课题")
    private String attendProject;

    @ApiModelProperty(value = "考勤集合")
    private List<PageData> attendanceList;



}
