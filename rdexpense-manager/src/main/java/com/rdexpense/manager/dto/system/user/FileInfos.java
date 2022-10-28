package com.rdexpense.manager.dto.system.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class FileInfos implements Serializable {


    /**
     * 文件id
     */
    @ApiModelProperty(value = "文件id")
    private String id;


    /**
     * url
     */
    @ApiModelProperty(value = "url")
    private String url;

    /**
     * 文件id
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
    private Integer fileSize;

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
    private Integer type;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createdAt;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updatedAt;

}

