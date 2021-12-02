package com.rdexpense.manager.dto.system.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rdexpense.manager.dto.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "员工管理列表")
public class UserListDTO extends  BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    private String userCode;

    @ApiModelProperty(value = "姓名")
    private String userName;

//    @ApiModelProperty(value = "英文名")
//    private String englishUserName;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "性别编码")
    private String genderCode;

    @ApiModelProperty(value = "部门")
    private String departmentName;

    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "职务")
    private String postName;

    @ApiModelProperty(value = "职务编码")
    private String postCode;

    @ApiModelProperty(value = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;

    @ApiModelProperty(value = "身高")
    private String height;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "学历编码")
    private String educationCode;

    @ApiModelProperty(value = "婚姻状态 0：未婚 1：已婚 ")
    private String maritalStatus;

    @ApiModelProperty(value = "血型")
    private String bloodType;

    @ApiModelProperty(value = "血型编码")
    private String bloodTypeCode;

    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;

    @ApiModelProperty(value = "办公电话")
    private String officeTelephone;

    @ApiModelProperty(value = "电子邮件")
    private String email;

    @ApiModelProperty(value = "其他")
    private String fax;

    @ApiModelProperty(value = "员工状态")
    private String employeeStatus;

    @ApiModelProperty(value = "员工状态编码")
    private String employeeStatusCode;

    @ApiModelProperty(value = "员工类型")
    private String employeeType;

    @ApiModelProperty(value = "员工类型编码")
    private String employeeTypeCode;

    @ApiModelProperty(value = "参工日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date participationDate;

    @ApiModelProperty(value = "入职日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date entryDate;

    @ApiModelProperty(value = "转正日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date confirmationDate;

    @ApiModelProperty(value = "离职日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveDate;

    @ApiModelProperty(value = "国籍")
    private String nationality;

    @ApiModelProperty(value = "籍贯")
    private String nativePlace;

    @ApiModelProperty(value = "民族")
    private String nation;

    @ApiModelProperty(value = "宗教信仰")
    private String religion;

    @ApiModelProperty(value = "部门列表")
    private List<UserDepartmentDto> departmentList;

}






