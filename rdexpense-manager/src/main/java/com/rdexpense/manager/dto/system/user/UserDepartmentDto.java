package com.rdexpense.manager.dto.system.user;

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
@ApiModel(value = "员工部门信息")
public class UserDepartmentDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "部门")
    private String departmentName;

    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "职务编码")
    private String postCode;
}
