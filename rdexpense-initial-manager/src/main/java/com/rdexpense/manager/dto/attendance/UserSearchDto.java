package com.rdexpense.manager.dto.attendance;

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
@ApiModel(value = "人员查询入参")
public class UserSearchDto implements Serializable {

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "考勤月份")
    private String attendanceMonth;
}
