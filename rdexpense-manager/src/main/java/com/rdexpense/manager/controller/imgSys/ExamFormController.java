package com.rdexpense.manager.controller.imgSys;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.imgSys.BodyPart;
import com.rdexpense.manager.dto.imgSys.ExamForm;
import com.rdexpense.manager.dto.imgSys.Patient;
import com.rdexpense.manager.service.imgSys.ExamFormService;
import com.rdexpense.manager.service.imgSys.PatientService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.common.util.ConstantMsgUtil.ERR_QUERY_FAIL;
import static com.common.util.ConstantMsgUtil.ERR_SAVE_FAIL;

/**
 * @description: 影响采集请求单
 * @author: Libaoyun
 * @date: 2022-10-31 18:56
 **/

@RestController
@RequestMapping("/form")
@Api(value = "患者采集单信息表", tags = "患者采集单信息表")
public class ExamFormController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BodyPart.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private ExamFormService examFormService;

    @ApiOperation("新增采集请求单信息")
    @PostMapping("/requisition")
    public ResponseEntity addExamForm(ExamForm examForm){
        PageData pd = this.getParamsFormat("XML");
        CheckParameter.stringLengthAndEmpty(pd.getString("patientIdCardNo"), "患者就诊卡号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("formIssuerCode"), "检查单开具人工号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "检查单开具科室编号",128);
        ResponseEntity result = null;
        try {
            examFormService.addExamForm(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增采集请求单失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "采集请求单", pd);
        }
    }

//    @ApiOperation("根据患者就诊卡或身份证查询患者/请求单信息")
//    @GetMapping("/patient")
//    public ResponseEntity getPatientByParams(Patient patient){
//        PageData pd = this.getPageData();
//        try {
//            PageData pageData = examFormService.getPatientByParams(pd);
//             return ResponseEntity.success(pageData, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
//        }
//            catch (MyException e) {
//            logger.error("查询患者信息,request=[{}]", pd);
//            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
//        }
//    }
}
