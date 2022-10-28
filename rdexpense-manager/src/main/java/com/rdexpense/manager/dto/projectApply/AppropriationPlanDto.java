package com.rdexpense.manager.dto.projectApply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "拨款计划")
public class AppropriationPlanDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "年度")
    private String years;

    @ApiModelProperty(value = "计划(万元)")
    private String planAmount;

    @ApiModelProperty(value = "编制人ID")
    private String creatorUserId;

    @ApiModelProperty(value = "编制人")
    private String creatorUser;

    @ApiModelProperty(value = "编制时间")
    private String createTime;
}
