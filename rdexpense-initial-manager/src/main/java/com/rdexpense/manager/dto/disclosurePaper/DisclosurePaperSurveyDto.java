package com.rdexpense.manager.dto.disclosurePaper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "经济交底书详情")
public class DisclosurePaperSurveyDto implements Serializable {
    @ApiModelProperty(value = "关键（核心）技术 ")
    private  String keyTechnology;

    @ApiModelProperty(value = "工作（进度）安排")
    private  String workPlan;

    @ApiModelProperty(value = "人工安排 ")
    private  String artificialPlan;

    @ApiModelProperty(value = "设备安排 ")
    private  String equipmentPlan;

    @ApiModelProperty(value = "调试安排 ")
    private  String testPlan;

    @ApiModelProperty(value = "其他安排")
    private  String otherPlan;

    @ApiModelProperty(value = "费用安排")
    private  String costPlan;

    @ApiModelProperty(value = "项目名称")
    private  String projectName;

}
