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
public class MenuNodeDto extends OrganizationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码ID", required = true)
    private String menuCode;


}
