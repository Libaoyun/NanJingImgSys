package com.imgSys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 科室人体部位信息
 * @author: Libaoyun
 * @date: 2022-10-17 19:15
 **/


@ApiModel("科室人体部位信息")
@Data
public class BodyPart implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty(value = "父部位序列号")
    private String parentSerialNum;

    @ApiModelProperty(value = "部位名称")
    private String partName;

    @ApiModelProperty(value = "部位序列号（系统内，唯一）")
    private String partSerialNum;

    @ApiModelProperty(value = "部位编码(科室内)")
    private String partCode;

    @ApiModelProperty("部位描述")
    private String partDiscription;

    @ApiModelProperty("部位示意图片地址路径")
    private String partSketchFile;

    @ApiModelProperty(value = "所属科室编号")
    private String departmentCode;

    @ApiModelProperty("是否为标准部位")
    private String standardPartFlag;

    @ApiModelProperty("对应示意图的base64编码")
    private String base64Str;

    @ApiModelProperty(value = "子部位数量")
    private int countChild;

    @ApiModelProperty("子节点")
    private List<BodyPart> children;
}
