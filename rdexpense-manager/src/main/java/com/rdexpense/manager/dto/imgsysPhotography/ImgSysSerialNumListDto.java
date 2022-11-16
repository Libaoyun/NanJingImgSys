package com.rdexpense.manager.dto.imgsysPhotography;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "记录序列号列表")
public class ImgSysSerialNumListDto extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "菜单编码", required = true)
    private String menuCode;

    @ApiModelProperty(value = "记录序列码", required = true)
    private List<String> serialNumList;
}
