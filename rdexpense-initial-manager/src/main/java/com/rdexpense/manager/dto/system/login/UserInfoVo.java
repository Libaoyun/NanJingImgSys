package com.rdexpense.manager.dto.system.login;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel(description = "用户对象USER")
public class UserInfoVo implements Serializable {

    /* {"id":142718,"name":"汪婧","gender":2,"catagory":101301,"positionStatus":101401,"order":1}*/
    @ApiModelProperty(value = "用户唯一标识", required = true)
    private Long id;

    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    @ApiModelProperty(value = "性别 -- 1 男性 2 女性", required = true)
    private Integer gender;

    @ApiModelProperty(value = "人员类别")
    private Integer category;

    @ApiModelProperty(value = "人员在岗位内排序号")
    private Integer order;

    @ApiModelProperty(value = "权限工程树")
    private JSONArray scopeTree;

    @ApiModelProperty(value = "用户类型 0内部员工（一体化） ,1-外部员工", required = true)
    private Integer userFlag;

    @ApiModelProperty(value = "token", required = true)
    private String token;

    @ApiModelProperty(value = "登录超时时间(ms)", required = true)
    private Long exptime;

    @ApiModelProperty(value = "用户名，内部用户无")
    private String userName;

    @ApiModelProperty(value = "是否首次登陆,首次登陆需要重置密码 true 是 false 否", required = true)
    private boolean firstLogin;

    @ApiModelProperty(value = "一体化内部用户岗位信息 外部用户没有该信息")
    private List<Map<String, Object>> position;

    @ApiModelProperty(value = "默认选择")
    private String currentProjectId;

    @ApiModelProperty(value = "用户登录来源  app:移动端 pc:PC端")
    private String loginSource;

    @ApiModelProperty(value = "是否在其他客户端登陆 true:是 false:否")
    private boolean otherLogin;


}
