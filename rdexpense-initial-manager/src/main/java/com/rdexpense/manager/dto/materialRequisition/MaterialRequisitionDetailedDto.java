package com.rdexpense.manager.dto.materialRequisition;


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
@ApiModel(value = "领料单明细")
public class MaterialRequisitionDetailedDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "数据来源 0：文件导入1：页面新建")
    private String paramSource;

    @ApiModelProperty(value = "材料名称")
    private String materialName;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "计量单位")
    private String unit;

    @ApiModelProperty(value = "数量")
    private String number;

    @ApiModelProperty(value = "不含税单价")
    private String noTaxPrice;

    @ApiModelProperty(value = "不含税金额")
    private String noTaxAmount;

    @ApiModelProperty(value = "备注")
    private String remark;


}
