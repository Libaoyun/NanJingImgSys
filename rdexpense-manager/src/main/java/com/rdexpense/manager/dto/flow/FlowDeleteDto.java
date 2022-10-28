package com.rdexpense.manager.dto.flow;

import com.rdexpense.manager.dto.base.OrganizationDto;
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
public class FlowDeleteDto extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "通用菜单编码",required = true)
    private String menuCode;

    @ApiModelProperty(value = "配置的菜单ID",required = true)
    private List<String> idList;
}
