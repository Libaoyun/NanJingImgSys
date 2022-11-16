package com.rdexpense.manager.dto.materialRequisition;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:51
 */
@Data
@ApiModel(value = "领料单明细")
public class MaterialRequisitionUploadDetailDto implements Serializable {

    @ApiModelProperty(value = "领料单项目")
    private String billProject;

    @ApiModelProperty(value = "领料单月份")
    private String billMonth;

    @ApiModelProperty(value = "领料单明细")
    private List<MaterialRequisitionDetailedDto> detailList;

}
