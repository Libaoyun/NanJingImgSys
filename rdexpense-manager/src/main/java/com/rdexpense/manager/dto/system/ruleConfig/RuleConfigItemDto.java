package com.rdexpense.manager.dto.system.ruleConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/6/3 13:55
 * @describe 规则配置
 */
@ApiModel
@Data
public class RuleConfigItemDto implements Serializable {


    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "规则分类 1:单次材料,2:材料费,3:机械使用费,4:人工费,5:其他费用", required = true)
    private String ruleType;

    @ApiModelProperty(value = "规则值")
    private String ruleValue;

    @ApiModelProperty(value = " 状态  0 禁用，1 可用", required = true)
    private String status;

}
