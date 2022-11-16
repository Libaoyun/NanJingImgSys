package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PermissionAuthDto {

    @ApiModelProperty(value = "授权对象ID",required=true)
    private String userId;

    @ApiModelProperty(value = "授权对象名称",required=true)
    private String authName;

    @ApiModelProperty(value = "授权对象所属部门ID全路径",required=true)
    private String departmentCode;

    @ApiModelProperty(value = "授权对象所属部门名称全路径",required=true)
    private String departmentName;

}
