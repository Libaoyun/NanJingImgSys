package com.rdexpense.manager.dto.system.handbook;

import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/6/3 13:55
 * @describe 新增知识库
 */
@ApiModel
@Data
public class TemplateSaveDto extends OrganizationDto implements Serializable {


    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "状态 0 禁用 ，1 启用", required = true)
    private String status;

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @ApiModelProperty(value = "文件集合", required = true)
    private List<CreateFileDto> fileList;
}
