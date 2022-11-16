package com.rdexpense.manager.controller.projectApply;


import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.base.FlowAbolishDto;
import com.rdexpense.manager.dto.base.FlowApproveDto;
import com.rdexpense.manager.dto.projectApply.*;
import com.rdexpense.manager.service.projectApply.ProjectApplyService;
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

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
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
//        CheckParameter.checkSearchParams(pd);

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
            String check = submitCheck(pd,2,2);
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
            String check = submitCheck(pd,2,2);
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
        recordData.put("confirmSubmit",pd.getString("confirmSubmit"));
        recordData.put("companyId",pd.getString("companyId"));
        String check =  submitCheck(recordData,1,1);
        if(StringUtils.isNotBlank(check)){
            return ResponseEntity.success(check, check);
        }


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
    public ResponseEntity upload(ProjectApplyUploadFileDto dto) {
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            projectApplyService.uploadAll(dto.getFile(),pd);
            result = ResponseEntity.success(null, INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
            logger.error("导入立项申请全部数据失败,request=[{}]", pd);
            return  result;

        } finally {
            logUtil.saveLogData(result.getCode(), 7, "立项申请全部数据", pd);
        }

    }



    @ApiOperation(value = "导入主信息")
    @PostMapping("/uploadMain")
    public ResponseEntity<ProjectApplyMainDto> uploadMain(ProjectApplyUploadFileDto dto) throws Exception{
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            PageData pageData = projectApplyService.uploadMain(dto.getFile(),pd);

            result = PropertyUtil.pushData(pageData, ProjectApplyMainDto.class, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入立项申请主信息失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导入立项调研信息")
    @PostMapping("/uploadSurvey")
    public ResponseEntity<ProjectApplySurveyDto> uploadSurvey(ProjectApplyUploadFileDto dto) throws Exception{
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            PageData pageData = projectApplyService.uploadSurvey(dto.getFile(),pd);
            result = PropertyUtil.pushData(pageData, ProjectApplySurveyDto.class, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入立项调研信息失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导入进度计划")
    @PostMapping(value = "/uploadProgress")
    public ResponseEntity<List<ProgressPlanDto>> uploadProgress(ProjectApplyUploadFileDto dto) {
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadProgress(dto.getFile(),pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, ProgressPlanDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("导入进度计划失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(),e.getMessage());
        }
    }


    @ApiOperation(value = "导入参加单位")
    @PostMapping(value = "/uploadUnit")
    public ResponseEntity<List<AttendUnitDto>> uploadUnit(ProjectApplyUploadFileDto dto) {
        PageData pd = checkParams(dto);
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
    public ResponseEntity<List<ResearchUserDto>> uploadUser(ProjectApplyUploadFileDto dto) {
        PageData pd = checkParams(dto);
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
    public ResponseEntity<List<BudgetAddDto>> uploadBudget(ProjectApplyUploadFileDto dto) throws Exception{
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadBudget(dto.getFile(),pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, BudgetAddDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入经费预算失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }

    @ApiOperation(value = "导入经费预算（每月预算）")
    @PostMapping("/uploadMonth")
    public ResponseEntity uploadMonth(ProjectApplyUploadFileDto dto) throws Exception {
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = projectApplyService.uploadMonth(dto.getFile(), pd);
            result = ResponseEntity.success(list, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("导入经费预算（每月预算）失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(), e.getMessage());
        }

    }


    @ApiOperation(value = "导入拨款计划")
    @PostMapping(value = "/uploadAppropriation")
    public ResponseEntity<List<AppropriationPlanDto>> uploadAppropriation(ProjectApplyUploadFileDto dto) {
        PageData pd = checkParams(dto);
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
//        CheckParameter.stringLengthAndEmpty1(pd.getString("reviewComments"), "申报单位审查意见", 200);
        CheckParameter.stringEmpty(pd.getString("reviewComments"), "申报单位审查意见");

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
            int m = 0;
            for(PageData progressPlan : progressPlanList){
                m++;
                CheckParameter.stringLengthAndEmpty(progressPlan.getString("years"), "年度", 256);
                CheckParameter.stringLengthAndEmpty(progressPlan.getString("planTarget"), "第" + m + "行的计划及目标", 200);
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



    private String submitCheck(PageData pd,int flag,int submitType){


        List<PageData> userList = null;
        if(flag == 1){
            userList = (List<PageData>) pd.get("researchUser");
        }else {
            userList = JSONObject.parseArray(pd.getString("researchUser"), PageData.class);
        }

        List<PageData> budgetList = null;
        List<PageData> monthList = null;
        List<PageData> monthListNew = null;
        List<PageData> progressPlanList = null;
        if(flag == 1){
            budgetList = (List<PageData>) pd.get("budgetList");
            monthList = (List<PageData>) pd.get("monthList");
            monthListNew = (List<PageData>) pd.get("monthListNew");
            progressPlanList = (List<PageData>) pd.get("progressPlan");

        }else {
            budgetList = JSONObject.parseArray(pd.getString("budgetList"), PageData.class);
            monthListNew = JSONObject.parseArray(pd.getString("monthListNew"), PageData.class);
            monthList = JSONObject.parseArray(pd.getString("monthList"), PageData.class);

            progressPlanList = JSONObject.parseArray(pd.getString("progressPlan"), PageData.class);
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


        List<String> idCardList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(userList)){
            for(PageData user : userList){

                String userName = user.getString("userName");
                String belongPost = user.getString("belongPost");
                String idCard = user.getString("idCard");
                if(!CollectionUtils.isEmpty(nameList)){
                    for(String s : nameList){
                        if(belongPost.contains(s)){
                            throw new MyException(userName+"存在非研发岗位，请确认调整后重新提交");
                        }
                    }

                }

                if(idCardList.contains(idCard)){
                    throw new MyException("研究人员身份证号存在重复的");
                }
                idCardList.add(idCard);


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

        String bureauLevel = pd.getString("bureauLevel");

        if(bureauLevel.equals("0")) {//其他拨款

            //根据提交类型进行校验，1：列表提交，2：新建或编辑提交
            if (submitType == 1){
                //列表提交
                //判断研发费用预算是否超值
                if(!CollectionUtils.isEmpty(budgetList)) {
                    BigDecimal total = new BigDecimal(0);//总费用
                    BigDecimal material = new BigDecimal(0);//材料
                    BigDecimal equipment = new BigDecimal(0);//设备
                    BigDecimal artificial = new BigDecimal(0);//人工
                    BigDecimal other = new BigDecimal(0);//其他费用

                    BigDecimal sourceBudgetTotal = new BigDecimal(0.00);//来源预算之和
                    BigDecimal expenseBudgetTotal = new BigDecimal(0.00);//支出预算之和
                    BigDecimal sourceBudget = new BigDecimal(0.00);//来源预算合计

                    BigDecimal otherBudgetTotal = new BigDecimal(0.00);//九、其他费用的小项合计
                    BigDecimal otherBudget = new BigDecimal(0.00);//九、其他费用合计

                    String costStr = "五、测试及化验费|六、差旅费|七、会议费|八、课题管理费|九、其他费用|1、国际合作交流费|2、出版/文献/信息传播|3、知识产权事务|4、专家费|5、其他";
                    for (PageData data : budgetList) {
                        String expenseAccount = data.getString("expenseAccount");
                        String expenseBudget = data.getString("expenseBudget");
                        if (expenseAccount.equals("支出预算合计")) {
                            total = new BigDecimal(expenseBudget);
                            sourceBudget = new BigDecimal(data.getString("sourceBudget"));
                        } else if (expenseAccount.equals("三、材料费")) {
                            material = new BigDecimal(expenseBudget);
                        } else if (expenseAccount.equals("二、设备费")) {
                            equipment = new BigDecimal(expenseBudget);
                        } else if (expenseAccount.equals("一、人员费")) {
                            artificial = new BigDecimal(expenseBudget);
                        } else if (costStr.contains(expenseAccount)) {
                            other = other.add(new BigDecimal(expenseBudget));
                        }

                        if (!expenseAccount.equals("支出预算合计")) {

                            if (StringUtils.isNotBlank(data.getString("sourceBudget"))) {
                                sourceBudgetTotal = sourceBudgetTotal.add(new BigDecimal(data.getString("sourceBudget")));
                            }

                            if (StringUtils.isNotBlank(data.getString("expenseBudget")) &&
                                    !expenseAccount.equals("1、国际合作交流费") &&
                                    !expenseAccount.equals("2、出版/文献/信息传播") &&
                                    !expenseAccount.equals("3、知识产权事务") &&
                                    !expenseAccount.equals("4、专家费") &&
                                    !expenseAccount.equals("5、其他")) {
                                expenseBudgetTotal = expenseBudgetTotal.add(new BigDecimal(data.getString("expenseBudget")));
                            }
                        }

                        if (StringUtils.isNotBlank(data.getString("expenseBudget")) &&
                                (expenseAccount.equals("1、国际合作交流费") ||
                                        expenseAccount.equals("2、出版/文献/信息传播") ||
                                        expenseAccount.equals("3、知识产权事务") ||
                                        expenseAccount.equals("4、专家费") ||
                                        expenseAccount.equals("5、其他"))) {
                            otherBudgetTotal = otherBudgetTotal.add(new BigDecimal(data.getString("expenseBudget")));
                        }

                        if (expenseAccount.equals("九、其他费用")) {
                            otherBudget = new BigDecimal(expenseBudget);
                        }
                    }

                    if (sourceBudgetTotal.compareTo(sourceBudget) != 0) {
                        throw new MyException("经费来源预算合计不正确");
                    }

                    if (expenseBudgetTotal.compareTo(total) != 0) {
                        throw new MyException("经费支出预算合计不正确");
                    }

                    if (otherBudgetTotal.compareTo(otherBudget) != 0) {
                        throw new MyException("其他费用合计与子项合计不相等");
                    }

                    if (sourceBudgetTotal.compareTo(expenseBudgetTotal) != 0) {
                        throw new MyException("经费来源预算合计与支出预算合计不相等");
                    }

                    //经费预算(每月)
                    BigDecimal sourceBudgetTotalMonth = new BigDecimal(0.00);//来源预算之和
                    BigDecimal expenseBudgetTotalMonth = new BigDecimal(0.00);//支出预算之和
                    BigDecimal expenseBudgetMonth = new BigDecimal(0);
                    BigDecimal sourceBudgetMonth = new BigDecimal(0.00);
                    BigDecimal otherBudgetTotalMonth = new BigDecimal(0.00);//九、其他费用的小项合计
                    BigDecimal otherBudgetMonth = new BigDecimal(0.00);//九、其他费用合计

                    for (PageData pageData : monthListNew) {
                        String expenseAccount = pageData.getString("expenseaccount");
                        if (expenseAccount.equals("支出预算合计") &&
                                StringUtils.isNotBlank(pageData.getString("sourcebudget"))) {

                            sourceBudgetTotalMonth = new BigDecimal(pageData.getString("sourcebudget"));

                            expenseBudgetTotalMonth = new BigDecimal(pageData.getString("expensebudget"));
                        } else {
                            if (StringUtils.isNotBlank(pageData.getString("sourcebudget"))) {
                                sourceBudgetMonth = sourceBudgetMonth.add(new BigDecimal(pageData.getString("sourcebudget")));
                            }
                        }

                        if (!expenseAccount.equals("支出预算合计")) {

                            if (StringUtils.isNotBlank(pageData.getString("expensebudget")) &&
                                    !expenseAccount.equals("1、国际合作交流费") &&
                                    !expenseAccount.equals("2、出版/文献/信息传播") &&
                                    !expenseAccount.equals("3、知识产权事务") &&
                                    !expenseAccount.equals("4、专家费") &&
                                    !expenseAccount.equals("5、其他")) {
                                expenseBudgetMonth = expenseBudgetMonth.add(new BigDecimal(pageData.getString("expensebudget")));
                            }
                        }

                        if (StringUtils.isNotBlank(pageData.getString("expensebudget")) &&
                                (expenseAccount.equals("1、国际合作交流费") ||
                                        expenseAccount.equals("2、出版/文献/信息传播") ||
                                        expenseAccount.equals("3、知识产权事务") ||
                                        expenseAccount.equals("4、专家费") ||
                                        expenseAccount.equals("5、其他"))) {
                            otherBudgetTotalMonth = otherBudgetTotalMonth.add(new BigDecimal(pageData.getString("expensebudget")));
                        }

                        if (expenseAccount.equals("九、其他费用") &&
                                StringUtils.isNotBlank(pageData.getString("expensebudget"))) {
                            otherBudgetMonth = new BigDecimal(pageData.getString("expensebudget"));
                        }
                    }


                    if (sourceBudgetTotalMonth.compareTo(sourceBudgetMonth) != 0) {
                        throw new MyException("经费来源预算合计不正确(每月预算)");
                    }

                    if (expenseBudgetTotalMonth.compareTo(expenseBudgetMonth) != 0) {
                        throw new MyException("经费支出预算合计不正确(每月预算)");
                    }

                    if (sourceBudgetTotalMonth.compareTo(expenseBudgetTotalMonth) != 0) {
                        throw new MyException("经费来源预算合计与支出预算合计不相等(每月预算)");
                    }

                    if (sourceBudgetTotalMonth.compareTo(sourceBudgetTotal) != 0) {
                        throw new MyException("经费预算合计与支出预算(每月预算)合计不相等");
                    }

                    if (otherBudgetTotalMonth.compareTo(otherBudgetMonth) != 0) {
                        throw new MyException("其他费用合计与子项合计(每月预算)不相等");
                    }

                    List<PageData> monthDetailList = (List<PageData>) pd.get("monthDetailList");
                    TreeMap<String, List<PageData>> map = null;
                    if (!CollectionUtils.isEmpty(monthDetailList)) {
                        map = monthDetailList.stream().collect(Collectors.groupingBy(o -> o.getString("years"), TreeMap::new, Collectors.toList()));
                    }

                    //一列比较
                    for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {

                        for (int j = 0; j < 12; j++) {
                            BigDecimal januaryTotal = new BigDecimal(0);

                            BigDecimal otherBudgetTotalMonthColumn = new BigDecimal(0.00);//九、其他费用的小项合计
                            BigDecimal otherBudgetMonthColumn = new BigDecimal(0.00);//九、其他费用合计

                            BigDecimal january = new BigDecimal(0);

                            String keyParam = "month" + Integer.valueOf(entry.getKey()) + "" + (j + 1);
                            for (PageData data : monthList) {
                                String expenseAccount = data.getString("expenseaccount");
                                if (expenseAccount.equals("支出预算合计")) {
                                    if (StringUtils.isNotBlank(data.getString(keyParam))) {
                                        januaryTotal = new BigDecimal(data.getString(keyParam));
                                    }
                                }

                                if (!data.getString("expenseaccount").equals("支出预算合计") &&
                                        !expenseAccount.equals("1、国际合作交流费") &&
                                        !expenseAccount.equals("2、出版/文献/信息传播") &&
                                        !expenseAccount.equals("3、知识产权事务") &&
                                        !expenseAccount.equals("4、专家费") &&
                                        !expenseAccount.equals("5、其他")) {
                                    if (StringUtils.isNotBlank(data.getString(keyParam))) {
                                        january = january.add(new BigDecimal(data.getString(keyParam)));
                                    }
                                }

                                if (expenseAccount.equals("九、其他费用")) {
                                    otherBudgetMonthColumn = new BigDecimal(data.getString(keyParam));
                                }

                                if (StringUtils.isNotBlank(data.getString("expensebudget")) &&
                                        (expenseAccount.equals("1、国际合作交流费") ||
                                                expenseAccount.equals("2、出版/文献/信息传播") ||
                                                expenseAccount.equals("3、知识产权事务") ||
                                                expenseAccount.equals("4、专家费") ||
                                                expenseAccount.equals("5、其他"))) {
                                    otherBudgetTotalMonthColumn = otherBudgetTotalMonthColumn.add(new BigDecimal(data.getString(keyParam)));
                                }
                            }

                            if (januaryTotal.compareTo(january) != 0) {
                                throw new MyException((j + 1) + "月经费支出预算合计不相等(每月预算)");
                            }

                            if (otherBudgetTotalMonthColumn.compareTo(otherBudgetMonthColumn) != 0) {
                                throw new MyException((j + 1) + "月其它费用与子项合计不相等(每月预算)");
                            }
                        }
                    }

                    //一行比较
                    for (PageData data : monthList) {
                        BigDecimal januaryTotal = new BigDecimal(0);
                        String expenseAccount = data.getString("expenseaccount");

                        BigDecimal expenseBudgetMonthRow = new BigDecimal(data.getString("expensebudget"));

                        for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                            for (int j = 0; j < 12; j++) {
                                String keyParam = "month" + Integer.valueOf(entry.getKey()) + "" + (j + 1);
                                januaryTotal = januaryTotal.add(new BigDecimal(data.getString(keyParam)));
                            }
                        }

                        if (januaryTotal.compareTo(expenseBudgetMonthRow) != 0) {
                            throw new MyException(expenseAccount + "行的经费支出预算合计与每月合计不正确(每月预算)");
                        }
                    }

                    //查询规则配置的阈值
                    //2:材料费,3:机械设备使用费,4:人工费,5:其他费用
                    List<PageData> ruleList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryRuleList", pd);
                    BigDecimal materialRate = null;//材料
                    BigDecimal equipmentRate = null;//设备
                    BigDecimal artificialRate = null;//人工
                    BigDecimal otherRate = null;//其他费用

                    if (!CollectionUtils.isEmpty(ruleList)) {
                        for (PageData data : ruleList) {
                            String ruleType = data.getString("ruleType");
                            String ruleValue = data.getString("ruleValue");
                            if (StringUtils.isBlank(ruleValue)) {
                                continue;
                            }
                            if (ruleType.equals("6")) {
                                otherRate = new BigDecimal(ruleValue);
                            } else if (ruleType.equals("3")) {
                                materialRate = new BigDecimal(ruleValue);
                            } else if (ruleType.equals("4")) {
                                equipmentRate = new BigDecimal(ruleValue);
                            } else if (ruleType.equals("5")) {
                                artificialRate = new BigDecimal(ruleValue);
                            }
                        }
                    }


                    try {
                        //1、材料费不得超过总预算的75%

                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            return buffer.toString();

                        }

                        if (materialRate != null && material.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(materialRate) == 1) {
                            buffer.append("材料费、");
                        }

                        //2、机械使用费不得低于总预算的15%
                        if (equipmentRate != null && equipment.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(equipmentRate) == -1) {
                            buffer.append("机械设备费、");
                        }

                        //3、人工费不得低于总预算的10%
                        if (artificialRate != null && artificial.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(artificialRate) == -1) {
                            buffer.append("人工费、");
                        }

                        //4、其他费用不得高于总预算的10%
                        if (otherRate != null && other.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(otherRate) == 1) {
                            buffer.append("其他费用、");
                        }
                    } catch (Exception e) {
                        throw new MyException("提交失败:" + e.getMessage());
                    }
                }
            } else {
                //新建或编辑提交
                //判断研发费用预算是否超值
                if(!CollectionUtils.isEmpty(budgetList)){
                    BigDecimal total = new BigDecimal(0);//总费用
                    BigDecimal material = new BigDecimal(0);//材料
                    BigDecimal equipment = new BigDecimal(0);//设备
                    BigDecimal artificial = new BigDecimal(0);//人工
                    BigDecimal other = new BigDecimal(0);//其他费用

                    BigDecimal sourceBudgetTotal = new BigDecimal(0.00);//来源预算之和
                    BigDecimal expenseBudgetTotal = new BigDecimal(0.00);//支出预算之和
                    BigDecimal sourceBudget = new BigDecimal(0.00);//来源预算合计

                    BigDecimal otherBudgetTotal = new BigDecimal(0.00);//九、其他费用的小项合计
                    BigDecimal otherBudget = new BigDecimal(0.00);//九、其他费用合计

                    String costStr = "五、测试及化验费|六、差旅费|七、会议费|八、课题管理费|九、其他费用|1、国际合作交流费|2、出版/文献/信息传播|3、知识产权事务|4、专家费|5、其他";
                    for(PageData data : budgetList){
                        String expenseAccount = data.getString("expenseAccount");
                        String expenseBudget = data.getString("expenseBudget");
                        if(expenseAccount.equals("支出预算合计")){
                            total = new BigDecimal(expenseBudget);
                            sourceBudget = new BigDecimal(data.getString("sourceBudget"));
                        }else if(expenseAccount.equals("三、材料费")){
                            material = new BigDecimal(expenseBudget);
                        }else if(expenseAccount.equals("二、设备费")){
                            equipment = new BigDecimal(expenseBudget);
                        }else if(expenseAccount.equals("一、人员费")){
                            artificial = new BigDecimal(expenseBudget);
                        }else if(costStr.contains(expenseAccount)){
                            other = other.add(new BigDecimal(expenseBudget));
                        }

                        if(!expenseAccount.equals("支出预算合计")){

                            if (StringUtils.isNotBlank(data.getString("sourceBudget"))){
                                sourceBudgetTotal = sourceBudgetTotal.add(new BigDecimal(data.getString("sourceBudget")));
                            }

                            if (StringUtils.isNotBlank(data.getString("expenseBudget")) &&
                                    !expenseAccount.equals("1、国际合作交流费") &&
                                    !expenseAccount.equals("2、出版/文献/信息传播") &&
                                    !expenseAccount.equals("3、知识产权事务") &&
                                    !expenseAccount.equals("4、专家费") &&
                                    !expenseAccount.equals("5、其他")){
                                expenseBudgetTotal = expenseBudgetTotal.add(new BigDecimal(data.getString("expenseBudget")));
                            }
                        }

                        if (StringUtils.isNotBlank(data.getString("expenseBudget")) &&
                                (expenseAccount.equals("1、国际合作交流费") ||
                                expenseAccount.equals("2、出版/文献/信息传播") ||
                                expenseAccount.equals("3、知识产权事务") ||
                                expenseAccount.equals("4、专家费") ||
                                expenseAccount.equals("5、其他"))){
                            otherBudgetTotal = otherBudgetTotal.add(new BigDecimal(data.getString("expenseBudget")));
                        }

                        if(expenseAccount.equals("九、其他费用")){
                            otherBudget = new BigDecimal(expenseBudget);
                        }
                    }


                    if (sourceBudgetTotal.compareTo(sourceBudget) != 0){
                        throw new MyException("经费来源预算合计不正确");
                    }

                    if (expenseBudgetTotal.compareTo(total) != 0){
                        throw new MyException("经费支出预算合计不正确");
                    }

                    if (otherBudgetTotal.compareTo(otherBudget) != 0){
                        throw new MyException("其他费用合计与子项合计不正确");
                    }

                    if (sourceBudgetTotal.compareTo(expenseBudgetTotal) != 0){
                        throw new MyException("经费来源预算合计与支出预算合计不相等");
                    }

                    BigDecimal sourceBudgetTotalMonth = new BigDecimal(0.00);//来源预算之和
                    BigDecimal expenseBudgetTotalMonth  = new BigDecimal(0.00);//支出预算之和
                    BigDecimal expenseBudgetMonth = new BigDecimal(0);
                    BigDecimal sourceBudgetMonth = new BigDecimal(0.00);
                    BigDecimal otherBudgetTotalMonth = new BigDecimal(0.00);//九、其他费用的小项合计
                    BigDecimal otherBudgetMonth = new BigDecimal(0.00);//九、其他费用合计

                    for(PageData data : monthList){

                        BigDecimal januaryTotal = new BigDecimal(0);

                        String expenseAccount = data.getString("expenseaccount");

                        if(expenseAccount.equals("支出预算合计") &&
                                StringUtils.isNotBlank(data.getString("sourcebudget"))){
                            sourceBudgetTotalMonth = new BigDecimal(data.getString("sourcebudget"));

                            expenseBudgetTotalMonth = new BigDecimal(data.getString("expensebudget"));
                        }

                        if(!expenseAccount.equals("支出预算合计")){

                            if (StringUtils.isNotBlank(data.getString("sourcebudget"))){
                                sourceBudgetMonth = sourceBudgetMonth.add(new BigDecimal(data.getString("sourcebudget")));
                            }
                            if (StringUtils.isNotBlank(data.getString("expensebudget")) &&
                                    !expenseAccount.equals("1、国际合作交流费") &&
                                    !expenseAccount.equals("2、出版/文献/信息传播") &&
                                    !expenseAccount.equals("3、知识产权事务") &&
                                    !expenseAccount.equals("4、专家费") &&
                                    !expenseAccount.equals("5、其他")){
                                expenseBudgetMonth = expenseBudgetMonth.add(new BigDecimal(data.getString("expensebudget")));
                            }
                        }

                        if (StringUtils.isNotBlank(data.getString("expensebudget")) &&
                                (expenseAccount.equals("1、国际合作交流费") ||
                                expenseAccount.equals("2、出版/文献/信息传播") ||
                                expenseAccount.equals("3、知识产权事务") ||
                                expenseAccount.equals("4、专家费") ||
                                expenseAccount.equals("5、其他"))){
                            otherBudgetTotalMonth = otherBudgetTotalMonth.add(new BigDecimal(data.getString("expensebudget")));
                        }

                        if(expenseAccount.equals("九、其他费用") && StringUtils.isNotBlank(data.getString("expensebudget"))){
                            otherBudgetMonth = new BigDecimal(data.getString("expensebudget"));
                        }


                    }

                    if (sourceBudgetTotalMonth.compareTo(sourceBudgetMonth) != 0){
                        throw new MyException("经费来源预算合计不正确(每月预算)");
                    }

                    if (expenseBudgetTotalMonth.compareTo(expenseBudgetMonth) != 0){
                        throw new MyException("经费支出预算合计不正确(每月预算)");
                    }

                    if (sourceBudgetTotalMonth.compareTo(expenseBudgetTotalMonth) != 0){
                        throw new MyException("经费来源预算合计与支出预算合计不相等(每月预算)");
                    }

                    if (sourceBudgetTotalMonth.compareTo(sourceBudgetTotal) != 0){
                        throw new MyException("经费预算合计与支出预算(每月预算)合计不相等");
                    }

                    if (otherBudgetTotalMonth.compareTo(otherBudgetMonth) != 0){
                        throw new MyException("其他费用合计与子项合计(每月预算)不相等");
                    }

                    //遍历出年份
                    Set<String> yearSet = new TreeSet<>(new Comparator<String>(){
                        @Override
                        public int compare(String s1,String s2){
                            return s1.compareTo(s2);
                        }
                    });

                    if (!CollectionUtils.isEmpty(monthList)) {
                        for (PageData detailData : monthList) {
                            for (Object key : detailData.keySet()) {
                                String keyStr = (String)key;

                                if(keyStr.length() > 8 && StringUtils.isNumeric(keyStr.substring(5,9))){
                                    yearSet.add(keyStr.substring(5,9));
                                }

                            }
                        }
                    }

                    //一列比较
                    for(PageData data : monthList){

                        BigDecimal januaryTotal = new BigDecimal(0);


                        BigDecimal otherBudgetMonthColumn = new BigDecimal(0.00);//九、其他费用合计

                        String expenseAccount = data.getString("expenseaccount");

                        if(expenseAccount.equals("支出预算合计")){

                            if (!CollectionUtils.isEmpty(yearSet)){
                                for(String yset : yearSet) {
                                    for (int j = 0; j < 12; j++) {
                                        String keyParam = "month"+yset+""+(j+1);

                                        januaryTotal = new BigDecimal(data.getString(keyParam));

                                        BigDecimal january = new BigDecimal(0);

                                        BigDecimal otherBudgetTotalMonthColumn = new BigDecimal(0.00);//九、其他费用的小项合计

                                        for(PageData data1 : monthList){
                                            String expenseAccount1 = data1.getString("expenseaccount");
                                            if(!expenseAccount1.equals("支出预算合计") &&
                                                    !expenseAccount1.equals("1、国际合作交流费") &&
                                                    !expenseAccount1.equals("2、出版/文献/信息传播") &&
                                                    !expenseAccount1.equals("3、知识产权事务") &&
                                                    !expenseAccount1.equals("4、专家费") &&
                                                    !expenseAccount1.equals("5、其他")){
                                                january = january.add(new BigDecimal(data1.getString(keyParam)));
                                            }

                                            if (StringUtils.isNotBlank(data1.getString("expensebudget")) &&
                                                    (expenseAccount1.equals("1、国际合作交流费") ||
                                                            expenseAccount1.equals("2、出版/文献/信息传播") ||
                                                            expenseAccount1.equals("3、知识产权事务") ||
                                                            expenseAccount1.equals("4、专家费") ||
                                                            expenseAccount1.equals("5、其他"))){
                                                otherBudgetTotalMonthColumn = otherBudgetTotalMonthColumn.add(new BigDecimal(data1.getString(keyParam)));
                                            }

                                            if(expenseAccount1.equals("九、其他费用")){
                                                otherBudgetMonthColumn = new BigDecimal(data1.getString(keyParam));
                                            }
                                        }

                                        if (januaryTotal.compareTo(january) != 0){
                                            throw new MyException((j+1)+"月经费支出预算合计不正确(每月预算)");
                                        }

                                        if (otherBudgetTotalMonthColumn.compareTo(otherBudgetMonthColumn) != 0){
                                            throw new MyException((j+1)+"月其它费用与子项合计不正确(每月预算)");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //一行比较
                    for(PageData data : monthList){
                        BigDecimal januaryTotal = new BigDecimal(0);
                        String expenseAccount = data.getString("expenseaccount");

                        BigDecimal expenseBudgetMonthRow = new BigDecimal(data.getString("expensebudget"));

                        for(String yset : yearSet) {
                            for (int j = 0; j < 12; j++) {
                                String keyParam = "month"+yset+""+(j+1);
                                januaryTotal = januaryTotal.add(new BigDecimal(data.getString(keyParam)));
                            }
                        }

                        if (januaryTotal.compareTo(expenseBudgetMonthRow) != 0){
                            throw new MyException(expenseAccount+"行的经费支出预算合计与每月合计不正确(每月预算)");
                        }
                    }


                    //查询规则配置的阈值
                    //2:材料费,3:机械设备使用费,4:人工费,5:其他费用
                    List<PageData> ruleList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryRuleList",pd);
                    BigDecimal materialRate = null;//材料
                    BigDecimal equipmentRate = null;//设备
                    BigDecimal artificialRate= null;//人工
                    BigDecimal otherRate = null;//其他费用

                    if(!CollectionUtils.isEmpty(ruleList)){
                        for(PageData data : ruleList){
                            String ruleType = data.getString("ruleType");
                            String ruleValue = data.getString("ruleValue");
                            if(StringUtils.isBlank(ruleValue)){
                                continue;
                            }
                            if(ruleType.equals("6")){
                                otherRate = new BigDecimal(ruleValue);
                            }else if(ruleType.equals("3")){
                                materialRate = new BigDecimal(ruleValue);
                            }else if(ruleType.equals("4")){
                                equipmentRate = new BigDecimal(ruleValue);
                            }else if(ruleType.equals("5")){
                                artificialRate = new BigDecimal(ruleValue);
                            }
                        }
                    }


                    try {
                        //1、材料费不得超过总预算的75%

                        if(total.compareTo(BigDecimal.ZERO) == 0){
                            return buffer.toString();

                        }

                        if(materialRate != null && material.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(materialRate) == 1){
                            buffer.append("材料费、");
                        }

                        //2、机械使用费不得低于总预算的15%
                        if(equipmentRate != null &&  equipment.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(equipmentRate) == -1){
                            buffer.append("机械设备费、");
                        }

                        //3、人工费不得低于总预算的10%
                        if(artificialRate != null &&artificial.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(artificialRate) == -1){
                            buffer.append("人工费、");
                        }

                        //4、其他费用不得高于总预算的10%
                        if(otherRate != null &&other.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(otherRate) == 1){
                            buffer.append("其他费用、");
                        }
                    } catch (Exception e) {
                        throw new MyException("提交失败:"+e.getMessage());
                    }

                }
            }

        }else {//局级拨款
            //判断研发费用预算是否超值
            if(!CollectionUtils.isEmpty(budgetList)){
                BigDecimal total = new BigDecimal(0);//总费用
                BigDecimal ry = new BigDecimal(0);//人员费用
                BigDecimal yjry = new BigDecimal(0);//研究人员费
                BigDecimal sb = new BigDecimal(0);//设备费
                BigDecimal cl = new BigDecimal(0);//材料费
                BigDecimal xgyw = new BigDecimal(0);//相关业务费
                BigDecimal chl = new BigDecimal(0);//差旅费
                BigDecimal xmgl = new BigDecimal(0);//项目管理费
                BigDecimal qt = new BigDecimal(0);//其他费用
                BigDecimal wwf = new BigDecimal(0);//七、委外费
                BigDecimal lwf = new BigDecimal(0);//1.2劳务费
                BigDecimal zs = new BigDecimal(0);//五、知识产权事务费
                BigDecimal rj = new BigDecimal(0);//六、软件测试费

                for(PageData data : budgetList){
                    String expenseAccount = data.getString("expenseAccount");
                    String expenseBudget = data.getString("expenseBudget");
                    if(expenseAccount.equals("支出预算合计")){
                        total = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("一、人员费")){
                        ry = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("1.1、研究人员费")){
                        yjry = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("二、设备费")){
                        sb = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("3.1、材料费")){
                        cl = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("四、相关业务费")){
                        xgyw = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("4.1、差旅费")){
                        chl = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("八、项目管理费")){
                        xmgl = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("九、其他费用")){
                        qt = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("七、委外研究开发费")){
                        wwf = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("1.2、劳务费")){
                        lwf = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("五、知识产权事务费")){
                        zs = new BigDecimal(expenseBudget);
                    }else if(expenseAccount.equals("六、软件测试费")){
                        rj = new BigDecimal(expenseBudget);
                    }
                }

                //查询规则配置的阈值
                //2:材料费,3:机械设备使用费,4:人工费,5:其他费用
                List<PageData> ruleList = (List<PageData>) dao.findForList("ProjectApplyMapper.queryRuleList",pd);
                BigDecimal ryRate = null;//人员费用
                BigDecimal yjryRate = null;//研究人员费
                BigDecimal sbRate = null;//设备费
                BigDecimal clRate = null;//材料费
                BigDecimal xgywRate = null;//相关业务费
                BigDecimal chlRate = null;//差旅费
                BigDecimal dxmglRate = null;//项目管理费低于
                BigDecimal cxmglRate = null;//项目管理费超过
                BigDecimal qtRate = null;//其他费用

                if(!CollectionUtils.isEmpty(ruleList)){
                    for(PageData data : ruleList){
                        String ruleType = data.getString("ruleType");
                        String ruleValue = data.getString("ruleValue");
                        if(StringUtils.isBlank(ruleValue)){
                            continue;
                        }
                        if(ruleType.equals("7")){
                            ryRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("8")){
                            yjryRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("9")){
                            sbRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("10")){
                            clRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("11")){
                            xgywRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("12")){
                            chlRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("13")){
                            dxmglRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("14")){
                            cxmglRate = new BigDecimal(ruleValue);
                        }else if(ruleType.equals("15")){
                            qtRate = new BigDecimal(ruleValue);
                        }
                    }
                }


                try {
                    if(total.compareTo(BigDecimal.ZERO) == 0){
                        return buffer.toString();

                    }

                    //1、人员费用不得低于10%  人员费/总和*100%
                    if(ryRate != null &&  ry.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(ryRate) == -1){
                        buffer.append("人员费用、");
                    }

                    BigDecimal total1 = total.subtract(wwf).subtract(sb).subtract(lwf);
                    //2、研究人员费用不得超过35%  研究人员费/(总和-七、委外费-二、设备费-1.2劳务费)*100%
                    if(total1.compareTo(BigDecimal.ZERO) != 0 && yjryRate != null && yjry.divide(total1, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(yjryRate) == 1){
                        buffer.append("研究人员费用、");
                    }

                    //3、设备费不得低于15%   设备费/总和*100%
                    if(sbRate != null &&  sb.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(sbRate) == -1){
                        buffer.append("设备费、");
                    }

                    //4、材料费不得超过75%   材料费/总和*100%
                    if( clRate != null && cl.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(clRate) == 1){
                        buffer.append("材料费、");
                    }

                    //5、相关业务费不得超过10%   相关业务费/总和*100%   （相关业务费=四+五+六+八+九）
                    BigDecimal total2 = xgyw.add(zs).add(rj).add(xmgl).add(qt);
                    if(total.compareTo(BigDecimal.ZERO) != 0 && xgywRate != null && total2.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(xgywRate) == 1){
                        buffer.append("相关业务费、");
                    }

                    //6、差旅费不得超过50%    差旅费/相关业务费*100%
                    if( xgyw.compareTo(BigDecimal.ZERO) != 0 && chlRate != null && chl.divide(xgyw, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(chlRate) == 1){
                        buffer.append("差旅费、");
                    }

                    //7、项目管理费不得低于5%，不得超过10%   项目管理费/总和*100%
                    if( dxmglRate != null && xmgl.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(dxmglRate) == -1){
                        buffer.append("项目管理费、");
                    }
                    if( cxmglRate != null && xmgl.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(cxmglRate) == 1){
                        buffer.append("项目管理费、");
                    }


                    //8、其他费用不得超过3%     其他费用/总和*100%
                    if( qtRate != null && qt.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).compareTo(qtRate) == 1){
                        buffer.append("其他费用、");
                    }


                } catch (Exception e) {
                    throw new MyException("提交失败:"+e.getMessage());
                }

            }


        }



        if(StringUtils.isNotBlank(buffer.toString())){
            buffer.append("不符合公司规定的预算比例，请确认调整后重新提交");
        }

        String bufferStr = buffer.toString();
        return bufferStr.replace("、不","不");

    }


    public static PageData checkParams(ProjectApplyUploadFileDto dto) {

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

        MultipartFile file = dto.getFile();
        CheckParameter.isNull(file, "上传文件");

        String fileName = file.getOriginalFilename();
        //是否为局级 0：否1：是,判断传的模板是否正确
        String bureauLevel = dto.getBureauLevel();
        if(StringUtils.isBlank(bureauLevel)){
            if(fileName.contains("立项申请导入模板(局级拨款)")){
                bureauLevel = "1";

            }else if(fileName.contains("立项申请导入模板(其他拨款)")){
                bureauLevel = "0";

            }else {
                throw new MyException("请上传正确的立项申请模板");
            }

        }

        if(bureauLevel.equals("1") && !fileName.contains("立项申请导入模板(局级拨款)")){
            throw new MyException("请上传正确的局部拨款立项申请模板");

        }else if(bureauLevel.equals("0") && !fileName.contains("立项申请导入模板(其他拨款)")){
            throw new MyException("请上传正确的其他拨款立项申请模板");

        }

        pageData.put("bureauLevel",bureauLevel);
        pageData.put("creatorOrgId",creatorOrgId);
        pageData.put("creatorOrgName",creatorOrgName);
        pageData.put("createUserId",createUserId);
        pageData.put("createUser",createUser);
        pageData.put("menuCode",menuCode);

        return pageData;

    }


}
