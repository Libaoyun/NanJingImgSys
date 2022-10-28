package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthorityUserListDTO {

    @ApiModelProperty(value = "授权状态 0-未授权 1-已授权")
    private Integer authStatus;

    @ApiModelProperty(value = "用户类型 0：人员；1：职务")
    private String userFlag;

    @ApiModelProperty(value = "授权对象ID")
    private String userId;

    @ApiModelProperty(value = "授权对象名称")
    private String authName;

    @ApiModelProperty(value = "所属部门职务编码")
    private String departmentCode;

    @ApiModelProperty(value = "所属部门职务")
    private String departmentName;

    @ApiModelProperty(value = "授权组织编码")
    private String scopeCode;

}
