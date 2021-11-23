package com.rdexpense.manager.dto.system.organization;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 项目查询
 */
@ApiModel
@Data
public class OrganizationQueryDTO implements Serializable {

    @ApiModelProperty("项目名称")
    private String orgName;

    @ApiModelProperty("项目编号")
    private String orgNumber;

    @ApiModelProperty("项目描述")
    private String remark;

    @ApiModelProperty(value = "状态 0 禁用 ，1 启用", required = true)
    private String status;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;

}
