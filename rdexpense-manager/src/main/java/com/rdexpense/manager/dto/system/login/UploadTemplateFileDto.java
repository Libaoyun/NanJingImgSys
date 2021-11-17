package com.rdexpense.manager.dto.system.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@ApiModel
public class UploadTemplateFileDto implements Serializable {

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    @ApiModelProperty(value = "上传人id")
    private String createUserId;

    @ApiModelProperty(value = "上传人姓名")
    private String createUser;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "组织编码")
    private String orgCode;

    @ApiModelProperty(value = "组织名称")
    private String orgName;


}
