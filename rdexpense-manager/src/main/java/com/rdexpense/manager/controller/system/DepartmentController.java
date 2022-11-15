package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.system.department.*;
import com.rdexpense.manager.dto.system.organization.OrganizationDetailDTO;
import com.rdexpense.manager.service.system.DepartmentService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:44
 * @description 科室管理
 */
@RestController
@RequestMapping("/department")
@Api(value = "科室管理", tags = "科室管理")
public class DepartmentController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private DepartmentService departmentService;


    @ApiOperation(value = "显示组织树")
    @PostMapping(value = "/queryTree", consumes = "application/json")
    public ResponseEntity<List<DepartmentTreeDTO>> queryTree() {
        PageData pd = this.getParams();
        try {
            List<PageData> dataList = departmentService.queryOrgTree(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(dataList, DepartmentTreeDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("查询组织配置,request=[{}]", pd);
            throw new MyException(ERR_QUERY_FAIL.desc(), e);
        }
    }


    @ApiOperation(value = "拖拽树")
    @PostMapping(value = "/updateOTree", consumes = "application/json")
    public ResponseEntity<List<DepartmentTreeDTO>> updateOrgTree(DepartmentUpdateTreeDTO departmentUpdateTreeDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            List<PageData> dataList = departmentService.updateOrgTree(pd);
            result = ResponseEntity.success(dataList, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "组织树", pd);
        }
    }


    @ApiOperation(value = "新增部门")
    @PostMapping(value = "/addDepartment", consumes = "application/json")
    public ResponseEntity addDepartment(DepartmentAddDTO departmentAddDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("parentId"), "父节点ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "部门名称",64);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "部门编码",64);

        ResponseEntity result = null;
        try {
            pd.put("orgType", "1");
            departmentService.addDepartment(pd);
            result = ResponseEntity.success(null, INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增部门失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "部门", pd);
        }
    }

    @ApiOperation(value = "编辑部门")
    @PostMapping(value = "/updateDepartment", consumes = "application/json")
    public ResponseEntity updateDepartment(DepartmentUpdateDTO departmentUpdateDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgId"), "部门ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "部门名称",64);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "部门编码",64);

        ResponseEntity result = null;
        try {
            departmentService.updateDepartment(pd);
            result = ResponseEntity.success(null, INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑部门失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "部门", pd);
        }
    }


    @PostMapping("/getDepartment")
    @ApiOperation(value = "查询部门详情")
    @ApiImplicitParam(name = "orgId", value = "部门ID", required = true, dataType = "String")
    public ResponseEntity<DepartmentUpdateDTO> getDepartment() {
        PageData pd = this.getParams();
        try {
            PageData pageData = departmentService.getDepartment(pd);
            return PropertyUtil.pushData(pageData, DepartmentUpdateDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询部门详情,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


    @ApiOperation(value = "新增职务")
    @PostMapping(value = "/addPost", consumes = "application/json")
    public ResponseEntity addPost(PostAddDTO postAddDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("parentId"), "父节点ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "职务名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("postCode"), "职务编码",128);

        ResponseEntity result = null;
        try {
            pd.put("orgType", "2");
            departmentService.addPost(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增职务失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "职务", pd);
        }
    }

    @ApiOperation(value = "编辑职务")
    @PostMapping(value = "/updatePost", consumes = "application/json")
    public ResponseEntity updatePost(PostUpdateDTO postUpdateDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgId"), "父节点ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "职务名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("postCode"), "职务编码",128);
        ResponseEntity result = null;
        try {
            departmentService.updatePost(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑职务失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "职务", pd);
        }
    }

    @PostMapping("/getPost")
    @ApiOperation(value = "查询部门详情")
    @ApiImplicitParam(name = "orgId", value = "职务ID", required = true, dataType = "String")
    public ResponseEntity<PostUpdateDTO> getPost() {
        PageData pd = this.getParams();
        try {
            PageData pageData = departmentService.getPost(pd);
            return PropertyUtil.pushData(pageData, PostUpdateDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询职务详情,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


    @ApiOperation(value = "删除树节点")
    @PostMapping(value = "/deleteOrgNode", consumes = "application/json")
    public ResponseEntity deleteSecondOrgNode(DepartmentTreeDeleteDTO departmentTreeDeleteDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgId"), "节点ID",128);

        ResponseEntity result = null;
        try {
            departmentService.deleteOrgNode(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除组织,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "组织", pd);
        }
    }

    /*@ApiOperation(value = "更改科室资源使用权限")
    @PostMapping(value = "/permissions")
    public ResponseEntity updateUsePermissions(DepartmentTreeDeleteDTO departmentTreeDeleteDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgId"), "节点ID",128);

        ResponseEntity result = null;
        try {
            departmentService.deleteOrgNode(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除组织,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "组织", pd);
        }
    }*/





}
