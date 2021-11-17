package com.rdexpense.manager.dto.system.handbook;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luxiangbao
 * @date 2020/6/3 13:33
 * @describe 用户手册列表
 */
@ApiModel
@Data
public class TemplateListDto implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    @ApiModelProperty(value = "作者ID")
    private String  creatorUserId;

    @ApiModelProperty(value = "作者")
    private String  creatorUserName;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "描述信息")
    private String remark;

    @ApiModelProperty(value = "文件url地址")
    private String fileUrl;

    @ApiModelProperty(value = "上传的文件名称")
    private String fileOriginalName;


    @ApiModelProperty(value = "编制日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;




}
