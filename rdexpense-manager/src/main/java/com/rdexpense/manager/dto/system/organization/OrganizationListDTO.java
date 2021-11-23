package com.rdexpense.manager.dto.system.organization;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 项目
 */
@ApiModel
@Data
public class OrganizationListDTO extends BaseEntity implements Serializable {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("项目ID")
    private String orgId;

    @ApiModelProperty("项目名称")
    private String orgName;

    @ApiModelProperty("项目编号")
    private String orgNumber;

    @ApiModelProperty(value = "状态 0 禁用 ，1 启用", required = true)
    private String status;

    @ApiModelProperty("项目描述")
    private String remark;


}
