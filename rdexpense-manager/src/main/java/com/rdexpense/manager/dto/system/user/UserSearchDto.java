package com.rdexpense.manager.dto.system.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "员工列表查询入参")
public class UserSearchDto implements Serializable {

    @ApiModelProperty(value = "编号")
    private String userCode;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "英文名")
    private String englishUserName;

    @ApiModelProperty(value = "当前页码,值需大于等于1",required=true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数量,值需大于等于1",required=true)
    private Integer pageSize;
}
