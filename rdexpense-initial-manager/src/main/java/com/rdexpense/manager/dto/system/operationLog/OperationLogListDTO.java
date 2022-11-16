package com.rdexpense.manager.dto.system.operationLog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "操作日志")
@Data
public class OperationLogListDTO implements Serializable {

    @ApiModelProperty(value = "主键Id")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户岗位id")
    private String userPositionId;

    @ApiModelProperty(value = "用户岗位名称")
    private String userPositionName;

    @ApiModelProperty(value = "用户登陆ip")
    private String loginIp;

    @ApiModelProperty(value = "操作类型")
    private String operateType;

    @ApiModelProperty(value = "操作内容")
    private String operateContent;

    @ApiModelProperty(value = "菜单编码Id")
    private String menuId;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "用户机构编码Id")
    private String userOrgId;

    @ApiModelProperty(value = "用户机构名称")
    private String userOrgName;

    @ApiModelProperty(value = "用户部门id")
    private String userDepartId;

    @ApiModelProperty(value = "用户部门名称")
    private String userDepartName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}

