package com.rdexpense.manager.dto.imgsysPhotography;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

public class ImgSysPhotoUploadDto extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "采集设备编码", required = true)
    private List<String> deviceCode;

    @ApiModelProperty(value = "影像文件名", required = true)
    private List<String> imgFileName;

    @ApiModelProperty(value = "归属科室", required = true)
    private List<String> departmentCode;

    @ApiModelProperty(value = "文件")
    private MultipartFile file;
}
