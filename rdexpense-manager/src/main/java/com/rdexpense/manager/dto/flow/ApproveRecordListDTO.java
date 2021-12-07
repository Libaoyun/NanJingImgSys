package com.rdexpense.manager.dto.flow;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 审批记录
 */
@ApiModel
@Data
public class ApproveRecordListDTO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "流程实例ID")
    private String processInstId;

    @ApiModelProperty(value = "审批人ID")
    private String approveUserId;

    @ApiModelProperty(value = "审批人")
    private String approveUserName;

    @ApiModelProperty(value = "审批节点ID")
    private String approveNodeId;

    @ApiModelProperty(value = "审批节点名称")
    private String approveNodeName;

    @ApiModelProperty(value = "审批意见")
    private String approveComment;

    @ApiModelProperty(value = "审批结果编码")
    private String approveResultId;

    @ApiModelProperty(value = "审批结果")
    private String approveResultName;

    @ApiModelProperty(value = "下一个审批人员ID")
    private String nextApproveUserId;

    @ApiModelProperty(value = "下一个审批人员")
    private String nextApproveUserName;

    @ApiModelProperty(value = "下一个审批节点ID")
    private String nextApproveNodeId;

    @ApiModelProperty(value = "下一个审批节点")
    private String nextApproveNodeName;

    @ApiModelProperty(value = "审批开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveStartTime;

    @ApiModelProperty(value = "审批结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveEndTime;

    @ApiModelProperty(value = "附件ID")
    private String fileId;

    @ApiModelProperty(value = "附件名称")
    private String fileName;

    @ApiModelProperty(value = "部门职务编码")
    private String departmentCode;

    @ApiModelProperty(value = "部门职务名称")
    private String departmentName;

}
