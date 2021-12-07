package com.rdexpense.manager.dto.disclosurePaper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "项目交底书查询列表")
public class DisclosurePaperSearchDTO extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "单据编号")
    private  String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private  String projectName;

    @ApiModelProperty(value = "项目负责人")
    private  String projectLeaderName;

    @ApiModelProperty(value = "岗位")
    private  String leaderPostName;

    @ApiModelProperty(value = "联系电话")
    private  String contactNumber;

    @ApiModelProperty(value = "交底书编制人")
    private  String preparedName;

    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private  Date preparedDate;

    @ApiModelProperty(value = "编制年度")
    private  String preparedYear;

    @ApiModelProperty(value = "编制季度")
    private  String preparedQuarter;


    @ApiModelProperty(value = "编制人")
    private  String creatorUserName;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
