package com.rdexpense.manager.dto.flow;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 流程
 */
@ApiModel
@Data
public class FlowDetailDTO extends BaseEntity implements Serializable {


    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "流程内容", required = true)
    private String flowContent;


}
