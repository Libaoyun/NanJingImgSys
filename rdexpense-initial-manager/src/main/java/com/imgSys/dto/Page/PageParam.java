package com.imgSys.dto.Page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 分页查询的参数
 * @author: Libaoyun
 * @date: 2022-10-31 10:08
 **/

@Data
public class PageParam implements Serializable {

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页大小")
    private Integer pageSize;

    @ApiModelProperty("关键词")
    private String keyword;
}
