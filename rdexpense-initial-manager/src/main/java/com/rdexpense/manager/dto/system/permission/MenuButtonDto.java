package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MenuButtonDto {
    @ApiModelProperty(value = "菜单code 含父级",required=true)
    private String menuCode;

    @ApiModelProperty(value = "多个是逗号隔开[\"a10001,a10002,a10003,a10004\"]",required=true)
    private String authorityButtonCode;
}
