package com.rdexpense.manager.dto.flow;

import com.common.entity.PageData;
import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.system.department.DepartmentUpdateTreeDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 新增流程
 */
@ApiModel
@Data
public class FlowAddDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "通用的菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "设置流程的菜单编码", required = true)
    private String menuId;

    @ApiModelProperty(value = "流程内容", required = true)
    private PageData flowContent;



}
