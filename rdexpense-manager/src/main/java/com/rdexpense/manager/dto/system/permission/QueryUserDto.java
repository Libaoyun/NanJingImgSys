package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
public class QueryUserDto {

    @ApiModelProperty(value="组织ID",required=true)
    private String organizationId;

    @ApiModelProperty(value = "编号")
    private String userCode;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "英文名")
    private String englishUserName;

    @ApiModelProperty(value = "页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;

}
