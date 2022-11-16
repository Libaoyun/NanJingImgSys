package com.rdexpense.manager.dto.analysis;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@ApiModel
public class ExpensesBillUploadFileDto extends  OrganizationDto implements Serializable {

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    @ApiModelProperty(value = "上传人id")
    private String createUserId;

    @ApiModelProperty(value = "上传人姓名")
    private String createUser;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;


}
