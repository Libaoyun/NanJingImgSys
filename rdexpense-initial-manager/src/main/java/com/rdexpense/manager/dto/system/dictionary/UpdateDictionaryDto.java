package com.rdexpense.manager.dto.system.dictionary;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "更新数据字典对象")
@Data
public class UpdateDictionaryDto extends OrganizationDto implements Serializable {


    @ApiModelProperty(value = "字典枚举项编码",required=true)
    private String dicEnumId;

    @ApiModelProperty(value = "字典枚举项值",required=true)
    private String dicEnumName;

    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "是否启用：0-不启用；1-启用",required=true)
    private Integer isValid;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "字典类型ID")
    private String dicTypeId;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;


}

