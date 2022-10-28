package com.rdexpense.manager.dto.system.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/4/15 17:16
 */
@Data
public class FunctionMenuButtonDTO implements Serializable {


    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 菜单编码ID
     */
    @ApiModelProperty(value = "菜单编码ID")
    private Integer menuCode;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    private  String title;

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
     * 重定向
     */
    @ApiModelProperty(value = "重定向")
    private String redirect;

    /**
     * 绑定组件
     */
    @ApiModelProperty(value = "绑定组件")
    private String component;

    private String isValid;

    private String relateFlow;

    private String name;

    private List<FunctionMenuButtonDTO> children;

    private Meta meta;

    private Integer noDropdownVal;
    private Boolean noDropdown = false;

    private Integer keepAliveVal;


    private Integer hiddenVal;
    private Boolean hidden;


    private Integer isHome;

    /**
     * 区分列表路由和按钮路由：0，路由菜单，1，按钮路由
     */
    @ApiModelProperty(value = "区分列表路由和按钮路由：0，路由菜单，1，按钮路由")
    private Integer pathRouting;



}
