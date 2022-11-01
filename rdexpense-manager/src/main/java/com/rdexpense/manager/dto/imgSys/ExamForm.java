package com.rdexpense.manager.dto.imgSys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 影像采集请求单
 * @author: Libaoyun
 * @date: 2022-10-31 18:58
 **/

@Data
@ApiModel("患者信息")
public class ExamForm implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("表单序列号")
    private Integer formSerialNum;

    @ApiModelProperty("患者就诊卡号")
    private Integer patientIdCardNo;

    @ApiModelProperty("检查单开具人工号")
    private Integer formIssuerCode;

    @ApiModelProperty("检查单登记人工号")
    private Integer formRegistrarCode;

    @ApiModelProperty("检查单开具科室编号")
    private Integer departmentCode;

    @ApiModelProperty("影像采集科室编号")
    private Integer imgDepartmentCode;

    @ApiModelProperty("激光号，关联病人(旧数据关联）")
    private Integer imgLaserId;

}
