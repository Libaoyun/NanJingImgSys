package com.rdexpense.manager.dto.projContract;

import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

public class ProjContractAddDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "合同编号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称", required = true)
    private String projectName;

    @ApiModelProperty(value = "所属单位名称")
    private String unitName;

    @ApiModelProperty(value = "负责人岗位")
    private String leaderPostName;

    @ApiModelProperty(value = "起始年度")
    private String startDate;

    @ApiModelProperty(value = "密级")
    private String secretsName;

    @ApiModelProperty(value = "合同状态", required = true)
    private String contractStatusName;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

    public ProjContractAddDto() {
    }
}
