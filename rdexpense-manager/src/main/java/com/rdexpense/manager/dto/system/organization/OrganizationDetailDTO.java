package com.rdexpense.manager.dto.system.organization;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 组织明细
 */
@ApiModel
@Data
public class OrganizationDetailDTO implements Serializable {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("项目名称")
    private String orgName;

    @ApiModelProperty("部门描述")
    private String remark;

    @ApiModelProperty("状态 0 禁用 ，1 启用")
    private String status;


}
