package com.imgSys.dto.upload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @description: 部位示意图上传相关信息
 * @author: Libaoyun
 * @date: 2022-10-24 15:46
 **/
@ApiModel
@Data
public class BodyPartImgDto implements Serializable {

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    /**
     * 上传部位号
     */
    @ApiModelProperty(value = "部位序列号")
    private String partSerialNum;

}
