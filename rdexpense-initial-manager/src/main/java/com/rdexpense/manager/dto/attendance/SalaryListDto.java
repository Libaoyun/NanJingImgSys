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
@ApiModel(value = "工资表")
public class SalaryListDto implements Serializable {

    @ApiModelProperty(value = "序号")
    private String number;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "职务职称")
    private String postName;

    @ApiModelProperty(value = "工资")
    private String salary;

    @ApiModelProperty(value = "养老保险")
    private String endowmentInsurance;

    @ApiModelProperty(value = "医疗保险")
    private String medicalInsurance;

    @ApiModelProperty(value = "失业保险")
    private String unemploymentInsurance;

    @ApiModelProperty(value = "工商保险")
    private String injuryInsurance;

    @ApiModelProperty(value = "生育保险")
    private String maternityInsurance;

    @ApiModelProperty(value = "公积金")
    private String accumulationFund;

    @ApiModelProperty(value = "企业年金")
    private String yearFund;

}
