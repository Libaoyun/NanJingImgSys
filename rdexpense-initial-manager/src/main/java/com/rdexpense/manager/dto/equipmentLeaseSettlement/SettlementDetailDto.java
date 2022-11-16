package com.rdexpense.manager.dto.equipmentLeaseSettlement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "查询租赁设备结算管理明细")
public class SettlementDetailDto extends BaseEntity  implements Serializable {

    @ApiModelProperty(value = "业务主键ID(编辑时传参)")
    private String businessId;

    @ApiModelProperty(value = "合同编号(长度：128)")
    private String contractCode;

    @ApiModelProperty(value = "设备名称(长度：128)")
    private String equipName;

    @ApiModelProperty(value = "设备名称编码(长度：128)")
    private String equipCode;

    @ApiModelProperty(value = "管理号码(长度：128)")
    private String manageCode;

    @ApiModelProperty(value = "规格型号(长度：32)")
    private String spec;

    @ApiModelProperty(value = "结算起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date settlementStartTime;

    @ApiModelProperty(value = "结算截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date settlementEndTime;

    @ApiModelProperty(value = "租赁方式编码(长度：32)")
    private String rentTypeCode;

    @ApiModelProperty(value = "租赁方式(长度：32)")
    private String rentType;

    @ApiModelProperty(value = "计费方式编码(长度：32)")
    private String feeTypeCode;

    @ApiModelProperty(value = "计费方式(长度：32)")
    private String feeType;

    @ApiModelProperty(value = "不含税单价(长度：24:8)")
    private String unitPriceExcludingTax;

    @ApiModelProperty(value = "含税单价(长度：24:8)")
    private String priceExcludingTax;

    @ApiModelProperty(value = "租期/工作量(长度：18:2)")
    private String plannedQuantity;

    @ApiModelProperty(value = "税率(长度：24:2)")
    private String taxRate;

    @ApiModelProperty(value = "税额(长度：24:2)")
    private String tax;

    @ApiModelProperty(value = "其他费用(长度：24:2)")
    private String otherFees;

    @ApiModelProperty(value = "其他费用说明(长度：256)")
    private String otherFeesCost;

    @ApiModelProperty(value = "扣款金额(长度：24:2)")
    private String deductionsMoney;

    @ApiModelProperty(value = "扣款金额说明(长度：256)")
    private String deductionsMoneyCost;

    @ApiModelProperty(value = "结算金额（不含税）（元）(长度：24:2)")
    private String settlementAmountExcludingTax;

    @ApiModelProperty(value = "结算金额（含税）（元）(长度：24:2)")
    private String settlementAmount;

    @ApiModelProperty(value = "款项性质编码(长度：64)")
    private String naturePaymentCode;

    @ApiModelProperty(value = "款项性质名称(长度：255)")
    private String naturePaymentName;

    @ApiModelProperty(value = "费用项目编码(长度：64)")
    private String costItemCode;

    @ApiModelProperty(value = "费用项目名称(长度：255)")
    private String costItemName;

    @ApiModelProperty(value = "导入模块数据标识，0：导入数据；1：页面新增数据")
    private String paramSource;

}
