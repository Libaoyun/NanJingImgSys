package com.rdexpense.manager.dto.system.menu;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author rdexpense
 * @date 2020/5/26 15:18
 * @describe
 */
@ApiModel
@Data
public class UpdateMenuNodeDto extends OrganizationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID", required = true)
    private String id;

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "菜单名称", required = true)
    private String title;

    @ApiModelProperty(value = "菜单路径", required = true)
    private String path;

    @ApiModelProperty(value = "图标类型")
    private String icon;

    @ApiModelProperty(value = "路由名称", required = true)
    private String name;

    @ApiModelProperty(value = "绑定组件")
    private String component;

    @ApiModelProperty(value = " 无子级菜单 0:否 1:是")
    private String noDropDown;

    @ApiModelProperty(value = "是否缓存 0:否 1:是")
    private String keepAlive;

    @ApiModelProperty(value = "是否显示 0:显示 1:不显示")
    private String hidden;

    @ApiModelProperty(value = "是否为审批页面 0:否 1:是")
    private String isApprove;

    @ApiModelProperty(value = "是否关联工作流 0:否 1:是")
    private String relateFlow;


    @ApiModelProperty(value = "组合按钮")
    private String comButton;


}
