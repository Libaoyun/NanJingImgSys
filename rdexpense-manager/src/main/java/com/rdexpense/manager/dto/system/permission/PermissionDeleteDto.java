package com.rdexpense.manager.dto.system.permission;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel(value = "删除授权")
public class PermissionDeleteDto extends OrganizationDto {

    @ApiModelProperty(value="组织ID",required=true)
    private String organizationId;

    @ApiModelProperty(value = "用户ID列表",required=true)
    List<String> userList;

    @ApiModelProperty(value = "菜单编码",required=true)
    private String menuCode;

}
