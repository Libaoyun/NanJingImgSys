package com.rdexpense.manager.dto.system.organization;

import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 组织
 */
@ApiModel
@Data
public class OrganizationListDTO extends BaseEntity implements Serializable {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("组织ID")
    private String orgId;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("备注")
    private String remark;


}
