package com.rdexpense.manager.dto.flow;

import com.common.entity.PageData;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.OrganizationDto;
import com.rdexpense.manager.dto.file.CreateFileDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 编辑流程
 */
@ApiModel
@Data
public class FlowSerialDataDTO extends OrganizationDto implements Serializable {

    @ApiModelProperty(value = "主键ID", required = true)
    private String id;

    @ApiModelProperty(value = "流程设置的ID", required = true)
    private String flowId;

    @ApiModelProperty(value = "单据号", required = true)
    private String serialNumber;

    @ApiModelProperty(value = "流程内容", required = true)
    private PageData flowContent;

    @ApiModelProperty(value = " 状态  1：已启动，2：运行中，3、已结束", required = true)
    private String status;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "附件列表")
    private List<CreateFileDto> attachmentList;

}
