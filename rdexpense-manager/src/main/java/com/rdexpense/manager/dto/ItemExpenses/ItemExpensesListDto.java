package com.rdexpense.manager.dto.ItemExpenses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "查询研发项目费用支出列表")
public class ItemExpensesListDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "流程状态；可取字典表数据")
    private String processStatus;

    @ApiModelProperty(value = "流程状态名称；可取字典表数据")
    private String processName;

    @ApiModelProperty(value = "所属月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date belongingMonth;

    @ApiModelProperty(value = "申请人ID")
    private String createUserId;

    @ApiModelProperty(value = "申请人")
    private String createUser;

    @ApiModelProperty(value = "研发项目支出一级科目编码")
    private String firstSubjectCode;

    @ApiModelProperty(value = "研发项目支出一级科目")
    private String firstSubject;

    @ApiModelProperty(value = "研发项目支出二级科目编码")
    private String twoSubjectCode;

    @ApiModelProperty(value = "研发项目支出二级科目")
    private String twoSubject;

    @ApiModelProperty(value = "支出依据(长度：1024)")
    private String payNoted;

    @ApiModelProperty(value = "本次申请列销金额（元）")
    private String budgetAmount;

    @ApiModelProperty(value = "本次申请后结余金额（元）")
    private String budgetBalance;

    @ApiModelProperty(value = "当前审批人(暂时未用，先保留)")
    private String approveUserName;

    @ApiModelProperty(value = "当前审批人ID(暂时未用，先保留)")
    private String approveUserId;

    @ApiModelProperty(value = "创建时间(暂时未用，先保留)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间(暂时未用，先保留)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "流程状态集合，传编码(暂时未用，先保留)")
    private List<String> statusList;

}
