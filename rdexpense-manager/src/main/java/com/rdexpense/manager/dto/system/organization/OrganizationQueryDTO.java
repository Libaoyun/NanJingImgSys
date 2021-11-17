package com.rdexpense.manager.dto.system.organization;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 组织查询
 */
@ApiModel
@Data
public class OrganizationQueryDTO implements Serializable {

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;

}
