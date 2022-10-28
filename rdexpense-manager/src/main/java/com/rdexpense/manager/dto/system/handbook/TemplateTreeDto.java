package com.rdexpense.manager.dto.system.handbook;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/6/3 13:33
 * @describe 用户手册列表
 */
@ApiModel
@Data
public class TemplateTreeDto implements Serializable {

    @ApiModelProperty(value = "业务主键ID")
    private String nodeId;

    @ApiModelProperty(value = "作者ID")
    private String  nodeName;

    @ApiModelProperty(value = "类型1：菜单2：自定义节点")
    private String  nodeType;

    @ApiModelProperty(value = "子节点")
    private List<TemplateTreeDto> children;



}
