package com.rdexpense.manager.dto.equipmentDepreciationExpenses;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "设备折旧支出模板上传")
public class DepreciationExpensesUploadFileDto implements Serializable {

    @ApiModelProperty(value = "研发项目申请表业务主键businessId")
    private String projectApplyMainId;

    @ApiModelProperty(value = "项目名称(长度：256)")
    private String projectName;

    @ApiModelProperty(value = "所属月份")
    private String belongingMonth;

    @ApiModelProperty(value = "起始年度")
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String startYear;

    @ApiModelProperty(value = "结束年度")
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String endYear;

    @ApiModelProperty(value = "文件(上传模板时使用)")
    private MultipartFile file;
}
