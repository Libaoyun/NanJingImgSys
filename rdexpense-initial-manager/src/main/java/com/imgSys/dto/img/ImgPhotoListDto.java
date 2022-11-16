package com.imgSys.dto.img;


import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "患者影像查询结果")
public class ImgPhotoListDto extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "影像名称")
    private String imgName;

    @ApiModelProperty(value = "影像序列号")
    private String imgSerialNum;

    @ApiModelProperty(value = "影像缩略图")
    private String thumbnailFile;

    @ApiModelProperty(value = "影像采集日期")
    private Date graphingTime;
}
