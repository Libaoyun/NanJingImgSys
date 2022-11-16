package com.imgSys.dto.img;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "患者信息")
public class PatientInfoDto extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "就诊卡号")
    private String patientIdCardNo;

    @ApiModelProperty(value = "患者姓名")
    private String name;

    @ApiModelProperty(value = "患者生日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    @ApiModelProperty(value = "患者性别")
    private String gender;

    @ApiModelProperty(value = "教育程度")
    private String educationDegree;

    @ApiModelProperty(value = "电话号码")
    private String phoneNum;

    @ApiModelProperty(value = "家庭地址")
    private String homeAddress;

    @ApiModelProperty(value = "身份证号")
    private String identifierId;

    @ApiModelProperty(value = "激光号")
    private String imgLaserId;

    @ApiModelProperty(value = "门诊号")
    private String outpatientNo;
}
