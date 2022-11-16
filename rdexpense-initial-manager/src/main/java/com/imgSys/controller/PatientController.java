package com.imgSys.controller;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imgSys.dto.BodyPart;
import com.imgSys.dto.Page.PageParam;
import com.imgSys.dto.Patient;
import com.imgSys.dto.serialNum.SerialNumListDto;
import com.imgSys.service.PatientService;
import com.rdexpense.manager.controller.base.BaseController;

import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @description: 患者基本信息管理
 * @author: Libaoyun
 * @date: 2022-10-29 10:22
 **/

@RestController
@RequestMapping("/patient")
@Api(value = "患者基本信息管理", tags = "患者基本信息管理")
public class PatientController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BodyPart.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private PatientService patientService;


    @ApiOperation("新增患者信息")
    @PostMapping()
    public ResponseEntity addPatientInfo(Patient patient){
        PageData pd = this.getParamsFormat("XML");

        CheckParameter.stringLengthAndEmpty(pd.getString("patientIdCardNo"), "就诊卡号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("name"), "姓名",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("gender"), "性别",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("phoneNum"), "手机号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("identifierId"), "身份证号/医保卡号",128);

        ResponseEntity result = null;
        try {
            patientService.addPatientInfo(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增患者信息失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "患者", pd);
        }
    }

    @ApiOperation(value = "删除一个/多个患者信息")
    @DeleteMapping()
    public ResponseEntity deletePatient(SerialNumListDto serialNumListDto) {
        PageData pd = this.getParams();
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("患者就诊卡号不能为空!");
        }
        ResponseEntity result = null;
        try {
            patientService.deletePatient(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除患者失败,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "患者", pd);
        }
    }

    @ApiOperation(value = "编辑患者信息")
    @PostMapping(value = "/update")
        public ResponseEntity updatePatient(Patient patient) {
        PageData pd = this.getParamsFormat("XML");

        CheckParameter.stringLengthAndEmpty(pd.getString("patientIdCardNo"), "就诊卡号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("name"), "姓名",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("gender"), "性别",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("phoneNum"), "手机号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("identifierId"), "身份证号/医保卡号",128);

        ResponseEntity result = null;
        try {
            patientService.updatePatient(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑患者信息失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "患者", pd);
        }
    }

    @GetMapping()
    @ApiOperation(value = "分页(关键词)查询所有患者信息")
    public ResponseEntity<PageInfo<Patient>> getAllPatient(Patient patient, PageParam pageParam) {
        PageData pd = this.getParams();
        try {
            pd.put("pageNum", pd.getString("pageNum") == "" ? "1" : pd.getString("pageNum"));
            pd.put("pageSize", pd.getString("pageSize") == "" ? "10" : pd.getString("pageSize"));
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = patientService.getAllPatient(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, Patient.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有设备信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

}
