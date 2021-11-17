package com.rdexpense.manager.dto.system.login;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "操作日志")
@Data
public class UqOperationLogDTO implements Serializable {
    /**
     * 主键Id
     */
    @ApiModelProperty(value = "主键Id")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**
     * 用户岗位id
     */
    @ApiModelProperty(value = "用户岗位id")
    private String userPositionId;

    /**
     * 用户岗位名称
     */
    @ApiModelProperty(value = "用户岗位名称")
    private String userPositionName;

    /**
     * 用户登陆ip
     */
    @ApiModelProperty(value = "用户登陆ip")
    private String loginIp;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型")
    private String operateType;



    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容")
    private String operateContent;

    /**
     * 菜单编码Id
     */
    @ApiModelProperty(value = "菜单编码Id")
    private String menuId;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    /**
     * 用户机构编码Id
     */
    @ApiModelProperty(value = "用户机构编码Id")
    private String userOrgId;

    /**
     * 用户机构名称
     */
    @ApiModelProperty(value = "用户机构名称")
    private String userOrgName;

    /**
     * 用户部门id
     */
    @ApiModelProperty(value = "用户部门id")
    private String userDepartId;

    /**
     * 用户部门名称
     */
    @ApiModelProperty(value = "用户部门名称")
    private String userDepartName;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}

