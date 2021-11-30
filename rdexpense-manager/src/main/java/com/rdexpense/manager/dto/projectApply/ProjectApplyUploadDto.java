package com.rdexpense.manager.dto.projectApply;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@ApiModel
public class ProjectApplyUploadDto extends  OrganizationDto implements Serializable {

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    @ApiModelProperty(value = "上传人id")
    private String createUserId;

    @ApiModelProperty(value = "上传人姓名")
    private String createUser;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "模板内容 1:上传全部 2：主表信息 3：立项调研信息 4：进度计划 5：参加单位 6：研究人员 7：经费预算 8：经费预算（每月预算） 9：拨款计划")
    private String fileType;


}
