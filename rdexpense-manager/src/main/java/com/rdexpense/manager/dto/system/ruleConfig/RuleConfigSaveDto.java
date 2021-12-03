package com.rdexpense.manager.dto.system.ruleConfig;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/6/3 13:55
 * @describe 规则配置
 */
@ApiModel
@Data
public class RuleConfigSaveDto extends OrganizationDto implements Serializable {


    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "规则项集合", required = true)
    private List<RuleConfigItemDto> ruleList;
}
