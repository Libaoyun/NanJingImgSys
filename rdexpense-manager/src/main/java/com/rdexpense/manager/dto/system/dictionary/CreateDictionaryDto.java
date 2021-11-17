package com.rdexpense.manager.dto.system.dictionary;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "新建数据字典对象")
@Data
public class CreateDictionaryDto extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "字典类型Id",required=true)
    private String dicTypeId;

    @ApiModelProperty(value = "字典类型名称",required=true)
    private String dicTypeName;

    @ApiModelProperty(value = "字典枚举项值",required=true)
    private String dicEnumName;

    @ApiModelProperty(value = "字典枚举项值编码",required=true)
    private String dicEnumId;

    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "是否启用：0-不启用；1-启用",required=true)
    private Integer isValid;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    private static final long serialVersionUID = 1L;
}

