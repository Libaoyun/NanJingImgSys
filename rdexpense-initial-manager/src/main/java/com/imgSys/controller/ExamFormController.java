package com.imgSys.controller;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.imgSys.dto.BodyPart;
import com.imgSys.dto.ExamForm;
import com.imgSys.dto.PartOfForm;
import com.imgSys.dto.Patient;
import com.imgSys.dto.form.PartOfFormRequest;
import com.imgSys.service.ExamFormService;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.rdexpense.manager.controller.base.BaseController;

import com.rdexpense.manager.service.system.UserService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

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

    @Autowired
    private UserService userService;

    @ApiOperation("新增采集请求单信息")
    @PostMapping("/requisition")
    public ResponseEntity addExamForm(ExamForm examForm){
        PageData pd = this.getParamsFormat("JSON");
        CheckParameter.stringLengthAndEmpty(pd.getString("patientIdCardNo"), "患者就诊卡号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("formSerialNum"), "检查单序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("formIssuerName"), "诊断医师姓名",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "就诊科室编号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("imgDepartmentCode"), "检查科室编号",128);
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

    @ApiOperation("根据患者就诊卡或身份证查询患者（请求单界面）")
    @GetMapping("/patient")
    public ResponseEntity getPatientByParams(Patient patient){
        PageData pd = this.getParams();
        try {
            PageData pageData = examFormService.getPatientByParams(pd);
             return ResponseEntity.success(pageData, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        }
            catch (MyException e) {
            logger.error("查询患者信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 新增与修改患者信息（并返回患者信息用于展示）
     */
    @PostMapping("/patient")
    @ApiOperation("新增与修改患者信息（并返回患者信息用于展示）")
    public ResponseEntity addOrModifyPatient(Patient patient){
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("patientIdCardNo"), "就诊卡号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("name"), "姓名",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("gender"), "性别",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("identifierId"), "身份证号/医保卡号",128);
        ResponseEntity result = null;
        try {
            PageData pageData = examFormService.addOrModifyPatient(pd);
            result = ResponseEntity.success(pageData, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        }catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增/修改患者信息失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "患者信息", pd);
        }
    }


    /**
     * 获取标准部位图片信息（依据部位编码排序）,这里可以调用BodyPartController里的接口standard。但是可能少些字段，因此还是再写一次
     */
    @GetMapping("/part/standard")
    @ApiOperation(value = "查询所有标准部位及图片信息（Base64）")
//    @ApiImplicitParam(name = "orgId", value = "部门ID", required = true, dataType = "String")
    public ResponseEntity<List<PageData>> getStandardBodyPart() {
        PageData pd = this.getParams();
        try {
            List<PageData> dataList = examFormService.getStandardBodyPart();
            return ResponseEntity.success(PropertyUtil.covertListModel(dataList, PageData.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有标准部位信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    /**
     * 添加检查单勾选的部位信息
     */
    @ApiOperation("新增检查单部位信息")
    @PostMapping("/body-part")
    public ResponseEntity addExamFormBodyPart(PartOfFormRequest partOfFormRequest){
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("formSerialNum"), "表单序列号",128);
        ResponseEntity result = null;
        try {
            examFormService.addPartOfForm(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增检查单部位失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "检查单部位", pd);
        }
    }

    /**
     * 添加检查单额外部位
     */
    @ApiOperation("添加检查单额外部位信息")
    @PostMapping("/extra-part")
    public ResponseEntity addExtraBodyPart(PartOfForm partOfForm){
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("formSerialNum"), "表单序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("partName"), "部位名称",128);
        ResponseEntity result = null;
        try {
            examFormService.addExtraBodyPart(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增检查单额外部位失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "检查单额外部位", pd);
        }
    }

    /**
     * 删除检查单额外部位
     */
    @ApiOperation("删除检查单额外部位信息")
    @DeleteMapping("/extra-part")
    public ResponseEntity deleteExtraBodyPart(PartOfForm partOfForm){
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("formSerialNum"), "表单序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("partName"), "部位名称",128);
        ResponseEntity result = null;
        try {
            examFormService.deleteExtraBodyPart(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("删除检查单额外部位失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "检查单额外部位", pd);
        }
    }

    /**
     * 获取患者信息（采集登记页面）
     */
    @ApiOperation("获取患者信息（采集登记页面）")
    @GetMapping("/register")
    public ResponseEntity getPatientOfForm(Patient patient){
        PageData pd = this.getParams();
        ResponseEntity result = null;
        try {
            PageData pageData = examFormService.getPatientOfForm(pd);
            result = ResponseEntity.success(pageData, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("登记检查单失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 4, "登记检查单", pd);
        }
    }

    /**
     * 登记采集请求单（采集时）
     */
    @ApiOperation("登记采集请求单（采集时）")
    @PostMapping("/register")
    public ResponseEntity registerExamForm(ExamForm examForm){
        PageData pd = this.getParamsFormat("JSON");
        CheckParameter.stringLengthAndEmpty(pd.getString("patientIdCardNo"), "患者就诊卡号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("formSerialNum"), "检查单序列号",128);
        ResponseEntity result = null;
        try {
            examFormService.registerExamForm(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("登记采集请求单失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "登记请求单", pd);
        }
    }

    @PostMapping("/queryParts2Graphing")
    @ApiOperation(value = "查询患者待采集部位", notes = "查询患者待采集部位")
    public ResponseEntity queryParts2Graphing() {
        PageData pd = this.getParams();

        String department_code = null;
        PageData userPd = userService.getUserDetail(pd);


        List<PageData> departmentList = (List<PageData>) userPd.get("departmentList");
        if (CollectionUtils.isNotEmpty(departmentList)) {
            department_code = departmentList.get(0).getString("department_code");
        }

        PageData queryPd = new PageData();
        if (StringUtils.isNotBlank(department_code)) {
            queryPd.put("", "");
        }


        return null;
    }







    /* FIXME 采集登记列表暂不使用，因此搁置先不管
    *//**
     * 分页查询患者影像采集登记列表
     *//*
    @GetMapping("/register")
    @ApiOperation(value = "查询患者影像采集登记列表")
    public ResponseEntity<PageInfo<RegisterList>> getListOfRegister(PageParam pageParam) {
        PageData pd = this.getParams();
        try {
            pd.put("pageNum", pd.getString("pageNum") == "" ? "1" : pd.getString("pageNum"));
            pd.put("pageSize", pd.getString("pageSize") == "" ? "10" : pd.getString("pageSize"));
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> dataList = examFormService.getListOfRegister();
            PageInfo<PageData> pageInfo = new PageInfo<>(dataList);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, RegisterList.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有患者影像采集登记列表,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    *//**
     * 编辑患者登记列表（可更改采集人员、采集状态、设备分配情况）
     *//*
    @ApiOperation(value = "编辑患者登记列表信息")
    @PostMapping(value = "/update")
    public ResponseEntity updateFormList(RegisterList registerList) {
        PageData pd = this.getParamsFormat("XML");
        CheckParameter.stringLengthAndEmpty(pd.getString("formSerialNum"), "图像采集单序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("serialNum"), "登记表中某部位的id",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("partName"), "采集部位名称",128);

        ResponseEntity result = null;
        try {
            examFormService.updateFormList(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑患者登记信息失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "患者登记表", pd);
        }
    }
*/

   /* *//**
     * 获取登记表中部位信息（包含是否被选中以及是否需要细节,用isSelect和isDetail表示）
     *//*
    @GetMapping("/part/standard")
    @ApiOperation(value = "查询所有标准部位及图片信息（Base64）")
//    @ApiImplicitParam(name = "orgId", value = "部门ID", required = true, dataType = "String")
    public ResponseEntity<List<PartOfForm>> getRegisterFormBodyPart() {
        PageData pd = this.getParams();
        try {
            List<PageData> dataList = examFormService.getStandardBodyPart();
            return ResponseEntity.success(PropertyUtil.covertListModel(dataList, PartOfForm.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有标准部位信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }*/

}
