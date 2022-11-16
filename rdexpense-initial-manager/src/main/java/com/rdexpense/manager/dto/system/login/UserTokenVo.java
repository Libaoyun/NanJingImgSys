package com.rdexpense.manager.dto.system.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "用户token")
public class UserTokenVo {

    @ApiModelProperty(value = "是否首次登陆,首次登陆需要重置密码 true 是 false 否", required = true)
    private boolean firstLogin;

    @ApiModelProperty(value = "token", required = true)
    private String token;

}
