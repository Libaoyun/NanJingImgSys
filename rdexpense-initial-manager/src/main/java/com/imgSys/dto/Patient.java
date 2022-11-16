package com.imgSys.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 患者基本信息
 * @author: Libaoyun
 * @date: 2022-10-28 19:17
 **/
@Data
@ApiModel("患者信息")
public class Patient implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("就诊卡号")
    private String patientIdCardNo;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("教育程度")
    private String educationDegree;

    @ApiModelProperty("手机号码")
    private String phoneNum;

    @ApiModelProperty("住址")
    private String homeAddress;

    @ApiModelProperty("身份证号(医保卡号)")
    private String identifierId;

    @ApiModelProperty("门诊卡号")
    private String outpatientNo;

    @ApiModelProperty("激光号(关联旧数据)")
    private String imgLaserId;

}
