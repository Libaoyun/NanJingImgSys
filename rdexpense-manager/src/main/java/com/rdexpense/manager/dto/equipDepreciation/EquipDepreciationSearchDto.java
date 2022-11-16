package com.rdexpense.manager.dto.equipDepreciation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "设备折旧单列表查询")
// 设备折旧单列表查询
public class EquipDepreciationSearchDto extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "单据编号")
    private String serialNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "单据状态")
    private String processStatus;

    @ApiModelProperty(value = "当前审批人")
    private String approveUserName;

    @ApiModelProperty(value = "所属月份")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date monthBelongTo;

    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "编制人")
    private String creatorUserName;
}
