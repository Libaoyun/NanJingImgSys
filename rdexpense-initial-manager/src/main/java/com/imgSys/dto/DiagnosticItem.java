package com.imgSys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 科室可诊疗项目
 * @author: Libaoyun
 * @date: 2022-10-19 17:02
 **/

@Data
@ApiModel("科室可诊疗项目")
public class DiagnosticItem implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("项目名称")
    private String itemName;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目序列号")
    private String itemSerialNum;

    @ApiModelProperty("项目描述")
    private String itemDiscrip;

    @ApiModelProperty("项目父级序列号")
    private String parentSerialNum;

    @ApiModelProperty("项目所属科室")
    private String departmentCode;

    @ApiModelProperty("项目的子项目")
    private List<DiagnosticItem> children;


}
