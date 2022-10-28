package com.rdexpense.manager.dto.system.menu;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class RouteMenu<T> extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 菜单编码ID
     */
    @ApiModelProperty(value = "菜单编码ID")
    private Integer menuCode;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    private String title;

    /**
     * 菜单路径
     */
    @ApiModelProperty(value = "菜单路径")
    private String path;

    /**
     * 父级ID
     */
    @ApiModelProperty(value = "父级ID")
    private Integer parentCode;

    /**
     * 菜单类型
     */
    @ApiModelProperty(value = "菜单类型")
    private String menuType;

    /**
     * 菜单顺序
     */
    @ApiModelProperty(value = "菜单顺序")
    private Integer orderNumber;

    /**
     * 菜单层级
     */
    @ApiModelProperty(value = "菜单层级")
    private Integer levelNumber;

    /**
     * 图标类型
     */
    @ApiModelProperty(value = "图标类型")
    private String icon;

    /**
     *
     */
    @ApiModelProperty(value = "")
    private String name;

    /**
     * 重定向
     */
    @ApiModelProperty(value = "重定向")
    private String redirect;

    /**
     * 绑定组件
     */
    @ApiModelProperty(value = "绑定组件")
    private String component;

    /**
     * 是否启用,0:禁用；1：启用
     */
    @ApiModelProperty(value = "是否启用,0:禁用；1：启用")
    private String isValid;

    /**
     * 是否可以关联工作流 0 否 1 是
     */
    @ApiModelProperty(value = "是否可以关联工作流 0 否 1 是")
    private String relateFlow;

    /*
     * 子菜单
     **/
    @Transient
    private List<T> children;

}
