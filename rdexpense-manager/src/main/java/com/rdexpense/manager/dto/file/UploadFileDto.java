package com.rdexpense.manager.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@ApiModel
public class UploadFileDto implements Serializable {

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    /**
     * 上传人id
     */
    @ApiModelProperty(value = "上传人id")
    private String uploadUserId;

    /**
     * 上传人姓名
     */
    @ApiModelProperty(value = "上传人姓名")
    private String uploadUserName;
}
