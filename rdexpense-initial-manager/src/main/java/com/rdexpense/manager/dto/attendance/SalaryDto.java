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
@ApiModel(value = "工资表")
public class SalaryDto implements Serializable {

    @ApiModelProperty(value = "工资表单位")
    private String salaryUnit;

    @ApiModelProperty(value = "工资表月份")
    private String salaryDate;

    @ApiModelProperty(value = "工资表项目")
    private String salaryProject;

    @ApiModelProperty(value = "工资集合")
    private List<SalaryListDto> salaryList;


}
