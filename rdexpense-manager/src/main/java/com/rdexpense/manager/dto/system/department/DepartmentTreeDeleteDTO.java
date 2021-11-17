package com.rdexpense.manager.dto.system.department;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 组织管理   树形结构
 */
@ApiModel
@Data
public class DepartmentTreeDeleteDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "菜单编码",required = true)
    private String menuCode;

    @ApiModelProperty(value = "节点ID", required = true)
    private String orgId;

}
