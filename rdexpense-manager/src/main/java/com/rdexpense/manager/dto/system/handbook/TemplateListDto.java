package com.rdexpense.manager.dto.system.handbook;

import com.rdexpense.manager.dto.base.BaseEntity;
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
public class TemplateListDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "文件名称", required = true)
    private String fileName;

    @ApiModelProperty(value = "文件格式", required = true)
    private String fileFormat;

    @ApiModelProperty(value = "文件大小", required = true)
    private String fileSize;

    @ApiModelProperty(value = "状态 0 禁用 ，1 启用", required = true)
    private String status;

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @ApiModelProperty(value = "文件url地址")
    private String fileUrl;

}
