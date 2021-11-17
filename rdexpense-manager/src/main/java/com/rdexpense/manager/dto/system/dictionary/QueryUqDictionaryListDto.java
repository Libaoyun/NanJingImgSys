package com.rdexpense.manager.dto.system.dictionary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@ApiModel(value = "数据字典类型集合对象")
@Data
public class QueryUqDictionaryListDto implements Serializable {

    @ApiModelProperty(value = "字典类型id集合", required = true)
    @NotEmpty(message = "字典类型编码[ids]不能为空")
    private List<String> dicTypeIds;

    private static final long serialVersionUID = 1L;
}

