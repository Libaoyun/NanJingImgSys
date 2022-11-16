package com.imgSys.dto.img;


import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "患者影像添加")
public class ImgPhotoAddDto extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    /*@ApiModelProperty(value = "影像名称")
    private String imgName;*/

    @ApiModelProperty(value = "患者激光号")
    private String imgLaserId;

    @ApiModelProperty(value = "患者就诊卡号")
    private String patientIdCardNo;

    @ApiModelProperty(value = "影像采集者工号")
    private String grapherCode;

    @ApiModelProperty(value = "采集者科室编码")
    private String departmentCode;

    @ApiModelProperty(value = "采集请求单序列码")
    private String examFormSerialNum;

    @ApiModelProperty(value = "采集设备编码")
    private String graphingDeviceCode;

    @ApiModelProperty(value = "影像图像列表")
    List<ImgFileInfoDto> imgList;

}
