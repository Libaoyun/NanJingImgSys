package com.rdexpense.manager.dto.system.dictionary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "数据字典列表查询对象")
@Data
public class QueryDictionaryDto implements Serializable {
    /**
     * 字典类型Id
     */
    @ApiModelProperty(value = "字典类型Id",required=true)
    private String dicTypeId;

    @ApiModelProperty(value = "页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;

    private static final long serialVersionUID = 1L;
}

