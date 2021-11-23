package com.rdexpense.manager.dto.system.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/4/1 9:25
 */
@Data
@ApiModel(value = "更新密码数据对象")
public class UpdateUserPasswordDto   {


   @ApiModelProperty(value = "密码",required = true)
   private String password;

   @ApiModelProperty(value = "旧密码",required = true)
   private String oldPassword;
}
