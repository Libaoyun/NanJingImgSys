package com.rdexpense.manager.dto.analysis;

import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import com.rdexpense.manager.dto.materialRequisition.MaterialRequisitionDetailedDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:00
 */
@Data
@ApiModel(value = "编辑其他事项")
public class ExpensesBillSummaryUpdateDto extends OrganizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "主键ID(列表里返回的ID)")
    private String id;

    @ApiModelProperty(value = "年份")
    private String years;

    @ApiModelProperty(value = "允许加计扣除金额合计")
    private String totalAmount;

    @ApiModelProperty(value = "人员人工费用")
    private String userAmount;

    @ApiModelProperty(value = "直接投入费用")
    private String inputAmount;

    @ApiModelProperty(value = "折旧费用")
    private String depreciationAmount;

    @ApiModelProperty(value = "无形资产摊销")
    private String amortizationAmount;

    @ApiModelProperty(value = "新产品设计费等")
    private String desginAmount;

    @ApiModelProperty(value = "前五项小计")
    private String itemSumAmount;

    @ApiModelProperty(value = "其他相关费用合计")
    private String otherAmount;

    @ApiModelProperty(value = "经限额调整后的其他相关费用")
    private String adjustOtherAmount;

    @ApiModelProperty(value = "境内活动费")
    private String territoryAmount;

    @ApiModelProperty(value = "允许加计后的境内活动费")
    private String adjustTerritoryAmount;

    @ApiModelProperty(value = "境外活动费")
    private String abroadAmount;

    @ApiModelProperty(value = "调整后的境外活动费")
    private String adjustAbroadAmount;

}
