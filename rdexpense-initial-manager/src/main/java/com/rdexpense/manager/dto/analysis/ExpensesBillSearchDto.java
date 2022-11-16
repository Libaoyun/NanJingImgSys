package com.rdexpense.manager.dto.analysis;

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
@ApiModel(value = "项目支出辅助账查询入参")
public class ExpensesBillSearchDto implements Serializable {

    @ApiModelProperty(value = "右上角项目ID", required = true)
    private String creatorOrgId;

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "年度")
    private String years;

    @ApiModelProperty(value = "完成情况编码")
    private List<String> completionStatusCode;

    @ApiModelProperty(value = "支出类型编码")
    private List<String> expensesTypeCode;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "编制人")
    private String creatorUserName;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
