package com.imgSys.dto.img;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class ImgCapturingDto implements Serializable {
    @ApiModelProperty(value = "影像采集设备编码")
    private String ipcDeviceCode;

    @ApiModelProperty(value = "查询影像缩略图标记")
    private String thumbnailImgFlag;

    @ApiModelProperty(value = "该设备患者图像采集开始时间")
    private String graphingStartTime;
}
