package com.imgSys.dto.img;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "患者影像查询条件")
public class ImgPhotoSearchDto extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    @ApiModelProperty(value = "患者就诊卡号")
    private String patientIdCardNo;

    @ApiModelProperty(value = "起始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @ApiModelProperty(value = "截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "拍摄部位")
    private String bodyPartSerialNum;

    @ApiModelProperty(value = "关键字")
    private String keyWord;

    @ApiModelProperty(value = "采集科室")
    private String departmentCode;

    public ImgPhotoSearchDto() {
    }
}

