package com.rdexpense.manager.dto.materialRequisition;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "领料单查询列表")
public class MaterialRequisitionListDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "流程状态；可取字典表数据")
    private String processStatus;

    @ApiModelProperty(value = "流程状态名称；可取字典表数据")
    private String processName;

    @ApiModelProperty(value = "不含税总金额")
    private String noTaxAmount;

    @ApiModelProperty(value = "所属月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date belongMonth;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeader;

    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    @ApiModelProperty(value = "备注")
    private String remark;

}
