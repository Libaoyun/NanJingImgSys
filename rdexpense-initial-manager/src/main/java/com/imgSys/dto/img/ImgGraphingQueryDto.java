package com.imgSys.dto.img;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "患者影像采集查询")
public class ImgGraphingQueryDto {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "查询设备编码")
    private String ipcDeviceCode;

    @ApiModelProperty(value = "采集登记时间")
    private String graphingRegisterTime;

    @ApiModelProperty(value = "已采集影像列表")
    private List<ImgFileInfoDto> imgFileDtoList;
}
