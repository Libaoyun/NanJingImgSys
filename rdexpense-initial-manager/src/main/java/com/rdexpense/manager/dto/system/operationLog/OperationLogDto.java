package com.rdexpense.manager.dto.system.operationLog;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "操作日志模糊实体传参")
@Data
public class OperationLogDto implements Serializable {

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户登陆Ip")
    private String loginIp;

    @ApiModelProperty(value = "操作类型")
    private String operateType;

    @ApiModelProperty(value = "操作内容")
    private String operateContent;

    @ApiModelProperty(value = "所属机构名称")
    private String userOrgName;

    @ApiModelProperty(value = "所属部门名称")
    private String userDepartName;

    @ApiModelProperty(value = "开始日期",example="yyyy-MM-dd")
    private String beginTime;

    @ApiModelProperty(value = "结束日期",example="yyyy-MM-dd")
    private String endTime;

    @ApiModelProperty(value = "页码,值需大于等于1", required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1", required = true)
    private Integer pageSize;


    private static final long serialVersionUID = 1L;
}

