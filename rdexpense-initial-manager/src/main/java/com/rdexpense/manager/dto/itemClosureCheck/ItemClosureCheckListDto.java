package com.rdexpense.manager.dto.itemClosureCheck;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "查询研发项目结题验收列表")
public class ItemClosureCheckListDto extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "成果名称")
    private String jobTitle;

    @ApiModelProperty(value = "流程状态；可取字典表数据")
    private String processStatus;

    @ApiModelProperty(value = "流程状态名称；可取字典表数据")
    private String processName;

    @ApiModelProperty(value = "当前审批人")
    private String nextApproveUserName;

    @ApiModelProperty(value = "当前审批人ID")
    private String nextApproveUserId;

    @ApiModelProperty(value = "申请评审验收单位ID")
    private String creatorOrgId;

    @ApiModelProperty(value = "申请评审验收单位")
    private String creatorOrg;

    @ApiModelProperty(value = "结题申报人ID/编制人ID")
    private String createUserId;

    @ApiModelProperty(value = "结题申报人/编制人")
    private String createUser;

    @ApiModelProperty(value = "申请评审日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createdDate;

    @ApiModelProperty(value = "项目负责人")
    private String applyUserName;

    @ApiModelProperty(value = "项目负责人ID")
    private String applyUserId;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "职务编码")
    private String postCode;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;

    @ApiModelProperty(value = "任务来源(长度：1024)")
    private String taskSource;

    @ApiModelProperty(value = "成果内容简介(长度：1024)")
    private String projectAbstract;

    @ApiModelProperty(value = "经济技术文件目录及提供单位(长度：1024)")
    private String directoryAndUnit;

    @ApiModelProperty(value = "申请评审单位意见(长度：1024)")
    private String checkRemark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "审批实例ID")
    private String processInstId;

    @ApiModelProperty(value = "所属单位名称(长度：256)")
    private String unitName;

    @ApiModelProperty(value = "申请评审日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date checkDate;

    @ApiModelProperty(value = "流程状态集合，传编码")
    private List<String> statusList;

}
