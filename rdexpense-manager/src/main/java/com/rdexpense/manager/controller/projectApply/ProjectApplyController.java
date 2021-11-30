package com.rdexpense.manager.controller.projectApply;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.text.Document;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfWriter;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.base.FlowAbolishDto;
import com.rdexpense.manager.dto.base.FlowApproveDto;
import com.rdexpense.manager.dto.base.UploadTemplateFileDto;
import com.rdexpense.manager.dto.projectApply.*;
import com.rdexpense.manager.dto.system.user.*;
import com.rdexpense.manager.service.projectApply.ProjectApplyService;
import com.rdexpense.manager.util.FileParamsUtil;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.*;
import static com.common.util.ConstantMsgUtil.ERR_EXPORT_FAIL;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:27
 */
@RestController
@RequestMapping("/projectApply")
@Api(value = "项目立项申请", tags = "项目立项申请")
public class ProjectApplyController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectApplyController.class);

    @Autowired
    private ProjectApplyService projectApplyService;

    @Autowired
    private BaseDao dao;

    @Autowired
    private LogUtil logUtil;


    @PostMapping("/queryList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<ProjectApplyListDto>> queryList(ProjectApplySearchDto projectApplySearchDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = projectApplyService.queryList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ProjectApplyListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询项目立项申请列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "立项申请(新建/提交)")
    @PostMapping("/add")
    public ResponseEntity addApply(ProjectApplyAddDto projectApplyAddDto) {
        PageData pd = this.getParams();
        checkParam(pd);
        ResponseEntity result = null;
        try {

            projectApplyService.addApply(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增项目立项申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目立项申请", pd);
        }
    }

    @ApiOperation(value = "立项申请(编辑/提交)")
    @PostMapping("/update")
    public ResponseEntity updateApply(ProjectApplyAddDto projectApplyAddDto) {

        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 256);
        checkParam(pd);

        ResponseEntity result = null;
        try {

            projectApplyService.updateApply(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("修改项目立项申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "项目立项申请", pd);
        }
    }



    @ApiOperation(value = "删除申请")
    @PostMapping("/delete")
    public ResponseEntity deleteApply(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String idList = pd.getString("businessIdList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("业务主键ID不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            projectApplyService.deleteApply(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除项目立项申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "项目立项申请", pd);
        }


    }


    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询申请详情", notes = "查询申请详情")
    @ApiImplicitParam(name = "businessId", value = "业务主键ID", required = true, dataType = "String")
    public ResponseEntity<ProjectApplyDetailDto> getApplyDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 128);
        try {
            PageData pageData = projectApplyService.getApplyDetail(pd);
            return PropertyUtil.pushData(pageData, ProjectApplyDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询项目立项申请详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


    @ApiOperation(value = "列表提交")
    @PostMapping(value = "/submit")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "businessId", value = "业务主键id", required = true, dataType = "string"),
            @ApiImplicitParam(name = "menuCode", value = "菜单编码", required = true, dataType = "Integer")})
    public ResponseEntity submitRecord(){
        PageData pd = this.getParams();
        CheckParameter.checkPositiveInt(pd.getString("menuCode"), "菜单编码");
        ResponseEntity result = null;
        // 编辑时，状态只能为已保存
        PageData recordData = (PageData) dao.findForObject("OtherCheckMapper.queryOneRecord", pd);
        String requestStatus = recordData.getString("processStatus");
        if (!requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0])) {
            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc());
        }
        checkParam(recordData);

        try {
            pd.put("serialNumber",recordData.getString("serialNumber"));
            pd.put("creatorOrgId",recordData.getString("creatorOrgId"));
            projectApplyService.submitRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SUBMIT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SUBMIT_FAIL.val(), e.getMessage());
            logger.error("项目立项申请提交失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 4, "项目立项申请", pd);
        }
    }


    @ApiOperation(value = "审批(同意/回退)")
    @PostMapping(value = "/approve")
    public ResponseEntity approveRecord(FlowApproveDto flowApproveDto){
        PageData pd = this.getParams();
        CheckParameter.checkPositiveInt(pd.getString("menuCode"), "菜单编码");
        String noted = pd.getString("noted");
        CheckParameter.stringLengthAndEmpty(noted, "审批意见", 1024);
        String strProcessInstId = String.valueOf(pd.getInt("processInstId"));
        CheckParameter.checkPositiveInt(strProcessInstId, "工作流ID");
        ResponseEntity result = null;
        try {
            projectApplyService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("项目立项申请审批失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 8, "项目立项申请", pd);
        }
    }

    @ApiOperation(value = "废除")
    @PostMapping(value = "/abolish")
    public ResponseEntity abolishRecord(FlowAbolishDto flowAbolishDto){
        PageData pd = this.getParams();
        CheckParameter.checkPositiveInt(pd.getString("menuCode"), "菜单编码");
        String noted = pd.getString("noted");
        CheckParameter.stringLengthAndEmpty(noted, "审批意见", 1024);
        String strProcessInstId = String.valueOf(pd.getInt("processInstId"));
        CheckParameter.checkPositiveInt(strProcessInstId, "工作流ID");
        ResponseEntity result = null;
        try {
            projectApplyService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("项目立项申请废除失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 9, "项目立项申请", pd);
        }
    }


    @ApiOperation(value = "上传模板")
    @PostMapping(value = "/upload")
    public ResponseEntity upload(ProjectApplyUploadDto projectApplyUploadDto) {
        PageData pd = transforParams(projectApplyUploadDto);

        ResponseEntity result = null;
        try {
   //         lineConfigService.upload(dto.getFile(),pd);
            result = ResponseEntity.success(null, INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("上传模板,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 9, "线路模板", pd);
        }

    }




    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        //取出菜单id
        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        try {
            HSSFWorkbook wb = projectApplyService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "用户管理_" + df.format(new Date()) + "_"+serialNumber +".xls";
            res.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = res.getOutputStream();
            wb.write(out);
            out.close();
            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目立项申请EXCEL", pd);
        }
    }



    @ApiOperation(value = "导出PDF", notes = "导出PDF")
    @PostMapping(value = "/exportPdf")
    public ResponseEntity exportPdf(BusinessIdListDto businessIdListDto) {
        PageData pageData = this.getParams();
        ResponseEntity result = null;
        try {
            //业务主数据
            PageData request = (PageData) dao.findForObject("EqMaintenanceRegisterMapper.queryMaintenanceRegister", pageData);
            logger.info("业务主数据 === " + request);
            //设备维保明细
            List<PageData> detailList = (List<PageData>) dao.findForList("EqMaintenanceRegisterMapper.selectMaintenanceDetail", pageData);
            logger.info("明细表数据 === " + detailList);
            //明细子表数据
            if(detailList.size()==0){
                detailList.add(new PageData());
            }
            List<PageData> detailChildList = (List<PageData>) dao.findForList("EqMaintenanceRegisterMapper.queryChildDetailById", detailList);
            logger.info("明细子表数据 === " + detailChildList);
            //查询合同数据
            PageData shieldData = (PageData) dao.findForObject("EqMaintenanceRegisterMapper.queryContractAndDic", pageData);
            logger.info("合同数据 ====" + shieldData);
     //       result = shieldContractExport(request, shieldData, detailList, detailChildList);
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目立项申请PDF", pageData);
        }
    }



    @ApiOperation(value = "导出WORD", notes = "导出WORD")
    @PostMapping(value = "/exportWord")
    public ResponseEntity exportWord(BusinessIdListDto businessIdListDto) {
        PageData pageData = this.getParams();
        ResponseEntity result = null;
        try {
            //业务主数据
            PageData request = (PageData) dao.findForObject("EqMaintenanceRegisterMapper.queryMaintenanceRegister", pageData);
            logger.info("业务主数据 === " + request);
            //设备维保明细
            List<PageData> detailList = (List<PageData>) dao.findForList("EqMaintenanceRegisterMapper.selectMaintenanceDetail", pageData);
            logger.info("明细表数据 === " + detailList);
            //明细子表数据
            if(detailList.size()==0){
                detailList.add(new PageData());
            }
            List<PageData> detailChildList = (List<PageData>) dao.findForList("EqMaintenanceRegisterMapper.queryChildDetailById", detailList);
            logger.info("明细子表数据 === " + detailChildList);
            //查询合同数据
            PageData shieldData = (PageData) dao.findForObject("EqMaintenanceRegisterMapper.queryContractAndDic", pageData);
            logger.info("合同数据 ====" + shieldData);
            //       result = shieldContractExport(request, shieldData, detailList, detailChildList);
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目立项申请PDF", pageData);
        }
    }




    private PageData transforParams(ProjectApplyUploadDto dto){
        PageData pageData = new PageData();

        String  createUserId = dto.getCreateUserId();
        CheckParameter.stringLengthAndEmpty(createUserId, "上传人id",128);

        String  createUser = dto.getCreateUser();
        CheckParameter.stringLengthAndEmpty(createUser, "上传人姓名",128);

        String  creatorOrgId = dto.getCreatorOrgId();
        CheckParameter.stringLengthAndEmpty(creatorOrgId, "右上角项目ID",128);

        String  creatorOrgName = dto.getCreatorOrgName();
        CheckParameter.stringLengthAndEmpty(creatorOrgName, "右上角项目名称",128);

        String  menuCode = dto.getMenuCode();
        CheckParameter.stringLengthAndEmpty(menuCode, "菜单编码",128);

        String  fileType = dto.getFileType();
        CheckParameter.stringLengthAndEmpty(fileType, "文件类型",128);

        MultipartFile file = dto.getFile();
        CheckParameter.isNull(file, "上传文件");

        pageData.put("creatorOrgId",creatorOrgId);
        pageData.put("creatorOrgName",creatorOrgName);
        pageData.put("createUserId",createUserId);
        pageData.put("createUser",createUser);
        pageData.put("menuCode",menuCode);
        pageData.put("fileType",fileType);

        return pageData;
    }

    private void checkParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        String operationType = pd.getString("operationType");
        if(operationType.equals("1")){
            return;
        }

        CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserId"), "编制人ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserName"), "编制人", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("createdDate"), "编制日期", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("unitName"), "单位名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("unitAddress"), "单位地址", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("applyUserName"), "申请人", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("applyUserId"), "申请人ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("gender"), "性别", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("genderCode"), "性别编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("age"), "年龄", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("postName"), "职务", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("postCode"), "职务编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("telephone"), "电话号码", 256);
        CheckParameter.checkDecimal(pd.getString("applyAmount"), "申请经费",20,2);
        CheckParameter.stringLengthAndEmpty(pd.getString("startYear"), "起始年度", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("endYear"), "结束年度", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("zipCode"), "邮编", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectTypeCode"), "项目类型编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectType"), "项目类型", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("professionalCategoryCode"), "专业类别编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("professionalCategory"), "专业类别", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("identify"), "是否鉴定", 256);
        CheckParameter.stringLengthAndEmpty1(pd.getString("researchContents"), "研究内容题要", 200);
        CheckParameter.stringLengthAndEmpty1(pd.getString("reviewComments"), "申报单位审查意见", 200);


        //校验立项调研信息

        CheckParameter.stringLengthAndEmpty1(pd.getString("currentSituation"), "国内外现状", 200);
        CheckParameter.stringLengthAndEmpty1(pd.getString("purposeSignificance"), "研发目的和意义", 200);
        CheckParameter.stringLengthAndEmpty1(pd.getString("contentMethod"), "主要研究内容及研究方法", 200);
        CheckParameter.stringLengthAndEmpty1(pd.getString("targetResults"), "要达到的目标、成果形式及主要技术指标", 200);
        CheckParameter.stringLengthAndEmpty1(pd.getString("basicConditions"), "现有研发条件和工作基础", 200);
        CheckParameter.stringLengthAndEmpty1(pd.getString("innovationPoints"), "研发项目创新点", 200);
        CheckParameter.stringLengthAndEmpty1(pd.getString("feasibilityAnalysis"), "成果转化的可行性分析", 200);


        //校验进度计划
        List<PageData> progressPlanList = JSONObject.parseArray(pd.getString("progressPlan"), PageData.class);
        if(!CollectionUtils.isEmpty(progressPlanList)){
            for(PageData progressPlan : progressPlanList){
                CheckParameter.stringLengthAndEmpty(progressPlan.getString("years"), "年度", 256);
                CheckParameter.stringLengthAndEmpty(progressPlan.getString("planTarget"), "计划及目标", 200);
                CheckParameter.stringLengthAndEmpty(progressPlan.getString("creatorUserId"), "编制人ID", 200);
                CheckParameter.stringLengthAndEmpty(progressPlan.getString("creatorUser"), "要达到的目标、编制人", 200);
                CheckParameter.stringLengthAndEmpty(progressPlan.getString("createTime"), "编制时间", 200);

            }
        }else{
            throw new MyException("进度计划不能为空");

        }

        //校验参加单位
        List<PageData> attendUnitList = JSONObject.parseArray(pd.getString("attendUnit"), PageData.class);
        if(!CollectionUtils.isEmpty(attendUnitList)){
            for(PageData attendUnit : attendUnitList){
                CheckParameter.stringLengthAndEmpty(attendUnit.getString("unitName"), "单位名称", 256);
                CheckParameter.stringLengthAndEmpty(attendUnit.getString("taskDivision"), "研究任务及分工", 200);
                CheckParameter.stringLengthAndEmpty(attendUnit.getString("creatorUserId"), "编制人ID", 200);
                CheckParameter.stringLengthAndEmpty(attendUnit.getString("creatorUser"), "要达到的目标、编制人", 200);
                CheckParameter.stringLengthAndEmpty(attendUnit.getString("createTime"), "编制时间", 200);

            }
        }else{
            throw new MyException("参加单位不能为空");

        }

        //校验研究人员
        List<PageData> researchUserList = JSONObject.parseArray(pd.getString("researchUser"), PageData.class);
        if(!CollectionUtils.isEmpty(researchUserList)){
            for(PageData researchUser : researchUserList){
                CheckParameter.stringLengthAndEmpty(researchUser.getString("userName"), "姓名", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("idCard"), "身份证", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("age"), "年龄", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("gender"), "性别", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("education"), "学历", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("belongDepartment"), "所属部门", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("belongPost"), "所属职务", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("majorStudied"), "所学专业", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("majorWorked"), "从事专业", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("belongUnit"), "所在单位", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("taskDivision"), "研究任务及分工", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("workRate"), "全时率", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("telephone"), "联系电话", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("startDate"), "参与研究开始日期", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("endDate"), "参与研究结束日期", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("creatorUserId"), "编制人ID", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("creatorUser"), "编制人", 256);
                CheckParameter.stringLengthAndEmpty(researchUser.getString("createTime"), "编制时间", 256);

            }
        }else{
            throw new MyException("研究人员不能为空");

        }

        //校验经费来源预算
        List<PageData> budgetSourceList = JSONObject.parseArray(pd.getString("budgetSource"), PageData.class);
        if(!CollectionUtils.isEmpty(budgetSourceList)){
            for(PageData budgetSource : budgetSourceList){
                CheckParameter.checkDecimal(budgetSource.getString("totalBudget"), "来源预算合计",20,2);
                checkBudgetSource(budgetSource);

            }
        }else{
            throw new MyException("经费来源预算不能为空");

        }

        //校验经费支出预算
        List<PageData> budgetExpensesList = JSONObject.parseArray(pd.getString("budgetExpenses"), PageData.class);
        if(!CollectionUtils.isEmpty(budgetExpensesList)){
            for(PageData budgetExpenses : budgetExpensesList){
                CheckParameter.checkDecimal(budgetExpenses.getString("totalBudget"), "支出预算合计",20,2);
                checkBudgetExpense(budgetExpenses);

            }
        }else{
            throw new MyException("经费支出预算不能为空");

        }

        //校验经费预算-每月预算

        CheckParameter.checkDecimal(pd.getString("totalSourceBudget"), "来源预算合计", 20, 2);
        checkBudgetSource(pd);

        CheckParameter.checkDecimal(pd.getString("totalExpenseBudget"), "支出预算合计", 20, 2);
        checkBudgetExpense(pd);


        //校验年度预算（按月填报）
        List<PageData> MonthList = JSONObject.parseArray(pd.getString("MonthList"), PageData.class);
        if(!CollectionUtils.isEmpty(MonthList)){
            for(PageData month : MonthList){
                CheckParameter.checkDecimal(month.getString("january"), "1月",20,2);
                CheckParameter.checkDecimal(month.getString("february"), "2月",20,2);
                CheckParameter.checkDecimal(month.getString("march"), "3月",20,2);
                CheckParameter.checkDecimal(month.getString("april"), "4月",20,2);
                CheckParameter.checkDecimal(month.getString("may"), "5月",20,2);
                CheckParameter.checkDecimal(month.getString("june"), "6月",20,2);
                CheckParameter.checkDecimal(month.getString("july"), "7月",20,2);
                CheckParameter.checkDecimal(month.getString("august"), "8月",20,2);
                CheckParameter.checkDecimal(month.getString("september"), "9月",20,2);
                CheckParameter.checkDecimal(month.getString("october"), "10月",20,2);
                CheckParameter.checkDecimal(month.getString("november"), "11月",20,2);
                CheckParameter.checkDecimal(month.getString("december"), "12月",20,2);
                CheckParameter.stringLengthAndEmpty(month.getString("years"), "年份", 256);

            }
        }else{
            throw new MyException("年度预算（按月填报）不能为空");

        }



        //校验拨款计划
        List<PageData> appropriationList = JSONObject.parseArray(pd.getString("appropriationPlan"), PageData.class);
        if(!CollectionUtils.isEmpty(appropriationList)){
            for(PageData appropriationPlan : appropriationList){
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("years"), "年度", 256);
                CheckParameter.checkDecimal(appropriationPlan.getString("计划(万元)"), "人员费",20,2);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("creatorUserId"), "编制人ID", 256);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("creatorUser"), "编制人", 256);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("createTime"), "编制时间", 256);

            }
        }else{
            throw new MyException("拨款计划不能为空");

        }

    }

    private void checkBudgetSource(PageData pd ){
        CheckParameter.checkDecimal(pd.getString("companyAppropriation"), "股份公司计划拨款",20,2);
        CheckParameter.checkDecimal(pd.getString("stateAppropriation"), "国家拨款",20,2);
        CheckParameter.checkDecimal(pd.getString("provincesAppropriation"), "省市拨款",20,2);
        CheckParameter.checkDecimal(pd.getString("unitFunds"), "单位自筹款",20,2);
        CheckParameter.checkDecimal(pd.getString("bankLoans"), "银行贷款",20,2);
        CheckParameter.checkDecimal(pd.getString("otherSource"), "其他来源款",20,2);
    }

    private void checkBudgetExpense(PageData pd ){
        CheckParameter.checkDecimal(pd.getString("staffCost"), "人员费",20,2);
        CheckParameter.checkDecimal(pd.getString("equipmentCost"), "设备费",20,2);
        CheckParameter.checkDecimal(pd.getString("materialCost"), "材料费",20,2);
        CheckParameter.checkDecimal(pd.getString("fuelCost"), "燃料及动力费",20,2);
        CheckParameter.checkDecimal(pd.getString("assayCost"), "测试及化验费",20,2);
        CheckParameter.checkDecimal(pd.getString("travelCost"), "差旅费",20,2);
        CheckParameter.checkDecimal(pd.getString("meetingCost"), "会议费",20,2);
        CheckParameter.checkDecimal(pd.getString("managementCost"), "课题管理费",20,2);
        CheckParameter.checkDecimal(pd.getString("otherCost"), "其他费用",20,2);
        CheckParameter.checkDecimal(pd.getString("exchangeCost"), "国际合作交流费",20,2);
        CheckParameter.checkDecimal(pd.getString("communicationCost"), "出版/文献/信息传播",20,2);
        CheckParameter.checkDecimal(pd.getString("propertyCost"), "知识产权事务",20,2);
        CheckParameter.checkDecimal(pd.getString("expertCost"), "专家费",20,2);
        CheckParameter.checkDecimal(pd.getString("other"), "其他",20,2);
        CheckParameter.checkDecimal(pd.getString("designCost"), "新产品设计费",20,2);
        CheckParameter.checkDecimal(pd.getString("expensesCost"), "委托研发费用",20,2);
    }

}
