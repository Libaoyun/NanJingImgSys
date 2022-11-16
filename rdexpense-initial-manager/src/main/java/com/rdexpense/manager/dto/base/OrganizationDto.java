package com.rdexpense.manager.dto.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class OrganizationDto implements Serializable {

    @ApiModelProperty(value = "右上角项目ID", required = true)
    private String creatorOrgId;

    @ApiModelProperty(value = "右上角项目名称", required = true)
    private String creatorOrgName;
}
