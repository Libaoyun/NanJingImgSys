package com.rdexpense.manager.dto.ItemChange;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import com.rdexpense.manager.dto.file.CreateFileDto;
import com.rdexpense.manager.dto.projectApply.BudgetDetailDto;
import com.rdexpense.manager.dto.projectApply.ResearchUserDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "查询研发项目变更申请详情")
public class ItemChangeDetailDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "提交时传参，1：列表提交，2：保存提交，3：编辑提交")
    private String flag;

    @ApiModelProperty(value = "主键ID(编辑时传参)")
    private String id;

    @ApiModelProperty(value = "业务主键ID(编辑时传参)")
    private String businessId;

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "研发项目申请表主键ID")
    private String projectApplyMainId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "创建单位ID")
    private String creatorOrgId;

    @ApiModelProperty(value = "创建单位")
    private String creatorOrg;

    @ApiModelProperty(value = "编制人ID")
    private String createUserId;

    @ApiModelProperty(value = "编制人")
    private String createUser;

    @ApiModelProperty(value = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createdDate;

    @ApiModelProperty(value = "开始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;

    @ApiModelProperty(value = "项目负责人")
    private String applyUserName;

    @ApiModelProperty(value = "项目负责人ID")
    private String applyUserId;

    @ApiModelProperty(value = "审批实例ID")
    private String processInstId;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "变更类型")
    private String changeType ;

    @ApiModelProperty(value = "变更类型编码")
    private String changeTypeCode;

    @ApiModelProperty(value = "要求变更的原项目相关部分内容(长度：1024)")
    private String partContent;

    @ApiModelProperty(value = "要求变更的内容或建议(长度：1024)")
    private String changeAdvise;

    @ApiModelProperty(value = "变更理由(长度：1024)")
    private String changeReason;

    @ApiModelProperty(value = "实施情况(长度：1024)")
    private String implementation;

    @ApiModelProperty(value = "经费使用情况(长度：1024)")
    private String fundsUse;

    @ApiModelProperty(value = "是否为局级 0：否,1：是(长度：8)")
    private String bureauLevel;

    @ApiModelProperty(value = "研发项目的支出最大月份值，根据研发项目ID，不依赖二级科目(长度：32)")
    private String belongingMonth;

    @ApiModelProperty(value = "研发周期变更列表")
    private List<ItemChangeCycleDto> cycleList;

    @ApiModelProperty(value = "研发经费支出变更列表")
    private List<ItemChangeBudgetDetailDto> budgetDetailList;

    @ApiModelProperty(value = "研发人员变更列表")
    private List<ResearchUserDto> userInfoList;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;
}
