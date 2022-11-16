package com.rdexpense.manager.controller.materialRequisition;


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
import com.rdexpense.manager.dto.materialRequisition.*;
import com.rdexpense.manager.service.materialRequisition.MaterialRequisitionService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.*;
import static com.common.util.DateCheckUtil.checkOrder;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:27
 */
@RestController
@RequestMapping("/materialRequisition")
@Api(value = "项目领料单管理", tags = "项目领料单管理")
public class MaterialRequisitionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MaterialRequisitionController.class);

    @Autowired
    private MaterialRequisitionService materialRequisitionService;

    @Autowired
    private BaseDao dao;

    @Autowired
    private LogUtil logUtil;

    private static final String FILE_PREFIX = "研发项目领料单";


    @PostMapping("/queryList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<MaterialRequisitionListDto>> queryList(MaterialRequisitionSearchDto materialRequisitionSearchDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = materialRequisitionService.queryList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, MaterialRequisitionListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询研发项目领料单列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "新增领料单")
    @PostMapping("/add")
    public ResponseEntity addMaterialRequisition(MaterialRequisitionAddDto materialRequisitionAddDto) {
        PageData pd = this.getParams();
        checkParam(pd);
        String check = checkDate(pd);
        if(StringUtils.isNotBlank(check)){
            return ResponseEntity.success(check, check);
        }

        ResponseEntity result = null;
        try {

            materialRequisitionService.addMaterialRequisition(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增研发项目领料单失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, FILE_PREFIX, pd);
        }
    }

    @ApiOperation(value = "编辑领料单")
    @PostMapping("/update")
    public ResponseEntity updateMaterialRequisition(MaterialRequisitionAddDto materialRequisitionAddDto) {

        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 256);

        // 编辑时，状态只能为已保存
        PageData recordData = (PageData) dao.findForObject("MaterialRequisitionMapper.queryDetail", pd);
        String requestStatus = recordData.getString("processStatus");
        if (!requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0]) && !requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[3])) {
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc());
        }

        checkParam(pd);
        String check = checkDate(pd);
        if(StringUtils.isNotBlank(check)){
            return ResponseEntity.success(check, check);
        }

        ResponseEntity result = null;
        try {

            materialRequisitionService.updateMaterialRequisition(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("修改研发项目领料单失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, FILE_PREFIX, pd);
        }
    }



    @ApiOperation(value = "删除领料单")
    @PostMapping("/delete")
    public ResponseEntity deleteMaterialRequisition(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkBusinessIdList(pd);

        ResponseEntity result = null;
        try {
            materialRequisitionService.deleteMaterialRequisition(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除研发项目领料单失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, FILE_PREFIX, pd);
        }


    }




    @ApiOperation(value = "查询详情")
    @PostMapping("/queryDetail")
    @ApiImplicitParam(name = "businessId", value = "业务主键ID", required = true, dataType = "String")
    public ResponseEntity<MaterialRequisitionDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 128);
        try {
            PageData pageData = materialRequisitionService.queryDetail(pd);
            return PropertyUtil.pushData(pageData, MaterialRequisitionDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询研发项目领料单详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "导入模板")
    @PostMapping(value = "/upload")
    public ResponseEntity<MaterialRequisitionUploadDetailDto> upload(MaterialRequisitionUploadFileDto dto) {
        PageData pd = checkParams(dto);
        try {
            PageData data = materialRequisitionService.upload(dto.getFile(),pd);
            return PropertyUtil.pushData(data, MaterialRequisitionUploadDetailDto.class, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("导入领导单明细失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "支出申请单")
    @PostMapping(value = "/generateApply")
    @ApiImplicitParam(name = "businessId", value = "业务主键ID", required = true, dataType = "String")
    public ResponseEntity<MaterialRequisitionApplyDetailDto> generateApply() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 128);
        try {
            PageData data = materialRequisitionService.generateApply(pd);
            return PropertyUtil.pushData(data, MaterialRequisitionApplyDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("支出申请单失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "导出excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        CheckParameter.checkBusinessIdList(pd);

        HttpServletResponse response = this.getResponse();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String number = SerialNumberUtil.generateSerialNo("materialRequisitionExcel");

        try {


            HSSFWorkbook wb = materialRequisitionService.exportExcel(pd);
            String fileName = FILE_PREFIX + "_" + date + "_" + number + ".xls";
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();

            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, FILE_PREFIX, pd);
        }
    }


    @ApiOperation(value = "导出PDF")
    @PostMapping(value = "/exportPdf")
    public ResponseEntity exportPDF(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getPageDataJson();
        ResponseEntity result = null;
        CheckParameter.checkBusinessIdList(pd);
        try {
            HttpServletResponse response = this.getResponse();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String serialNumber = SerialNumberUtil.generateSerialNo("materialRequisitionPdf");
            String fileName = FILE_PREFIX + df.format(new Date()) + "_"+serialNumber +".pdf";
            //输出流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String businessIdStr = pd.getString("businessIdList");
            List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);
            // 打开文档，开始填写内容
            Document document = new Document(new RectangleReadOnly(842, 595));
            PdfWriter.getInstance(document, bos);
            document.open();
            if (businessIdList.size() > 1) {
                // 文件名
                for (int i = 0; i < businessIdList.size(); i++) {
                    if (i > 0) {//第一页往后，下一条记录新起一页
                        document.newPage();
                    }
                    PageData data = new PageData();
                    data.put("businessId", businessIdList.get(i));
                    //生成单个pdf
                    materialRequisitionService.exportPDF(data, document);
                }
            } else {
                if (businessIdList.size() > 0) {
                    PageData data = new PageData();
                    data.put("businessId", businessIdList.get(0));
                    materialRequisitionService.exportPDF(data, document);
                } else {
                    throw new MyException(ConstantMsgUtil.WAN_EXPORT.desc());
                }
            }
            document.close();

            //写入返回response
            response.reset();
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8"));
            response.setContentType("application/octet-stream; charset=utf-8");
            OutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(bos.toByteArray());
            out.flush();
            out.close();
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "用户管理PDF", pd);
        }
    }


    @ApiOperation(value = "导出PDF")
    @PostMapping(value = "/exportPdf1")
    public ResponseEntity exportPDF1(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkBusinessIdList(pd);
        ResponseEntity result = null;
        HttpServletResponse response = this.getResponse();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String businessIdStr = pd.getString("businessIdList");
        List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String number = SerialNumberUtil.generateSerialNo("materialRequisitionPdf");
        try {

            if(businessIdList.size() == 1){
                // 打开文档，开始填写内容
                Document document = new Document(new RectangleReadOnly(842, 595));
                PdfWriter.getInstance(document, bos);
                document.open();
                PageData data = new PageData();
                data.put("businessId",businessIdList.get(0));
                materialRequisitionService.exportPDF(data, document);

                document.close();
                response.reset();
                response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(FILE_PREFIX+date+"_"+number+".pdf", "utf-8"));
                response.setContentType("application/octet-stream; charset=utf-8");
                OutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(bos.toByteArray());
                out.flush();
                out.close();

            }else {
                //文件名
                String fileName = FILE_PREFIX+date+"_"+number+".zip";
                // 压缩流
                ZipOutputStream zos = new ZipOutputStream(bos);

                materialRequisitionService.exportZip(businessIdList, zos,FILE_PREFIX);
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



            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "其他合同费用校核导出PDF", pd);
        }

    }


        public static PageData checkParams(MaterialRequisitionUploadFileDto dto) {

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



        String  projectName = dto.getProjectName();
        CheckParameter.stringLengthAndEmpty(projectName, "项目名称",256);

        String  projectId = dto.getProjectId();
        CheckParameter.stringLengthAndEmpty(projectId, "项目ID",256);

        String  belongMonth = dto.getBelongMonth();
        CheckParameter.stringLengthAndEmpty(belongMonth, "belongMonth",256);

        pageData.put("belongMonth",belongMonth);
        pageData.put("projectName",projectName);
        pageData.put("projectId",projectId);
        pageData.put("creatorOrgId",creatorOrgId);
        pageData.put("creatorOrgName",creatorOrgName);
        pageData.put("createUserId",createUserId);
        pageData.put("createUser",createUser);
        pageData.put("menuCode",menuCode);

        return pageData;

    }

    private void checkParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);

        CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserId"), "编制人ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserName"), "编制人", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("createdDate"), "编制日期", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectId"), "项目ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectId"), "项目编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectLeader"), "项目负责人", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("belongMonth"), "所属月份", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("applyDate"), "申请日期", 256);
