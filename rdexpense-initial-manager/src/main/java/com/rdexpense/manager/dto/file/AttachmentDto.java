package com.rdexpense.manager.dto.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "附件信息")
@Data
public class AttachmentDto implements Serializable {
    /**
     * 自增主键
     */
    @ApiModelProperty(value = "自增主键")
    private Long id;

    /**
     * 业务主键ID
     */
    @ApiModelProperty(value = "业务主键ID")
    private String businessId;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 文件后缀
     */
    @ApiModelProperty(value = "文件后缀")
    private String fileExt;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    private String fileSize;

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

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件类型  0：图片类型 1：附件
     */
    @ApiModelProperty(value = "文件类型  0：图片类型 1：附件")
    private String type;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createdAt;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date updatedAt;

    /**
     *  url 上传附件需返回出去
     */
    @ApiModelProperty(value = "url")
    private String url;

    private static final long serialVersionUID = 1L;
}

