package com.rdexpense.manager.dto.projectApply;

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
@ApiModel(value = "立项申请查询入参")
public class ProjectApplySearchDto implements Serializable {

    @ApiModelProperty(value = "右上角项目ID", required = true)
    private String creatorOrgId;

    @ApiModelProperty(value = "单据号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "流程状态集合，传编码")
    private List<String> statusList;

    @ApiModelProperty(value = "当前审批人")
    private String approveUserName;

    @ApiModelProperty(value = "申请人")
    private String applyUserName;

    @ApiModelProperty(value = "岗位")
    private String postName;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "项目类型编码")
    private List<String> projectTypeCode;

    @ApiModelProperty(value = "专业类别编码")
    private List<String> professionalCategoryCode;

    @ApiModelProperty(value = "研究内容")
    private String researchContents;

    @ApiModelProperty(value = "起始年度")
    private String startYear;

    @ApiModelProperty(value = "结束年度")
    private String endYear;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "编制人")
    private String createUser;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
