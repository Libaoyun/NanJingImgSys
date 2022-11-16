package com.rdexpense.manager.dto.system.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ghf96
 * @date 2020/8/19 11:07
 * @description 组织管理   树形结构
 */
@ApiModel
@Data
public class SysOrgResultDataDTO implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("父id ， 数值用父节点的code ")
    private Integer pId;

    @ApiModelProperty("编码")
    private Integer code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("状态 0 禁用 ，1 启用")
    private Integer status;

    @ApiModelProperty("排序数值")
    private Integer orderNumber;

    @ApiModelProperty("x 坐标")
    private String xCoordinate;

    @ApiModelProperty("y 坐标")
    private String yCoordinate;

    @ApiModelProperty("类型 0 项目 1 盾构队 2 区间 3 左右线、平面")
    private Integer orgType;

    @ApiModelProperty(" 结构层级  0 根节点，1 子节点，2 子子节点")
    private Integer orgLevelNumber;

    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("钉钉编码")
    private String dingdingCode;

    @ApiModelProperty("钉钉名称")
    private String dingdingName;

    @ApiModelProperty("经管编码")
    private String financeCode;

    @ApiModelProperty("经管名称")
    private String financeName;

    @ApiModelProperty("是否贯通 0 否，1 是")
    private Integer flag;

    @ApiModelProperty("树")
    private List<SysOrgResultDataDTO> childNode;
}
