package com.imgSys.dto.img;


import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "患者影像信息App提交")
public class ImgInfoAppSubmitDto extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "采集设备编码")
    private String graphingDeviceCode;

    @ApiModelProperty(value = "原始影像路径")
    private String originalFile;

    @ApiModelProperty(value = "影像缩略图路径")
    private String thumbnailFile;
}
