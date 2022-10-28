package com.rdexpense.manager.dto.projContract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2021/11/28 8:50
 */
@Data
@ApiModel(value = "项目合同列表查询入参")
public class ProjContractSearchDto implements Serializable {
    @ApiModelProperty(value = "合同编号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /*@ApiModelProperty(value = "密级代码")
    private String secretsCode;*/

    @ApiModelProperty(value = "合同密级代码列表")
    //private String secretsName;
    private List<String> secretsCodeList;

    /*@ApiModelProperty(value = "合同状态码")
    private String contractStatusCode;*/

    @ApiModelProperty(value = "合同状态列表")
    //private String contractStatusName;
    private List<String> contractStatusCodeList;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    /*@ApiModelProperty(value = "负责人岗位代码")
    private String leaderPostId;*/

    @ApiModelProperty(value = "岗位")
    private String leaderPostName;

    @ApiModelProperty(value = "所属单位名称")
    private String unitName;

    @ApiModelProperty(value = "编制人")
    private String creatorUserName;

    @ApiModelProperty(value = "起始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "编制人")
    private String createUserName;

    @ApiModelProperty(value = "创建日期")
    private Date createdDate;

    @ApiModelProperty(value = "当前页码,值需大于等于1", required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1", required=true)
    private Integer pageSize;
}
