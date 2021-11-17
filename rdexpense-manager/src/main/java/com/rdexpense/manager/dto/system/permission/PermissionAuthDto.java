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
}
