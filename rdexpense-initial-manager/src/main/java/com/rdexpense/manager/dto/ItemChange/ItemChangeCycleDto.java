package com.rdexpense.manager.dto.ItemChange;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "研发项目周期变更")
public class ItemChangeCycleDto implements Serializable {
    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "数据状态，0：变更前，1：变更后")
    private String dataStatus;

    @ApiModelProperty(value = "开始年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startYear;

    @ApiModelProperty(value = "结束年度")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endYear;

    @ApiModelProperty(value = "变更人ID")
    private String cycleUpdateUserId;

    @ApiModelProperty(value = "变更人")
    private String cycleUpdateUser;

    @ApiModelProperty(value = "变更日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cycleUpdateTime;

}
