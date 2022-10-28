package com.rdexpense.manager.dto.system.handbook;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/6/3 14:38
 * @describe 删除用户手册
 */
@ApiModel
@Data
public class TemplateDeleteDto extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "业务主键ID集合", required = true)
    private List<String> businessIdList;

    @ApiModelProperty(value = "菜单编码", required = true)
    private Integer menuCode;
}
