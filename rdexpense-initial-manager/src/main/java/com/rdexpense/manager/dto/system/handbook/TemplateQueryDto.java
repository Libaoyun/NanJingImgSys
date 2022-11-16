package com.rdexpense.manager.dto.system.handbook;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author luxiangbao
 * @date 2020/6/3 13:33
 * @describe 知识库
 */
@ApiModel
@Data
public class TemplateQueryDto implements Serializable {

    @ApiModelProperty(value = "文件名称", required = true)
    private String fileName;

    @ApiModelProperty(value = "文件格式", required = true)
    private String fileFormat;

    @ApiModelProperty(value = "状态 0 禁用 ，1 启用", required = true)
    private String status;

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "页码,值需大于等于1", required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1", required = true)
    private Integer pageSize;



}
