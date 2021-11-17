package com.rdexpense.manager.dto.system.handbook;


import com.rdexpense.manager.dto.file.CreateFileDto;
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
public class TemplateDetailDto implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "描述信息")
    private String remark;

    @ApiModelProperty(value = "文件集合")
    private List<CreateFileDto> fileList;


}