//        CheckParameter.stringLengthAndEmpty(pd.getString("billProject"), "领料单项目", 256);
//        CheckParameter.stringLengthAndEmpty(pd.getString("billMonth"), "领料单月份", 256);
        //明细
        List<PageData> detailList = JSONObject.parseArray(pd.getString("detailList"), PageData.class);


        if(!CollectionUtils.isEmpty(detailList)){
            for(PageData appropriationPlan : detailList){
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("materialName"), "材料名称", 256);
                CheckParameter.checkDecimal(appropriationPlan.getString("number"), "数量",20,2);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("specifications"), "规格", 256);
                CheckParameter.stringLengthAndEmpty(appropriationPlan.getString("unit"), "计量单位", 256);
                CheckParameter.checkDecimal(appropriationPlan.getString("noTaxPrice"), "不含税单价",20,2);
                CheckParameter.checkDecimal(appropriationPlan.getString("noTaxAmount"), "不含税金额",20,2);
            }
        }else{
            throw new MyException("领料单明细不能为空");

        }


    }



    private String checkDate(PageData pd){
        String belongMonth = pd.getString("belongMonth")+"-01";

        //根据项目ID查询项目的周期
        PageData project = (PageData) dao.findForObject("MaterialRequisitionMapper.queryProject",pd);
        if(project != null){
            String startYear = project.getString("startYear");
            String endYear = project.getString("endYear");

            if(StringUtils.isNotBlank(startYear)){
                String date1 = checkOrder(startYear,belongMonth);
                if(date1.equals("than")){
                    throw new MyException("该发料单所属月份不在当前项目研发周期内，请确认调整后重新操作");
                }
            }


            if(StringUtils.isNotBlank(endYear)){
                String date2 = checkOrder(endYear,belongMonth);
                if(date2.equals("less")){
                    throw new MyException("该发料单所属月份不在当前项目研发周期内，请确认调整后重新操作");
                }
            }


        }else {
            throw new MyException("该项目不存在");
        }

        if(StringUtils.isNotBlank(pd.getString("billMonth")) && !pd.getString("billMonth").equals(pd.getString("belongMonth"))){
            throw new MyException("模板中的所属月份与页面选择所属月份不一致，请确认后重新上传");
        }

        if(StringUtils.isNotBlank(pd.getString("billProject")) && !pd.getString("billProject").equals(pd.getString("projectName"))){
            throw new MyException("模板中的项目名称与页面选择项目名称不一致，请确认后重新上传");
        }


        StringBuffer buffer = new StringBuffer();
        String confirmSubmit = pd.getString("confirmSubmit");
        if(StringUtils.isNotBlank(confirmSubmit) && confirmSubmit.equals("1")){
            return buffer.toString();
        }

        //检验总计金额
        PageData ruleData = (PageData) dao.findForObject("MaterialRequisitionMapper.queryRuleData");
        if(ruleData != null){
            BigDecimal ruleNumber = new BigDecimal(0);
            if(StringUtils.isNotBlank(ruleData.getString("ruleValue"))){
                ruleNumber = new BigDecimal(ruleData.getString("ruleValue"));
            }

            BigDecimal number = new BigDecimal(0);

            String detailListStr = pd.getString("detailList");
            List<PageData> detailList = JSONObject.parseArray(detailListStr, PageData.class);
            if (!CollectionUtils.isEmpty(detailList)) {
                for (PageData detailData : detailList) {
                    if (StringUtils.isNotBlank(detailData.getString("noTaxAmount"))) {
                        number = number.add(new BigDecimal(detailData.getString("noTaxAmount")));
                    }

                }
            }
            number = number.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_UP);

            if( number.compareTo(ruleNumber) == 1){
                buffer.append("该发料单总金额已经超出单次最高限额").append(ruleNumber).append("万元");
            }


        }

        return buffer.toString();


    }


}
