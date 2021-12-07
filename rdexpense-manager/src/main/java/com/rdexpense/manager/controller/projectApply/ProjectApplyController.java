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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.*;
import static com.common.util.ConstantMsgUtil.ERR_EXPORT_FAIL;
import static com.common.util.DateCheckUtil.checkOrder;

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

    private static final String FILE_PREFIX = "项目立项申请_";


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
        CheckParameter.checkDefaultParams(pd);
        String operationType = pd.getString("operationType");
        if(operationType.equals("2")){
            checkParam(pd,2);
            String check = submitCheck(pd,2);
            if(StringUtils.isNotBlank(check)){
                return ResponseEntity.success(check, check);
            }

        }

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

        // 编辑时，状态只能为已保存
        PageData recordData = (PageData) dao.findForObject("ProjectApplyMapper.queryApplyDetail", pd);
        String requestStatus = recordData.getString("processStatus");
        if (!requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0]) && !requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[3])) {
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc());
        }

        CheckParameter.checkDefaultParams(pd);
        String operationType = pd.getString("operationType");
        if(operationType.equals("2")){
            checkParam(pd,2);
            String check = submitCheck(pd,2);
            if(StringUtils.isNotBlank(check)){
                return ResponseEntity.success(check, check);
            }

        }

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
        CheckParameter.checkBusinessIdList(pd);

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



    @ApiOperation(value = "查询申请详情")
    @PostMapping("/queryDetail")
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
        PageData recordData = projectApplyService.getApplyDetail(pd);
        String requestStatus = recordData.getString("processStatus");
        if (!requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0]) && !requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[3])) {
            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc());
        }

        checkParam(recordData,1);

        String check =  submitCheck(recordData,1);
        if(StringUtils.isNotBlank(check)){
            return ResponseEntity.success(check, check);
        }


        try {
            pd.put("serialNumber",recordData.getString("serialNumber"));
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
        checkApprove(pd);
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


    @ApiOperation(value = "废除（未开发）")
    @PostMapping(value = "/abolish")
    public ResponseEntity abolishRecord(FlowAbolishDto flowAbolishDto){
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
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



    @ApiOperation(value = "导入全部数据")
    @PostMapping(value = "/uploadAll")
    public ResponseEntity upload(UploadTemplateFileDto dto) {
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            projectApplyService.uploadAll(dto.getFile(),pd);
            result = ResponseEntity.success(null, INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
            logger.error("导入立项申请全部数据失败,request=[{}]", pd);
            throw new MyException(ERR_UPLOAD_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 7, "立项申请全部数据", pd);
        }

    }



    @ApiOperation(value = "导入主信息")
    @PostMapping("/uploadMain")
    public ResponseEntity<ProjectApplyDetailDto> uploadMain(UploadTemplateFileDto dto) throws Exception{
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            PageData pageData = projectApplyService.uploadMain(dto.getFile(),pd);

            result = PropertyUtil.pushData(pageData, ProjectApplyDetailDto.class, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入立项申请主信息失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), e.getErrorMsg());
        }
    }


    @ApiOperation(value = "导入立项调研信息")
    @PostMapping("/uploadSurvey")
    public ResponseEntity<ProjectApplyDetailDto> uploadSurvey(UploadTemplateFileDto dto) throws Exception{
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            PageData pageData = projectApplyService.uploadSurvey(dto.getFile(),pd);
            result = PropertyUtil.pushData(pageData, ProjectApplyDetailDto.class, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入立项调研信息失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), ERR_UPLOAD_FAIL.desc());
        }
    }


    @ApiOperation(value = "导入进度计划")
    @PostMapping(value = "/uploadProgress")
    public ResponseEntity<List<ProgressPlanDto>> uploadProgress(UploadTemplateFileDto dto) {
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadProgress(dto.getFile(),pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, ProgressPlanDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("导入进度计划失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导入参加单位")
    @PostMapping(value = "/uploadUnit")
    public ResponseEntity<List<AttendUnitDto>> uploadUnit(UploadTemplateFileDto dto) {
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadUnit(dto.getFile(),pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, AttendUnitDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("导入参加单位失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导入研究人员（初始）")
    @PostMapping(value = "/uploadUser")
    public ResponseEntity<List<ResearchUserDto>> uploadUser(UploadTemplateFileDto dto) {
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadUser(dto.getFile(),pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, ResearchUserDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("导入研究人员（初始）失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导入经费预算")
    @PostMapping("/uploadBudget")
    public ResponseEntity<List<BudgetAddDto>> uploadBudget(UploadTemplateFileDto dto) throws Exception{
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadBudget(dto.getFile(),pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, BudgetAddDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入经费预算失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), ERR_UPLOAD_FAIL.desc());
        }
    }

    @ApiOperation(value = "导入经费预算（每月预算）")
    @PostMapping("/uploadMonth")
    public ResponseEntity uploadMonth(UploadTemplateFileDto dto) throws Exception {
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadMonth(dto.getFile(), pd);
            result = ResponseEntity.success(list, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入经费预算（每月预算）失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), ERR_UPLOAD_FAIL.desc());
        }

    }


    @ApiOperation(value = "导入拨款计划")
    @PostMapping(value = "/uploadAppropriation")
    public ResponseEntity<List<AppropriationPlanDto>> uploadAppropriation(UploadTemplateFileDto dto) {
        PageData pd = FileParamsUtil.checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadAppropriation(dto.getFile(),pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, AppropriationPlanDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("导入拨款计划失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        CheckParameter.checkBusinessIdList(pd);

        HttpServletResponse response = this.getResponse();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String number = SerialNumberUtil.generateSerialNo("projectApplyExcel");

        try {


            String businessIdStr = pd.getString("businessIdList");
            List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);

            if(businessIdList.size() == 1){
                HSSFWorkbook wb = projectApplyService.exportExcel(businessIdList.get(0));
                String fileName = FILE_PREFIX+date+"_"+number+".xls";
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = response.getOutputStream();
                wb.write(out);
                out.close();

            }else {
                //文件名
                String fileName = FILE_PREFIX+date+"_"+number+".zip";
                //输出流
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // 压缩流
                ZipOutputStream zos = new ZipOutputStream(bos);
                //生成excel
                projectApplyService.exportZip(1,businessIdList, zos, bos,FILE_PREFIX);
                zos.flush();
                zos.close();
                //写入返回response
                response.reset();
                response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(bos.toByteArray());
                out.flush();
                out.close();
            }

            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目立项申请EXCEL", pd);
        }
    }



    @ApiOperation(value = "导出PDF")
    @PostMapping(value = "/exportPdf")
    public ResponseEntity exportPdf(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkBusinessIdList(pd);
        ResponseEntity result = null;

        HttpServletResponse response = this.getResponse();
        try {

            String businessIdStr = pd.getString("businessIdList");
            List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            String number = SerialNumberUtil.generateSerialNo("projectApplyPdf");


            if(businessIdList.size() == 1){
                projectApplyService.exportWordPdf(2,businessIdList.get(0),response,FILE_PREFIX);
            }else {
                //文件名
                String fileName = FILE_PREFIX+date+"_"+number+".zip";
                //输出流
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // 压缩流
                ZipOutputStream zos = new ZipOutputStream(bos);

                projectApplyService.exportZip(2,businessIdList, zos, bos,FILE_PREFIX);
                zos.flush();
                zos.close();
                //写入返回response
                response.reset();
                response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(bos.toByteArray());
                out.flush();
                out.close();
            }


            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目立项申请PDF", pd);
        }
    }



    @ApiOperation(value = "导出WORD")
    @PostMapping(value = "/exportWord")
    public ResponseEntity exportWord(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        CheckParameter.checkBusinessIdList(pd);

        HttpServletResponse response = this.getResponse();
        try {
            String businessIdStr = pd.getString("businessIdList");
            List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            String number = SerialNumberUtil.generateSerialNo("projectApplyWord");


            if(businessIdList.size() == 1){
                projectApplyService.exportWordPdf(1,businessIdList.get(0),response,FILE_PREFIX);

            }else {
                //文件名
                String fileName = FILE_PREFIX+date+"_"+number+".zip";
                //输出流
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // 压缩流
                ZipOutputStream zos = new ZipOutputStream(bos);

                projectApplyService.exportZip(3,businessIdList, zos, bos,FILE_PREFIX);
                zos.flush();
                zos.close();
                //写入返回response
                response.reset();
                response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(bos.toByteArray());
                out.flush();
                out.close();
            }
            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目立项申请PDF", pd);
        }
    }


    @ApiOperation(value = "预览合同")
    @PostMapping(value = "/preview")
    public ResponseEntity preview(ProjectApplyAddDto projectApplyAddDto) {
        PageData pd = this.getParams();
        HttpServletResponse response = this.getResponse();
        try {
            //业务主数据
            projectApplyService.preview(pd,response);

            ResponseEntity result = ResponseEntity.success(null, INFO_PREVIEW_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            throw new MyException(ERR_PREVIEW_FAIL.desc(), e);
        }
    }




    private void checkApprove(PageData pd){
        CheckParameter.checkDefaultParams(pd);

        CheckParameter.stringLengthAndEmpty(pd.getString("waitId"), "待办列表waitId", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("approveComment"), "审批意见", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("approveType"), "审批类型", 256);

    }



    private void checkParam(PageData pd,int flag) {

 //       CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserId"), "编制人ID", 256);
 //       CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserName"), "编制人", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("createdDate"), "编制日期", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("unitName"), "单位名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("unitAddress"), "单位地址", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("applyUserName"), "申请人", 256);
 //       CheckParameter.stringLengthAndEmpty(pd.getString("applyUserId"), "申请人ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("gender"), "性别", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("genderCode"), "性别编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("age"), "年龄", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("postName"), "职务", 256);
//        CheckParameter.stringLengthAndEmpty(pd.getString("postCode"), "职务编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("telephone"), "电话号码", 256);
        CheckParameter.checkDecimal(pd.getString("applyAmount"), "申请经费",20,2);
        CheckParameter.stringLengthAndEmpty(pd.getString("startYear"), "起始年度", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("endYear"), "结束年度", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("zipCode"), "邮编", 256);
 //       CheckParameter.stringLengthAndEmpty(pd.getString("projectTypeCode"), "项目类型编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectType"), "项目类型", 256);
 //       CheckParameter.stringLengthAndEmpty(pd.getString("professionalCategoryCode"), "专业类别编码", 256);
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
        List<PageData> progressPlanList = null;
        if(flag == 1){
            progressPlanList = (List<PageData>) pd.get("progressPlan");
        }else {
            progressPlanList = JSONObject.parseArray(pd.getString("progressPlan"), PageData.class);
        }

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
        List<PageData> attendUnitList = null;
        if(flag == 1){
            attendUnitList = (List<PageData>) pd.get("attendUnit");
        }else {
            attendUnitList = JSONObject.parseArray(pd.getString("attendUnit"), PageData.class);
        }

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
        List<PageData> researchUserList = null;
        if(flag == 1){
            researchUserList = (List<PageData>) pd.get("researchUser");
        }else {
            researchUserList = JSONObject.parseArray(pd.getString("researchUser"), PageData.class);
        }

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


        //校验经费预算
        List<PageData> budgetList = null;
        if(flag == 1){
            budgetList = (List<PageData>) pd.get("budgetList");
        }else {
            budgetList = JSONObject.parseArray(pd.getString("budgetList"), PageData.class);
        }

        if(!CollectionUtils.isEmpty(budgetList)){
            for(PageData budget : budgetList){
                if(StringUtils.isNotBlank(budget.getString("sourceAccount"))){
                    CheckParameter.checkDecimal(budget.getString("sourceBudget"), "来源预算数",20,2);
                }

                CheckParameter.stringLengthAndEmpty(budget.getString("expenseAccount"), "支出科目", 256);
                CheckParameter.checkDecimal(budget.getString("expenseBudget"), "支出预算数",20,2);

            }
        }else{
            throw new MyException("经费预算不能为空");

        }


        //校验经费预算（每月填报）
        List<PageData> monthList = null;
        if(flag == 1){
            monthList = (List<PageData>) pd.get("monthList");
        }else {
            monthList = JSONObject.parseArray(pd.getString("monthList"), PageData.class);
        }

        if(!CollectionUtils.isEmpty(monthList)){
            for(PageData budget : monthList){
                if(StringUtils.isNotBlank(budget.getString("sourceaccount"))){
                    CheckParameter.checkDecimal(budget.getString("sourcebudget"), "来源预算数",20,2);
                }

                CheckParameter.stringLengthAndEmpty(budget.getString("expenseaccount"), "支出科目", 256);
                CheckParameter.checkDecimal(budget.getString("expensebudget"), "支出预算数",20,2);

            }
        }else{
            throw new MyException("经费预算不能为空");

        }


        //校验拨款计划
        List<PageData> appropriationList = null;
        if(flag == 1){
            appropriationList = (List<PageData>) pd.get("appropriationPlan");
        }else {
            appropriationList = JSONObject.parseArray(pd.getString("appropriationPlan"), PageData.class);
        }

        if(!CollectionUtils.isEmpty(appropriationList)){
            for(PageData appropriationPlan : appropriationList){
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("years"), "年度", 256);
                CheckParameter.checkDecimal(appropriationPlan.getString("planAmount"), "计划(万元)",20,2);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("creatorUserId"), "编制人ID", 256);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("creatorUser"), "编制人", 256);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("createTime"), "编制时间", 256);

            }
        }else{
            throw new MyException("拨款计划不能为空");

        }

    }



    private String submitCheck(PageData pd,int flag){


        List<PageData> userList = null;
        if(flag == 1){
            userList = (List<PageData>) pd.get("researchUser");
        }else {
            userList = JSONObject.parseArray(pd.getString("researchUser"), PageData.class);
        }

        List<PageData> budgetList = null;
        if(flag == 1){
            budgetList = (List<PageData>) pd.get("budgetList");
        }else {
            budgetList = JSONObject.parseArray(pd.getString("budgetList"), PageData.class);
        }


        //1、判断人员项目周期

        //判断人员是否存在不容岗位
        pd.put("dicTypeId",1021);
        List<PageData> dicList = (List<PageData>)dao.findForList("DictionaryMapper.queryDictionariesList",pd);
        List<String> nameList = null;
        if(!CollectionUtils.isEmpty(dicList)){
            nameList = dicList.stream().map(e -> e.getString("dicEnumName")).collect(Collectors.toList());
        }



        String startYear = pd.getString("startYear");
        String endYear = pd.getString("endYear");

        String year = checkOrder(startYear,endYear);
        if(year.equals("than")){
            throw new MyException("项目的起始年度不能大于结束年度");
        }

        if(!CollectionUtils.isEmpty(userList)){
            for(PageData user : userList){

                String userName = user.getString("userName");
                String belongPost = user.getString("belongPost");
                if(!CollectionUtils.isEmpty(nameList) && nameList.contains(belongPost)){
                    throw new MyException(userName+"存在非研发岗位，请确认调整后重新提交");
                }


                String startDate = user.getString("startDate");
                String endDate = user.getString("endDate");

                String date = checkOrder(startDate,endDate);
                if(date.equals("than")){
                    throw new MyException(userName+"参与研发的开始时间不能大于结束时间");
                }

                String date1 = checkOrder(startYear,startDate);
                if(date1.equals("than")){
                    throw new MyException(userName+"参与研发周期已超出项目周期，请确认调整后重新提交");
                }

                String date2 = checkOrder(endYear,endDate);
                if(date2.equals("less")){
                    throw new MyException(userName+"参与研发周期已超出项目周期，请确认调整后重新提交");
                }


            }

        }



        StringBuffer buffer = new StringBuffer();
        String confirmSubmit = pd.getString("confirmSubmit");
        if(StringUtils.isNotBlank(confirmSubmit) && confirmSubmit.equals("1")){
            return buffer.toString();
        }

        //判断研发费用预算是否超值
        if(!CollectionUtils.isEmpty(budgetList)){
            BigDecimal total = new BigDecimal(0);//总费用
            BigDecimal material = new BigDecimal(0);//材料
            BigDecimal equipment = new BigDecimal(0);//设备
            BigDecimal artificial = new BigDecimal(0);//人工

            for(PageData data : budgetList){
                String expenseAccount = data.getString("expenseAccount");
                String expenseBudget = data.getString("expenseBudget");
                if(expenseAccount.equals("支出预算合计")){
                    total = new BigDecimal(expenseBudget);
                }else if(expenseAccount.equals("三、材料费")){
                    material = new BigDecimal(expenseBudget);
                }else if(expenseAccount.equals("二、设备费")){
                    equipment = new BigDecimal(expenseBudget);
                }else if(expenseAccount.equals("一、人员费")){
                    artificial = new BigDecimal(expenseBudget);
                }
            }

            BigDecimal other = total.subtract(material).subtract(equipment).subtract(artificial);//其他


            //查询规则配置的阈值
            //2:材料费,3:机械设备使用费,4:人工费,5:其他费用
            List<PageData> ruleList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryRuleList",pd);
            BigDecimal materialRate = new BigDecimal(75);//材料
            BigDecimal equipmentRate = new BigDecimal(15);//设备
            BigDecimal artificialRate= new BigDecimal(10);//人工
            BigDecimal otherRate = new BigDecimal(10);//其他费用

            if(!CollectionUtils.isEmpty(ruleList)){
                for(PageData data : ruleList){
                    String ruleType = data.getString("ruleType");
                    String ruleValue = data.getString("ruleValue");
                    if(ruleType.equals("5")){
                        otherRate = new BigDecimal(ruleValue);
                    }else if(ruleType.equals("2")){
                        materialRate = new BigDecimal(ruleValue);
                    }else if(ruleType.equals("3")){
                        equipmentRate = new BigDecimal(ruleValue);
                    }else if(ruleType.equals("4")){
                        artificialRate = new BigDecimal(ruleValue);
                    }
                }
            }


            //1、材料费不得超过总预算的75%

            if(material.divide(total).multiply(new BigDecimal(100)).compareTo(materialRate) == 1){
                buffer.append("材料费");
            }

            //2、机械使用费不得低于总预算的15%
            if(equipment.divide(total).multiply(new BigDecimal(100)).compareTo(equipmentRate) == -1){
                buffer.append("、机械设备费");
            }

            //3、人工费不得低于总预算的10%
            if(artificial.divide(total).multiply(new BigDecimal(100)).compareTo(artificialRate) == -1){
                buffer.append("、人工费");
            }

            //4、其他费用不得低于总预算的10%
            if(other.divide(total).multiply(new BigDecimal(100)).compareTo(otherRate) == 1){
                buffer.append("、其他费用");
            }

        }



        if(StringUtils.isNotBlank(buffer.toString())){
            buffer.append("不符合公司规定的预算比例，请确认调整后重新提交");
        }

        return buffer.toString();

    }

}
