package com.rdexpense.manager.dto.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author luxiangbao
 * @date 2020/6/3 13:55
 * @describe 单据废除通用方法
 */
@ApiModel
@Data
public class FlowAbolishDto extends OrganizationDto implements Serializable {


    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "废除说明")
    private String noted;

}
