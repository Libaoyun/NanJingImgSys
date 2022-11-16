package com.rdexpense.manager.dto.attendance;

import com.rdexpense.manager.dto.base.OrganizationDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class AttendanceUploadFileDto extends  OrganizationDto implements Serializable {

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    @ApiModelProperty(value = "上传人id")
    private String createUserId;

    @ApiModelProperty(value = "上传人姓名")
    private String createUser;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "身份证编码集合")
    private List<String> idCardList;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "考勤月份")
    private String attendanceMonth;



}
