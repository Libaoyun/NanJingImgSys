package com.rdexpense.manager.dto.system.permission;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "新增授权")
public class PermissionAddDto extends OrganizationDto {

    @ApiModelProperty(value = "菜单编码",required=true)
    private String menuCode;

    @ApiModelProperty(value="组织ID",required=true)
    private String organizationId;

    @ApiModelProperty(value="用户类型 0：人员；1：职务 ",required=true)
    private String userFlag;

    @ApiModelProperty(value = "对象集合",required=true)
    private List<PermissionAuthDto> userList;

}
