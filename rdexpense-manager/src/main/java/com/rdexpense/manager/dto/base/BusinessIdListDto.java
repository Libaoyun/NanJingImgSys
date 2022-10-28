package com.rdexpense.manager.dto.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/4/1 10:31
 */
@Data
@ApiModel(value = "业务主键id集合")
public class BusinessIdListDto extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "菜单编码",required = true)
    private String menuCode;

    @ApiModelProperty(value = "业务主键id",required = true)
    private List<String> businessIdList;
}
