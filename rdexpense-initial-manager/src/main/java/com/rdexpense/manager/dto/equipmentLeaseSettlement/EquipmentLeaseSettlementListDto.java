package com.rdexpense.manager.dto.equipmentLeaseSettlement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "查询租赁设备结算管理列表")
public class EquipmentLeaseSettlementListDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "流程状态；可取字典表数据")
    private String processStatus;

    @ApiModelProperty(value = "流程状态名称；可取字典表数据")
    private String processName;

    @ApiModelProperty(value = "当前审批人")
    private String nextApproveUserName;

    @ApiModelProperty(value = "当前审批人ID")
    private String nextApproveUserId;

    @ApiModelProperty(value = "不含税总金额（元）(长度：24：8)")
    private String contractAddAmountWithoutTax;

    @ApiModelProperty(value = "所属月份")
    private String belongingMonth;

    @ApiModelProperty(value = "项目负责人")
    private String applyUserName;

    @ApiModelProperty(value = "项目负责人ID")
    private String applyUserId;

    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    @ApiModelProperty(value = "备注(长度：1024)")
    private String remark;

    @ApiModelProperty(value = "申请人ID")
    private String createUserId;

    @ApiModelProperty(value = "申请人(搜索查询时，使用selectCreateUser字段传参)")
    private String createUser;

    @ApiModelProperty(value = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "流程状态集合，传编码")
    private List<String> statusList;

}
