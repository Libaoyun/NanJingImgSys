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
import com.rdexpense.manager.dto.system.handbook.*;
import com.rdexpense.manager.service.system.TemplateService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.common.util.ConstantMsgUtil.INFO_QUERY_SUCCESS;

/**
 * @author luxiangbao
 * @date 2021/6/29 11:53
 * @describe
 */
@Api(value = "知识库管理", tags = "知识库管理")
@RestController
@RequestMapping(value = "/template")
public class TemplateController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private TemplateService templateService;

    @ApiOperation(value = "列表查询")
    @PostMapping(value = "/searchRecord", consumes = "application/json")
    public ResponseEntity<PageInfo<TemplateListDto>> searchRecord(TemplateQueryDto templateQueryDto) {
        PageData pd = this.getParams();
        String strMenuId = String.valueOf(pd.getInt("menuCode"));
        CheckParameter.checkPositiveInt(strMenuId, "菜单编码");
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = templateService.searchRecord(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, TemplateListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询知识库管理,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "新增")
    @PostMapping(value = "/addRecord", consumes = "application/json")
    public ResponseEntity addRecord(TemplateSaveDto templateSaveDto) {
        PageData pd = this.getParams();
        checkSave(pd);
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            templateService.addRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增知识库管理,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "知识库管理", pd);
        }
    }


    @ApiOperation(value = "编辑")
    @PostMapping(value = "/updateRecord", consumes = "application/json")
    public ResponseEntity updateRecord(TemplateSaveDto templateSaveDto) {
        PageData pd = this.getParams();
        checkSave(pd);
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID",128);
        ResponseEntity result = null;
        try {
            templateService.updateRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("编辑知识库管理,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "知识库管理", pd);
        }
    }


    @ApiOperation(value = "删除")
    @PostMapping(value = "/deleteRecord", consumes = "application/json")
    public ResponseEntity deleteRecord(TemplateDeleteDto templateDeleteDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            //校验取出参数
            String businessIdList = pd.getString("businessIdList");
            if (StringUtils.isBlank(businessIdList)) {
                throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
            }

            templateService.deleteRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除知识库管理,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "知识库管理", pd);
        }
    }


    @ApiOperation(value = "查看详情")
    @PostMapping(value = "/recordDetail")
    @ApiImplicitParam(name = "businessId", value = "业务主键id", required = true, dataType = "string")
    public ResponseEntity<TemplateDetailDto> recordDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID",128);
        try {
            PageData pageData = templateService.recordDetail(pd);
            return PropertyUtil.pushData(pageData, TemplateDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询知识库管理详情,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), ConstantMsgUtil.ERR_QUERY_FAIL.desc());

        }
    }


    private void checkSave(PageData pd) {
        String status = pd.getString("status");
        CheckParameter.stringLengthAndEmpty(status, "是否启用", 8);

        String file = pd.getString("fileList");
        if (StringUtils.isBlank(file)) {
            throw new MyException("上传文件不能为空");

        }

    }


}
