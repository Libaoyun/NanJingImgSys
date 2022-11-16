package com.imgSys.dto.img;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class ImgFileInfoDto implements Serializable {
    @ApiModelProperty(value = "影像名称")
    private String imgName;

    @ApiModelProperty(value = "影像文件后缀")
    private String fileExt;

    @ApiModelProperty(value = "图像文件大小")
    private Integer fileSize;

    @ApiModelProperty(value = "图像base64编码")
    private String base64Str;

    @ApiModelProperty(value = "图像文件url")
    private String url;

}
