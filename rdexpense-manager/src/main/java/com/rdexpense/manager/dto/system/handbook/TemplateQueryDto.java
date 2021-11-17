package com.rdexpense.manager.dto.system.handbook;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author luxiangbao
 * @date 2020/6/3 13:33
 * @describe 用户手册列表
 */
@ApiModel
@Data
public class TemplateQueryDto implements Serializable {

    @ApiModelProperty(value = "nodeId", required = true)
    private String 节点ID;

    @ApiModelProperty(value = "页码,值需大于等于1", required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1", required = true)
    private Integer pageSize;



}
