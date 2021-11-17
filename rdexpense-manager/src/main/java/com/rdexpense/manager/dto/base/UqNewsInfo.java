package com.rdexpense.manager.dto.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel
public class UqNewsInfo implements Serializable {

    @ApiModelProperty(value = "自增主键")
    private Long id;

    @ApiModelProperty(value = "工程编码")
    private String creatorOrgId;

    @ApiModelProperty(value = "工程名称")
    private String creatorOrgName;

    @ApiModelProperty(value = "消息分类；可取字典表数据")
    private String newsType;

    @ApiModelProperty(value = "消息内容")
    private String newsContent;

    @ApiModelProperty(value = "消息状态")
    private String processName;

    @ApiModelProperty(value = "数据生成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


}
