package com.rdexpense.manager.dto.system.user;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/4/1 10:31
 */
@Data
@ApiModel(value = "员工主键ID")
public class IdListDto extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "菜单编码",required = true)
    private String menuCode;

    @ApiModelProperty(value = "主键id",required = true)
    private List<String> idList;
}
