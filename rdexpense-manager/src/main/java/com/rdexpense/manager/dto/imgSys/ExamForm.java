package com.rdexpense.manager.dto.imgSys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
    private String formSerialNum;

    @ApiModelProperty("患者就诊卡号")
    private String patientIdCardNo;

    @ApiModelProperty("检查单开具人工号")
    private String formIssuerCode;

    @ApiModelProperty("检查单登记人工号")
    private String formRegisterCode;

    @ApiModelProperty("诊断医师姓名")
    private String formIssuerName;

    @ApiModelProperty("检查单开具科室编号")
    private String departmentCode;

    @ApiModelProperty("影像采集科室编号")
    private String imgDepartmentCode;

    @ApiModelProperty("激光号，关联病人(旧数据关联）")
    private String imgLaserId;

    @ApiModelProperty("是否登记")
    private String isRegister;

    @ApiModelProperty("是否缴费")
    private String isPay;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("请求单登记时间")
    private Date registerTime;

}
