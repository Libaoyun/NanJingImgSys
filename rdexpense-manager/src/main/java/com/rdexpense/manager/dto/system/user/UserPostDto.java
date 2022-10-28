package com.rdexpense.manager.dto.system.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "员工职务信息")
public class UserPostDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "职务")
    private String orgName;

    @ApiModelProperty(value = "职务编码")
    private String orgId;

    @ApiModelProperty(value = "类型 1:部门 2:职务")
    private String orgType;

    @ApiModelProperty(value = "子节点")
    private List<UserPostDto> children;
}
