package com.rdexpense.manager.dto.system.organization;

import com.rdexpense.manager.dto.base.OrganizationDto;
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
public class OrganizationUpdateDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "主键ID", required = true)
    private String id;

    @ApiModelProperty(value = "项目名称", required = true)
    private String orgName;

    @ApiModelProperty("部门描述")
    private String remark;

    @ApiModelProperty(value = "状态 0 禁用 ，1 启用", required = true)
    private String status;


}
