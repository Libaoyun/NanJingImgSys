package com.rdexpense.manager.dto.base;



import com.rdexpense.manager.dto.system.permission.PermissionOrganizationListDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;


@Data
@ApiModel(description = "用户对象USER")
public class UserInfoDTO implements Serializable {

    @ApiModelProperty(value = "用户编码",required = true)
    private String userCode;

    @ApiModelProperty(value = "姓名", required = true)
    private String userName;

    @ApiModelProperty(value = "部门", required = true)
    private String department;

    @ApiModelProperty(value = "部门ID", required = true)
    private String departmentId;

    @ApiModelProperty(value = "职务", required = true)
    private String post;

    @ApiModelProperty(value = "职务ID", required = true)
    private String postId;

    @ApiModelProperty(value = "所属公司ID", required = true)
    private String companyId;

    @ApiModelProperty(value = "已授权的组织")
    private List<PermissionOrganizationListDTO> organizationList;

    @ApiModelProperty(value = "token", required = true)
    private String token;

    @ApiModelProperty(value = "登录超时时间(ms)", required = true)
    private Long exptime;

    @ApiModelProperty(value = "是否首次登陆,首次登陆需要重置密码 true 是 false 否", required = true)
    private boolean firstLogin;


}
