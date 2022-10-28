package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class QueryAuthUserDto {

    @ApiModelProperty(value="组织ID",required=true)
    private String organizationId;

    @ApiModelProperty(value = "授权对象名称")
    private String authName;

    @ApiModelProperty(value = "页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;

}
