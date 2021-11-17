package com.rdexpense.manager.dto.system.department;

import com.rdexpense.manager.dto.base.OrganizationDto;
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
public class DepartmentTreeDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("父id")
    private String parentId;

    @ApiModelProperty("编码")
    private String orgId;

    @ApiModelProperty("名称")
    private String orgName;

    @ApiModelProperty("状态 0 禁用 ，1 启用")
    private String status;

    @ApiModelProperty("排序数值")
    private String orderNumber;

    @ApiModelProperty("节点标识 0:公司,1:部门, 2:职务")
    private String orgType;

    @ApiModelProperty("子节点")
    private List<DepartmentTreeDTO> children;
}
