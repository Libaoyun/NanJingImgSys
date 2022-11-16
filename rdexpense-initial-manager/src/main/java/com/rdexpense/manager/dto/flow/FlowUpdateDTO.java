package com.rdexpense.manager.dto.flow;

import com.common.entity.PageData;
import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 编辑流程
 */
@ApiModel
@Data
public class FlowUpdateDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "主键ID", required = true)
    private String id;

    @ApiModelProperty(value = "通用的菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "流程内容", required = true)
    private PageData flowContent;


}
