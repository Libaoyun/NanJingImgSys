package com.rdexpense.manager.dto.system.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户token")
public class UserTokenDTO {

    @ApiModelProperty(value = "是否首次登陆,首次登陆需要重置密码 true 是 false 否", required = true)
    private boolean firstLogin;

    @ApiModelProperty(value = "token")
    private String token;

}
