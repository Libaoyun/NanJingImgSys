package com.imgSys.dto.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: 患者登记列表
 * @author: Libaoyun
 * @date: 2022-11-04 14:56
 **/

@Data
@ApiModel("患者信息")
public class RegisterList {

//    @ApiModelProperty("id")
//    private Integer id;

    @ApiModelProperty("患者姓名")
    private String name;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    @ApiModelProperty("就诊卡号")
    private String patientIdCardNo;

    @ApiModelProperty("门诊卡号")
    private String outpatientNo;

    @ApiModelProperty("身份证号(医保卡号)")
    private String identifierId;

    @ApiModelProperty("图像采集单序列号")
    private String formSerialNum;

    @ApiModelProperty("序列号")
    private String serialNum;

    @ApiModelProperty("检查单开具科室名称（诊断科室）")
    private String departmentName;

    @ApiModelProperty("诊断医师姓名")
    private String formIssuerName;

    @ApiModelProperty("采集人姓名")
    private String collectors;

    @ApiModelProperty("采集部位名称")
    private String partName;

    @ApiModelProperty("是否采集完成")
    private String isCollect;

    @ApiModelProperty("是否分配设备")
    private String isDistribute;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("请求单填写时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("请求单登记时间")
    private Date registerTime;

}
