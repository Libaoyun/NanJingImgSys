package com.rdexpense.manager.dto.system.permission;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data

public class PermissionOrganizationListDTO {

    @ApiModelProperty("组织ID")
    private String organizationId;

    @ApiModelProperty("组织名称")
    private String organizationName;

}
