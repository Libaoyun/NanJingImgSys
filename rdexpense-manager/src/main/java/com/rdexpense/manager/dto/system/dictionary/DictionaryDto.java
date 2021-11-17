package com.rdexpense.manager.dto.system.dictionary;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "数据字典")
@Data
public class DictionaryDto {


    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "字典类型Id")
    private String dicTypeId;

    @ApiModelProperty(value = "字典类型名称")
    private String dicTypeName;

    @ApiModelProperty(value = "字典枚举项编码")
    private String dicEnumId;

    @ApiModelProperty(value = "字典枚举项值")
    private String dicEnumName;

    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "是否启用：0-不启用；1-启用")
    private Integer isValid;

    @ApiModelProperty(value = "是否显示：0-不显示；1-显示")
    private Integer isShow;

    private static final long serialVersionUID = 1L;
}

