package com.rdexpense.manager.dto.system.operationLog;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IdListDto extends OrganizationDto implements Serializable {
    @ApiModelProperty(value = "主键ID集合", required = true)
    private List<String> idList;

    @ApiModelProperty(value = "菜单编码", required = true)
    private Integer menuCode;

}

