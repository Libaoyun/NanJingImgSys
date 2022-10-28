package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel
public class UserAuthMenuListDto {

    @ApiModelProperty(value = "主键ID",required=true)
    private String id;

    @ApiModelProperty(value = "权限对象ID", required = true)
    private String userId;

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value="用户类型 0：人员；1：职务 ", required = true)
    private String userFlag;

    @ApiModelProperty(value = "detailButton", required = true)
    private String detailButton;

    @ApiModelProperty(value = "菜单按钮", required = true)
    private String authorityButtonCode;

    @ApiModelProperty(value = "授权组织ID", required = true)
    private String scopeCode;


}
