package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.rdexpense.manager.dto.system.department.DepartmentTreeDTO;
import com.rdexpense.manager.dto.system.menu.MenuTreeDto;
import com.rdexpense.manager.dto.system.permission.*;
import com.rdexpense.manager.dto.system.user.UserListDTO;
import com.rdexpense.manager.service.system.PermissionService;
import com.rdexpense.manager.controller.base.BaseController;
import com.common.entity.ResponseEntity;
import com.github.pagehelper.PageInfo;


import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "权限配置", tags = {"权限配置"})
@RestController
@RequestMapping(value = "/permission")
public class PermissionController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LogUtil logUtil;


    @PostMapping(value = "/queryAuthorizedUser")
    @ApiOperation(value = "查询已授权用户", notes = "查询已授权用户")
    public ResponseEntity<PageInfo<AuthorityUserListDTO>> queryAuthorizedUser(QueryAuthUserDto queryAuthUserDto) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("organizationId"), "组织ID", 64);
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = permissionService.queryAuthorizedUser(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, AuthorityUserListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询已授权用户失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }


    }


    @PostMapping(value = "/addAuthorization")
    @ApiOperation(value = "新增授权对象", notes = "新增授权对象")
    public ResponseEntity<String> addAuthorization(PermissionAddDto permissionAddDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("organizationId"), "组织ID", 64);
        CheckParameter.stringLengthAndEmpty(pd.getString("userFlag"), "用户类型", 64);
        //校验取出参数
        String userList = pd.getString("userList");
        if (StringUtils.isBlank(userList)) {
            throw new MyException("授权对象不能为空");
        }


        try {
            permissionService.addAuthorization(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增授权用户,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "授权用户", pd);
        }
    }


    @PostMapping(value = "/queryAuthMenuTree")
    @ApiOperation(value = "查询授权菜单")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "organizationId", value = "组织编码", required = true, dataType = "String")})
    public ResponseEntity<List<MenuTreeDto>> queryAuthMenuTree() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("organizationId"), "组织ID", 64);
        CheckParameter.stringLengthAndEmpty(pd.getString("userId"), "用户ID", 64);
        try {
            List<PageData> list = permissionService.queryAuthMenuTree(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, MenuTreeDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("查询授权菜单,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());

        }

    }


    @PostMapping(value = "/updateAuthMenu")
    @ApiOperation(value = "编辑授权菜单", notes = "编辑授权菜单")
    public ResponseEntity<String> updateAuthorizeUser(AlterUserAuthMenuDto alterUserAuthMenuDto) {

        PageData pd = this.getParams();

        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("organizationId"), "组织ID", 64);
        CheckParameter.stringLengthAndEmpty(pd.getString("userId"), "授权对象ID", 64);

        ResponseEntity result = null;
        try {
            permissionService.updateAuthMenu(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("编辑授权菜单失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "授权菜单", pd);
        }

    }




    @PostMapping(value = "/deleteAuthorization")
    @ApiOperation(value = "删除授权对象", notes = "删除授权对象")
    public ResponseEntity<String> removeAuthorization(PermissionDeleteDto permissionDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        //校验取出参数
        String idList = pd.getString("userList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("授权对象不能为空");
        }
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("organizationId"), "组织ID", 64);

        try {
            permissionService.removeAuthorization(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除授权用户,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "授权用户", pd);
        }
    }



    @ApiOperation(value = "查询部门职务树", notes = "查询部门职务树")
    @GetMapping(value = "/queryDeptTree")
    public ResponseEntity<List<DepartmentTreeDTO>> queryDeptTree() {
        PageData pd = this.getParams();
        try {
            List<PageData> list = permissionService.queryDeptTree(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, DepartmentTreeDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询部门职务树失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }

    }

    @ApiOperation(value = "查询员工", notes = "查询员工")
    @PostMapping(value = "/queryUser")
    public ResponseEntity<PageInfo<UserListDTO>> queryUser(QueryUserDto queryUserDto) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("organizationId"), "组织ID", 64);
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = permissionService.queryUser(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, UserListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询员工,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "查询授权组织", notes = "查询授权组织")
    @GetMapping(value = "/queryOrganization")
    public ResponseEntity<List<PermissionOrganizationListDTO>> queryOrganization() {
        PageData pd = this.getParams();
        try {
            List<PageData> list = permissionService.queryOrganization(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, PermissionOrganizationListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询授权组织失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }

    }





}
