package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class QueryUserDto {

    @ApiModelProperty(value = "授权项目ID", required = true)
    private String organizationId;

    @ApiModelProperty(value = "组织编码")
    private String orgCode;

    @ApiModelProperty(value = "组织类型")
    private String orgType;

    @ApiModelProperty(value = "编号")
    private String userCode;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "页码,值需大于等于1", required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1", required = true)
    private Integer pageSize;

}
