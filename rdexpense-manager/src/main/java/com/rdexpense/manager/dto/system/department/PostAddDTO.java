package com.rdexpense.manager.dto.system.department;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 职务
 */
@ApiModel
@Data
public class PostAddDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "父id", required = true)
    private String parentId;

    @ApiModelProperty(value = "职务名称", required = true)
    private String orgName;

    @ApiModelProperty(value = "职务编码", required = true)
    private String postCode;

    @ApiModelProperty("职务级别")
    private String postLevel;

    @ApiModelProperty("职务级别编码")
    private String postLevelCode;

    @ApiModelProperty("职务类型")
    private String postType;

    @ApiModelProperty("职务类型编码")
    private String postTypeCode;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0 禁用 ，1 启用")
    private String status;


}
