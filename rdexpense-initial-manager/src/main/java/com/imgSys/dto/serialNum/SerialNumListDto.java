package com.imgSys.dto.serialNum;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 一组序列号，用于删除时使用
 * @author: Libaoyun
 * @date: 2022-10-31 16:21
 **/

@Data
public class SerialNumListDto implements Serializable {

    @ApiModelProperty(value = "主键id",required = true)
    private List<String> idList;
}
