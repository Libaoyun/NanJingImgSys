package com.rdexpense.manager.dto.system.department;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 部门
 */
@ApiModel
@Data
public class DepartmentAddDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "父id", required = true)
    private String parentId;

    @ApiModelProperty(value = "部门名称", required = true)
    private String orgName;

    @ApiModelProperty(value = "部门编码", required = true)
    private String departmentCode;

    @ApiModelProperty("部门简称")
    private String departmentSimpleName;

//    @ApiModelProperty("部门级别")
//    private String departmentLevel;
//
//    @ApiModelProperty("部门级别编码")
//    private String departmentLevelCode;
//
//    @ApiModelProperty("部门类型")
//    private String departmentType;
//
//    @ApiModelProperty("部门类型编码")
//    private String departmentTypeCode;
//
//    @ApiModelProperty("传真")
//    private String fax;

    @ApiModelProperty("联系电话")
    private String inTelephone;

/*
    @ApiModelProperty("外线电话")
    private String outTelephone;
*/

    @ApiModelProperty("部门描述")
    private String departmentRemark;

    @ApiModelProperty("部门职责")
    private String departmentDuty;

    @ApiModelProperty("定编人数")
    private String departmentPeople;

    @ApiModelProperty("状态 0 禁用 ，1 启用")
    private String status;


}
