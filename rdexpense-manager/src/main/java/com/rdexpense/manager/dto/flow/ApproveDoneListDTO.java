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
 * @description 待办已办
 */
@ApiModel
@Data
public class ApproveDoneListDTO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "流程实例ID")
    private String processInstId;

    @ApiModelProperty(value = "审批节点")
    private String approveNodeName;

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "标题名称")
    private String titleName;

    @ApiModelProperty(value = "发起人ID")
    private String createUserId;

    @ApiModelProperty(value = "发起人")
    private String createUser;

    @ApiModelProperty(value = "下一个审批人员ID")
    private String nextApproveUserId;

    @ApiModelProperty(value = "下一个审批人员")
    private String nextApproveUserName;

    @ApiModelProperty(value = "下一个审批节点ID")
    private String nextApproveNodeId;

    @ApiModelProperty(value = "下一个审批节点")
    private String nextApproveNodeName;

    @ApiModelProperty(value = "发起时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "审批时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveTime;

    @ApiModelProperty(value = "是否被被打回 1：是 其他：否")
    private String backFlag;


}
