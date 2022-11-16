package com.rdexpense.manager.dto.equipmentDepreciationExpenses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.SettlementDetailDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "设备折旧支出单管理详情")
public class EquipmentDepreciationExpensesDetailDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "主键ID(编辑时传参)")
    private String id;

    @ApiModelProperty(value = "业务主键ID(编辑时传参)")
    private String businessId;

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "申请人ID")
    private String createUserId;

    @ApiModelProperty(value = "申请人")
    private String createUser;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createdDate;

    @ApiModelProperty(value = "研发项目申请表业务主键businessId")
    private String projectApplyMainId;

    @ApiModelProperty(value = "项目名称(长度：256)")
    private String projectName;

    @ApiModelProperty(value = "项目负责人")
    private String applyUserName;

    @ApiModelProperty(value = "项目负责人ID")
    private String applyUserId;

    @ApiModelProperty(value = "所属月份")
    private String belongingMonth;

    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;

    @ApiModelProperty(value = "备注(长度：1024)")
    private String remark;

    @ApiModelProperty(value = "不含税总金额（元）(长度：24：2)")
    private String contractAddAmountWithoutTax;

    @ApiModelProperty(value = "设备折旧支出单明细")
    private List<DepreciationExpensesDetailDto> depreciationExpensesDetailList;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;
}
