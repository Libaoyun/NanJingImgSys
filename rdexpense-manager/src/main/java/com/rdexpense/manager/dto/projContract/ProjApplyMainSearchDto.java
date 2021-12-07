package com.rdexpense.manager.dto.projContract;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "立项项目列表查询入参")
public class ProjApplyMainSearchDto extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "申请单号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "申请人")
    private String applyUserName;

    @ApiModelProperty(value = "申请人ID")
    private String applyUserId;

    @ApiModelProperty(value = "岗位编码")
    private String postCode;

    @ApiModelProperty(value = "岗位")
    private String postName;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "项目类别编码")
    private String projectTypeCode;

    @ApiModelProperty(value = "项目类别")
    private String projectType;

    @ApiModelProperty(value = "研究内容")
    private String researchContents;

    @ApiModelProperty(value = "申请经费")
    private String applyAmount;

    @ApiModelProperty(value = "密级代码")
    private String secretsCode;

    @ApiModelProperty(value = "合同状态码")
    private String contractStatusCode;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
