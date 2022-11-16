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
@ApiModel(value = "立项调研信息")
public class SurveyInfoDto implements Serializable {

    @ApiModelProperty(value = "国内外现状")
    private String currentSituation;

    @ApiModelProperty(value = "研发目的和意义")
    private String purposeSignificance;

    @ApiModelProperty(value = "主要研究内容及研究方法")
    private String contentMethod;

    @ApiModelProperty(value = "要达到的目标、成果形式及主要技术指标")
    private String targetResults;

    @ApiModelProperty(value = "现有研发条件和工作基础")
    private String basicConditions;

    @ApiModelProperty(value = "研发项目创新点")
    private String innovationPoints;

    @ApiModelProperty(value = "成果转化的可行性分析")
    private String feasibilityAnalysis;
}
