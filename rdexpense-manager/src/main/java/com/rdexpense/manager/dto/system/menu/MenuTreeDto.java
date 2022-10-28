package com.rdexpense.manager.dto.system.menu;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @date 2020/5/26 15:18
 * @describe
 */
@ApiModel
@Data
public class MenuTreeDto extends OrganizationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "菜单编码ID")
    private String menuCode;


    @ApiModelProperty(value = "菜单名称")
    private String title;

    @ApiModelProperty(value = "菜单路径")
    private String path;

    @ApiModelProperty(value = "父级ID")
    private Integer parentCode;

    @ApiModelProperty(value = "")
    private String menuType;

    @ApiModelProperty(value = "菜单顺序")
    private Integer orderNumber;

    @ApiModelProperty(value = "菜单层级")
    private Integer levelNumber;

    @ApiModelProperty(value = "图标类型")
    private String icon;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "重定向")
    private String redirect;

    @ApiModelProperty(value = "绑定组件")
    private String component;

    @ApiModelProperty(value = "是否启用,0:禁用；1：启用")
    private String isValid;

    @ApiModelProperty(value = "是否可以关联工作流 0 否 1 是")
    private String relateFlow;

    @ApiModelProperty(value = "0:false,1:true")
    private String noDropDown;

    @ApiModelProperty(value = "0:false,1:true")
    private String keepAlive;

    @ApiModelProperty(value = "0:false,1:true")
    private String hidden;

    @ApiModelProperty(value = "0:不是，1：是")
    private String isHome;

    @ApiModelProperty(value = "0，路由菜单，1，按钮路由")
    private String pathRouting;

    @ApiModelProperty(value = "是否需要展示：0展示，1bu展示")
    private String isAble;

    @ApiModelProperty(value = "是否为审批页面")
    private String isApprove;

    @ApiModelProperty(value = "组合按钮")
    private String comButton;

    @ApiModelProperty(value = "详细按钮")
    private String detailButton;

    @ApiModelProperty(value = "是否选中")
    private Boolean selected;

    @ApiModelProperty(value = "标识")
    private String flag;


    /*
     * 子菜单
     **/
    @Transient
    private List<MenuTreeDto> children;

}
