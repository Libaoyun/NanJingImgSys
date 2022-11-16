package com.rdexpense.manager.dto.system.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "页面跳转")
public class PageJumpVo implements Serializable {

    @ApiModelProperty(value = "跳转url", required = true)
    private String url;

    public static PageJumpVo setUrl(String url){
        return new PageJumpVo(url);
    }

}
