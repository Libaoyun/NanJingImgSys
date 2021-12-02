package com.rdexpense.manager.dto.base;

import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/6/3 13:55
 * @describe 审批通用方法
 */
@ApiModel
@Data
public class FlowApproveDto extends OrganizationDto implements Serializable {


    @ApiModelProperty(value = "待办列表waitId")
    private String waitId;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "审批意见")
    private String approveComment;

    @ApiModelProperty(value = "审批类型 1:同意 2:回退上一个节点 3：回退到发起人")
    private String approveType;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;
}
