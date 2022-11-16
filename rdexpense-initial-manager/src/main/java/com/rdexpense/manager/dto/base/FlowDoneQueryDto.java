package com.rdexpense.manager.dto.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "待办已办查询入参")
public class FlowDoneQueryDto implements Serializable {


    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "所属模块")
    private String titleName;

    @ApiModelProperty(value = "发起时间")
    private String createTime;

    @ApiModelProperty(value = "当前审批人")
    private String nextApproveUserName;

    @ApiModelProperty(value = "发起人")
    private String creatorUserName;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
