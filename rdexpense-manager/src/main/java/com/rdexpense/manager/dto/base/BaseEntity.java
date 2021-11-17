package com.rdexpense.manager.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "创建时间",hidden = true)
    private Date createTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "修改时间",hidden = true)
    private Date updateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "创建人",hidden = true)
    private String createUser;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "修改人",hidden = true)
    private String updateUser;
}
