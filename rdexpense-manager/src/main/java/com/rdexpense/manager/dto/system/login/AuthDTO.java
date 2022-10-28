package com.rdexpense.manager.dto.system.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel
@Data
public class AuthDTO {

    @ApiModelProperty(value="用户名",required = true)
    private String userCode;

    @ApiModelProperty(value="密码",required = true)
    private String password;

}
