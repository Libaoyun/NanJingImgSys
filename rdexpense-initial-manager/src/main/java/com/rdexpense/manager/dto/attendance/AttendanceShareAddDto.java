package com.rdexpense.manager.dto.attendance;

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
@ApiModel(value = "新增人员考勤分摊表")
public class AttendanceShareAddDto  implements Serializable {

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "研发人员考勤表(不要传合计)")
    private List<AttendanceListAddDto> attendanceList;

    @ApiModelProperty(value = "研发人员工资表")
    private List<SalaryListDto> salaryList;



}
