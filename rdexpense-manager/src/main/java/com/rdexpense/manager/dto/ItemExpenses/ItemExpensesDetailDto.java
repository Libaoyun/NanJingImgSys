package com.rdexpense.manager.dto.ItemExpenses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "查询研发项目费用支出列表")
public class ItemExpensesDetailDto {

    @ApiModelProperty(value = "提交时传参，1：列表提交，2：保存提交，3：编辑提交")
    private String flag;

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

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目负责人")
    private String applyUserName;

    @ApiModelProperty(value = "项目负责人ID")
    private String applyUserId;

    @ApiModelProperty(value = "所属月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date belongingMonth;

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

    @ApiModelProperty(value = "预算总额(18,2)")
    private String budgetAmount;

    @ApiModelProperty(value = "已累计支出(18,2)")
    private String accumulatedExpenditure;

    @ApiModelProperty(value = "预算结余(18,2)")
    private String budgetBalance;

    @ApiModelProperty(value = "本次金额(元)(18,2)")
    private String amount;

    @ApiModelProperty(value = "结余金额(元)(18,2)")
    private String balanceAmount;

    @ApiModelProperty(value = "申报意见(长度：1024)")
    private String remark;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;
}
