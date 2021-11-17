package com.rdexpense.manager.dto.system.dictionary;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(value = "数据字典")
@Data
public class DictionaryQueryDto {

    @ApiModelProperty(value = "字典枚举项编码")
    private String dicEnumId;

    @ApiModelProperty(value = "字典枚举项值")
    private String dicEnumName;

}

