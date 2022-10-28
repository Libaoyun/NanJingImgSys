package com.rdexpense.manager.dto.system.permission;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel
public class AlterUserAuthMenuDto extends OrganizationDto  {

    @ApiModelProperty(value = "菜单编码",required=true)
    private String menuCode;

    @ApiModelProperty(value = "权限对象ID", required = true)
    private String userId;

    @ApiModelProperty(value="用户类型 0：人员；1：职务 ", required = true)
    private String userFlag;

    @ApiModelProperty(value = "授权组织ID", required = true)
    private String organizationId;

    @ApiModelProperty(value = "菜单code", required = true)
    private List<MenuButtonDto> menuButton;


}
