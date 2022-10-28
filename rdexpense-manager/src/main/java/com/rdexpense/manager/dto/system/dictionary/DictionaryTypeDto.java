package com.rdexpense.manager.dto.system.dictionary;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author rdexpense
 * @description 数据字典类型字典表
 */
@Data
@ApiModel(value = "数据字典类型对象")
public class DictionaryTypeDto extends OrganizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "字典类别id")
    private String dicTypeId;

    @ApiModelProperty(value = "字典类型名称")
    private String dicTypeName;

    @ApiModelProperty(value = "字典类型父节点类别id,第一级节点传0")
    private String dicTypeParentId;

    @ApiModelProperty(value = "字典类型标识 1:字典类型 0:非字典类型")
    private String dicTypeFlag;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;


}
