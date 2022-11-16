package com.imgSys.dto.form;

import com.imgSys.dto.PartOfForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 新增部位时请求类
 * @author: Libaoyun
 * @date: 2022-11-04 10:12
 **/
@Data
@ApiModel("新增补位时封装信息")
public class PartOfFormRequest {

    @ApiModelProperty("表单序列号")
    private String formSerialNum;

    @ApiModelProperty("部位信息")
    private List<PartOfForm> bodyParts;
}
