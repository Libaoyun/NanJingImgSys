package com.rdexpense.manager.dto.attendance;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "分摊表")
public class ShareListAddDto implements Serializable {

    @ApiModelProperty(value = "序号")
    private String number;

    @ApiModelProperty(value = "姓名")
    private String username;

    @ApiModelProperty(value = "身份证")
    private String idcard;

    @ApiModelProperty(value = "职务职称")
    private String postname;

    @ApiModelProperty(value = "星期日期")
    private String weekday;

    @ApiModelProperty(value = "1号")
    private String day_四_1;

    @ApiModelProperty(value = "2号")
    private String day_四_2;

    @ApiModelProperty(value = "3号")
    private String day_四_3;

    @ApiModelProperty(value = "4号")
    private String day_四_4;

    @ApiModelProperty(value = "5号")
    private String day_四_5;

    @ApiModelProperty(value = "6号")
    private String day_四_6;

    @ApiModelProperty(value = "7号")
    private String day_四_7;

    @ApiModelProperty(value = "8号")
    private String day_四_8;

    @ApiModelProperty(value = "9号")
    private String day_四_9;

    @ApiModelProperty(value = "10号")
    private String day_四_10;

    @ApiModelProperty(value = "11号")
    private String day_四_11;

    @ApiModelProperty(value = "12号")
    private String day_四_12;

    @ApiModelProperty(value = "13号")
    private String day_四_13;

    @ApiModelProperty(value = "14号")
    private String day_四_14;

    @ApiModelProperty(value = "15号")
    private String day_四_15;

    @ApiModelProperty(value = "16号")
    private String day_四_16;

    @ApiModelProperty(value = "17号")
    private String day_四_17;

    @ApiModelProperty(value = "18号")
    private String day_四_18;

    @ApiModelProperty(value = "19号")
    private String day_四_19;

    @ApiModelProperty(value = "20号")
    private String day_四_20;

    @ApiModelProperty(value = "21号")
    private String day_四_21;

    @ApiModelProperty(value = "22号")
    private String day_四_22;

    @ApiModelProperty(value = "23号")
    private String day_四_23;

    @ApiModelProperty(value = "24号")
    private String day_四_24;

    @ApiModelProperty(value = "25号")
    private String day_四_25;

    @ApiModelProperty(value = "26号")
    private String day_四_26;

    @ApiModelProperty(value = "27号")
    private String day_四_27;

    @ApiModelProperty(value = "28号")
    private String day_四_28;

    @ApiModelProperty(value = "29号")
    private String day_四_29;

    @ApiModelProperty(value = "30号")
    private String day_四_30;

    @ApiModelProperty(value = "31号")
    private String day_四_31;

    @ApiModelProperty(value = "天数")
    private String days;

    @ApiModelProperty(value = "总天数")
    private String dayssum;

    @ApiModelProperty(value = "该课题出勤率")
    private String attendrate;

    @ApiModelProperty(value = "应付工资")
    private String wagessalary;

    @ApiModelProperty(value = "养老保险")
    private String endowmentinsurance;

    @ApiModelProperty(value = "医疗保险")
    private String medicalinsurance;

    @ApiModelProperty(value = "失业保险")
    private String unemploymentinsurance;

    @ApiModelProperty(value = "工伤保险")
    private String injuryinsurance;

    @ApiModelProperty(value = "生育保险")
    private String maternityinsurance;

    @ApiModelProperty(value = "公积金")
    private String accumulationfund;

    @ApiModelProperty(value = "企业年金")
    private String yearfund;

    @ApiModelProperty(value = "本课题应付工资")
    private String topicwagessalary;

    @ApiModelProperty(value = "本课题养老保险")
    private String topicendowmentinsurance;

    @ApiModelProperty(value = "本课题医疗保险")
    private String topicmedicalinsurance;

    @ApiModelProperty(value = "本课题失业保险")
    private String topicunemploymentinsurance;

    @ApiModelProperty(value = "本课题工伤保险")
    private String topicinjuryinsurance;

    @ApiModelProperty(value = "本课题生育保险")
    private String topicmaternityinsurance;

    @ApiModelProperty(value = "本课题公积金")
    private String topicaccumulationfund;

    @ApiModelProperty(value = "本课题企业年金")
    private String topicyearfund;

    @ApiModelProperty(value = "本课题五险一金合计")
    private String topicinsurancesum;


}
