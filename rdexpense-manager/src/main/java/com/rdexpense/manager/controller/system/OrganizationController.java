package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.system.organization.*;
import com.rdexpense.manager.dto.system.user.IdListDto;
import com.rdexpense.manager.service.system.OrganizationService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:44
 * @description 组织管理
 */
@RestController
@RequestMapping("/organization")
@Api(value = "组织管理", tags = "组织管理")
public class OrganizationController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private OrganizationService organizationService;


    @PostMapping("/queryOrganizationList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<OrganizationListDTO>> queryUserList(OrganizationQueryDTO organizationQueryDTO) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = organizationService.queryOrganizationList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, OrganizationListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询组织列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "新增组织")
    @PostMapping(value = "/addOrganization", consumes = "application/json")
    public ResponseEntity addOrganization(OrganizationAddDTO projectAddDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "项目名称",128);

        ResponseEntity result = null;
        try {
            organizationService.addOrganization(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增项目失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目", pd);
        }
    }


    @ApiOperation(value = "编辑组织")
    @PostMapping(value = "/updateOrganization", consumes = "application/json")
    public ResponseEntity updateOrganization(OrganizationUpdateDTO projectUpdateDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "项目名称",128);

        ResponseEntity result = null;
        try {
            organizationService.updateOrganization(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑项目失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "项目", pd);
        }
    }



    @ApiOperation(value = "删除组织")
    @PostMapping(value = "/deleteOrganization", consumes = "application/json")
    public ResponseEntity deleteSecondOrgNode(IdListDto idListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        ResponseEntity result = null;
        try {
            organizationService.deleteOrganization(pd);
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



    @PostMapping("/getOrganizationDetail")
    @ApiOperation(value = "查询组织详情", notes = "查询组织详情")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String")
    public ResponseEntity<OrganizationDetailDTO> getOrganizationDetail() {
        PageData pd = this.getParams();
        try {
            PageData pageData = organizationService.getOrganizationDetail(pd);
            return PropertyUtil.pushData(pageData, OrganizationDetailDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询组织详情,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


}
