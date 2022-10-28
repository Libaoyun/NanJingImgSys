package com.rdexpense.manager.controller.imgSys;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.imgSys.BodyPart;
import com.rdexpense.manager.dto.imgSys.DiagnosticItem;
import com.rdexpense.manager.service.imgSys.DiagnosticItemService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.common.util.ConstantMsgUtil.*;
import static com.common.util.ConstantMsgUtil.ERR_QUERY_FAIL;

/**
 * @description: 科室诊疗项目表
 * @author: Libaoyun
 * @date: 2022-10-19 17:09
 **/

@RestController
@RequestMapping("/dig-item")
@Api(value = "科室诊疗项目管理", tags = "科室诊疗项目管理")
public class DiagnosticItemController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BodyPart.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private DiagnosticItemService diagnosticItemService;

    @ApiOperation("新增科室诊疗项目信息")
    @PostMapping()
    public ResponseEntity addDiagnosticItem(DiagnosticItem diagnosticItem){
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("parentSerialNum"), "父项目序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("itemCode"), "项目名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("itemCode"), "项目编码",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "所属科室编码",128);

        ResponseEntity result = null;
        try {
            diagnosticItemService.addDiagnosticItem(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增科室诊疗项目信息失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目", pd);
        }
    }

    @ApiOperation(value = "删除某科室诊疗项目信息")
    @DeleteMapping()
    public ResponseEntity deleteDiagnosticItem(DiagnosticItem diagnosticItem) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("itemSerialNum"), "项目序列号",128);
        ResponseEntity result = null;
        try {
            diagnosticItemService.deleteDiagnosticItem(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除科室诊疗项目失败,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "项目", pd);
        }
    }

    @ApiOperation(value = "编辑科室诊疗项目信息")
    @PutMapping()
    public ResponseEntity updateDiagnosticItem(DiagnosticItem diagnosticItem) {
        PageData pd = this.getParamsFormat("XML");
        CheckParameter.stringLengthAndEmpty(pd.getString("itemSerialNum"), "项目序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("parentSerialNum"), "父项目序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("itemCode"), "项目名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("itemCode"), "项目编码",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "所属科室编码",128);

        ResponseEntity result = null;
        try {
            diagnosticItemService.updateDiagnosticItem(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑科室诊疗项目失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "项目", pd);
        }
    }

    @GetMapping()
    @ApiOperation(value = "树形查询所有科室诊疗项目信息")
//    @ApiImplicitParam(name = "orgId", value = "部门ID", required = true, dataType = "String")
    public ResponseEntity<List<DiagnosticItem>> getAllDiagnosticItem() {
        PageData pd = this.getParams();
        try {
            List<PageData> dataList = diagnosticItemService.getAllDiagnosticItem("0");
            return ResponseEntity.success(PropertyUtil.covertListModel(dataList, DiagnosticItem.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有科室诊疗项目信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }
}
