package com.rdexpense.manager.dto.imgSys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 检查单中勾选的部位
 * @author: Libaoyun
 * @date: 2022-11-03 19:24
 **/
@Data
@ApiModel("患者信息")
public class PartOfForm {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("序列号")
    private String serialNum;

    @ApiModelProperty("部位名称")
    private String partName;

    @ApiModelProperty("部位编码")
    private String partCode;

    @ApiModelProperty("部位序列号")
    private String partSerialNum;

    @ApiModelProperty("表单序列号")
    private String formSerialNum;

    @ApiModelProperty("是否需要拍摄细节")
    private String isDetail;

    @ApiModelProperty("具体拍摄要求（一般为其他拍摄部位，即非标准部位）")
    private String requireDetail;

    @ApiModelProperty("采集人姓名")
    private String collector;

}
