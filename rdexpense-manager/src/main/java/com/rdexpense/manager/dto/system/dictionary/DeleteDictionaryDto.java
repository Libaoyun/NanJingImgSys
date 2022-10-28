package com.rdexpense.manager.dto.system.dictionary;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "删除数据字典对象")
@Data
public class DeleteDictionaryDto extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "主键id集合", required = true)
    private List<String> ids;

    @ApiModelProperty(value = "字典类别id")
    private String dicTypeId;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;


    private static final long serialVersionUID = 1L;
}

