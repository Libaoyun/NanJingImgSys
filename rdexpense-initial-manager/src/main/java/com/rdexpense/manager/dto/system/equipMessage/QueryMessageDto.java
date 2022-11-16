package com.rdexpense.manager.dto.system.equipMessage;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class QueryMessageDto implements Serializable {

    @ApiModelProperty(value = "工程编码")
    private String creatorOrgId;

    @ApiModelProperty(value = "工程名称")
    private String creatorOrgName;

    @ApiModelProperty(value = "消息内容")
    private String newsContent;

    @ApiModelProperty(value = "消息分类编码")
    private List<String> statusList;

    @ApiModelProperty(value = "管理号码")
    private String manageNumber;

    @ApiModelProperty(value = "设备名称")
    private String equipmentStandardName;

    @ApiModelProperty(value = "管理单位")
    private String managementUnit;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
