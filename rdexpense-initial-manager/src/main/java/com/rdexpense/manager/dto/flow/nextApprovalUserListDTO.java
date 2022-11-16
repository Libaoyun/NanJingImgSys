package com.rdexpense.manager.dto.flow;

import com.rdexpense.manager.dto.base.BaseEntity;
import com.rdexpense.manager.dto.system.user.UserListDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/8/19 11:07
 * @description 流程
 */
@ApiModel
@Data
public class nextApprovalUserListDTO extends BaseEntity implements Serializable {


    @ApiModelProperty(value = "处理类型，1：需要选择审批人，2：不需要选择审批人，直接提交或审批", required = true)
    private String handleStrategy;

    @ApiModelProperty(value = "用户信息列表", required = true)
    private List<UserListDTO> userList;


}
