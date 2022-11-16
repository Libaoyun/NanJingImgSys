package com.rdexpense.manager.controller.itemClosureCheck;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.common.util.SerialNumberUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.text.Document;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfWriter;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.equipmentDepreciationExpenses.DepreciationExpensesDetailDto;
import com.rdexpense.manager.dto.equipmentDepreciationExpenses.DepreciationExpensesUploadFileDto;
import com.rdexpense.manager.dto.equipmentDepreciationExpenses.EquipmentDepreciationExpensesDetailDto;
import com.rdexpense.manager.dto.equipmentDepreciationExpenses.EquipmentDepreciationExpensesListDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.EquipmentLeaseSettlementDetailDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.EquipmentLeaseSettlementListDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.EquipmentLeaseSettlementUploadFileDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.SettlementDetailDto;
import com.rdexpense.manager.service.itemClosureCheck.EquipmentDepreciationExpensesService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

@RestController
@RequestMapping("/equipmentDepreciationExpenses")
@Api(value = "设备折旧支出单管理", tags = "设备折旧支出单管理")
public class EquipmentDepreciationExpensesController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentDepreciationExpensesController.class);

    @Autowired
    private EquipmentDepreciationExpensesService equipmentDepreciationExpensesService;

    @Autowired
    private BaseDao dao;

    @Autowired
    private LogUtil logUtil;

    @PostMapping("/queryList")
    @ApiOperation(value = "查询设备折旧支出单管理列表", notes = "查询设备折旧支出单管理列表")
    public ResponseEntity<PageInfo<EquipmentDepreciationExpensesListDto>> queryList(EquipmentDepreciationExpensesListDto equipmentDepreciationExpensesListDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = equipmentDepreciationExpensesService.queryList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, EquipmentDepreciationExpensesListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询设备折旧支出单管理列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "删除设备折旧支出单管理申请")
    @PostMapping("/delete")
    public ResponseEntity deleteApply(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String businessIdList = pd.getString("businessIdList");
        if (StringUtils.isBlank(businessIdList)) {
            throw new MyException("业务主键businessId不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            equipmentDepreciationExpensesService.deleteEquipmentDepreciationExpenses(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除设备折旧支出单管理失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "设备折旧支出单管理申请", pd);
        }
    }

    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询设备折旧支出单管理详情", notes = "查询设备折旧支出单管理详情")
    @ApiImplicitParam(name = "businessId", value = "业务主键businessId", required = true, dataType = "String")
    public ResponseEntity<EquipmentDepreciationExpensesDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键businessId",64);
        try {

            PageData pageData = equipmentDepreciationExpensesService.getDepreciationExpensesDetail(pd);

            return PropertyUtil.pushData(pageData, EquipmentDepreciationExpensesDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询设备折旧支出单管理详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @ApiOperation(value = "新增或编辑")
    @PostMapping("/saveOrUpdate")
    public ResponseEntity saveOrUpdate(EquipmentDepreciationExpensesDetailDto equipmentDepreciationExpensesDetailDto) {
        PageData pd = this.getParams();
        saveCheckParam(pd);
        ResponseEntity result = null;
        try {

            equipmentDepreciationExpensesService.saveOrUpdate(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增或编辑设备折旧支出单管理失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "设备折旧支出单管理", pd);
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
        String idList = pd.getString("businessIdList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        try {
            HSSFWorkbook wb = equipmentDepreciationExpensesService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "设备折旧支出单_" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            logUtil.saveLogData(result.getCode(), 6, "设备折旧支出单EXCEL", pd);
        }
    }

    @ApiOperation(value = "导出PDF", notes = "导出PDF")
    @PostMapping(value = "/exportPdf")
    public ResponseEntity exportPdf(BusinessIdListDto businessIdListDto) {
        PageData pageData = this.getParams();

        //取出菜单id
        CheckParameter.checkDefaultParams(pageData);
        //校验取出参数
        String idList = pageData.getString("businessIdList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        ResponseEntity result = null;
        try {
            HttpServletResponse response = this.getResponse();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            String fileName =  "设备折旧支出单_" + date + ".pdf";

            //输出流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String idStr = pageData.getString("businessIdList");
            List<PageData> listId = (List<PageData>) JSONArray.parse(idStr);
            // 打开文档，开始填写内容
            Document document = new Document(new RectangleReadOnly(842, 595));
            PdfWriter.getInstance(document, bos);
            document.open();
            if (listId.size() > 1) {
                // 文件名
                for (int i = 0; i < listId.size(); i++) {
                    if (i > 0) {//第一页往后，下一条记录新起一页
                        document.newPage();
                    }
                    PageData data = new PageData();
                    data.put("businessId", listId.get(i));
                    if (data.size() == 1) {//导出选择部分
                        data  = equipmentDepreciationExpensesService.getDepreciationExpensesDetail(data);
                    }
                    //生成单个pdf
                    equipmentDepreciationExpensesService.exportPDF(data, document);
                }
            } else {
                // 第一页
                if (listId.size() > 0) {
                    PageData data = new PageData();
                    data.put("businessId", listId.get(0));
                    if (data.size() == 1) {//导出选择部分
                        data  = equipmentDepreciationExpensesService.getDepreciationExpensesDetail(data);
                    }
                    equipmentDepreciationExpensesService.exportPDF(data, document);
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
            result = ResponseEntity.success(ConstantMsgUtil.INFO_EXPORT_SUCCESS.val(), ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "设备折旧支出单PDF", pageData);
        }
    }


    @ApiOperation(value = "导入模板")
    @PostMapping(value = "/upload")
    public ResponseEntity upload(DepreciationExpensesUploadFileDto dto) {
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = equipmentDepreciationExpensesService.upload(dto.getFile(),pd);

            result = ResponseEntity.success(PropertyUtil.covertListModel(list, DepreciationExpensesDetailDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("导入设备折旧支出单失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    /**
     * 导入模板时校验
     * @param dto
     * @return
     */
    public static PageData checkParams(DepreciationExpensesUploadFileDto dto) {
        PageData pageData = new PageData();

        MultipartFile file = dto.getFile();
        CheckParameter.isNull(file, "上传文件");

        String  projectName = dto.getProjectName();
        CheckParameter.stringLengthAndEmpty(projectName, "项目名称",256);

        String  projectApplyMainId = dto.getProjectApplyMainId();
        CheckParameter.stringLengthAndEmpty(projectApplyMainId, "项目业务主键ID",256);

        String  belongingMonth = dto.getBelongingMonth();
        CheckParameter.stringLengthAndEmpty(belongingMonth, "所属月份",16);

        CheckParameter.stringLengthAndEmpty(dto.getStartYear(), "研发项目开始年份", 256);
        CheckParameter.stringLengthAndEmpty(dto.getEndYear(), "研发项目结束年份", 256);
        String startYear = dto.getStartYear();
        String endYear = dto.getEndYear();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
        try {
            if (df.parse(belongingMonth).before(df.parse(startYear)) || df.parse(belongingMonth).after(df.parse(endYear))){
                throw new MyException("该设备折旧支出单所属月份不在当前项目研发周期内，请确认调整后重新选择");
            }
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        }

        pageData.put("belongingMonth",belongingMonth);
        pageData.put("projectName",projectName);
        pageData.put("projectApplyMainId",projectApplyMainId);

        return pageData;
    }


    /**
     * 保存时校验
     * @param pd
     */
    private void saveCheckParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称", 256);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
        String belongingMonth = pd.getString("belongingMonth");
        CheckParameter.stringLengthAndEmpty(belongingMonth, "所属月份", 16);
        CheckParameter.stringLengthAndEmpty(pd.getString("applyDate"), "申请日期", 16);

        CheckParameter.stringLengthAndEmpty(pd.getString("startYear"), "研发项目开始年份", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("endYear"), "研发项目结束年份", 256);
        String startYear = pd.getString("startYear");
        String endYear = pd.getString("endYear");
        try {
            if (df.parse(belongingMonth).before(df.parse(startYear)) || df.parse(belongingMonth).after(df.parse(endYear))){
                throw new MyException("该折旧单所属月份不在当前项目研发周期内，请确认调整后重新保存");
            }
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        }
        CheckParameter.stringLength(pd.getString("remark"), "备注", 1024);


        List<PageData> pageDataList = JSONArray.parseArray(pd.getString("depreciationExpensesDetailList"), PageData.class);
        if (pageDataList.size() > 0){
            for (PageData pageData : pageDataList) {
                if(!pageData.getString("typeCode").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("typeCode"), "类型编码", 128);
                }
                if(!pageData.getString("financialCode").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("financialCode"), "资产编号", 128);
                }
                if(!pageData.getString("equipmentName").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("equipmentName"), "设备名称", 128);
                }

                if(!pageData.getString("originalValue").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("originalValue"), "设备原值", 24, 2);
                }
                if(!pageData.getString("dateIn").isEmpty()){
                    CheckParameter.checkDate(pageData,pageData.getString("dateIn"), "购入日期", "yyyy-MM-dd");
                }

                if(!pageData.getString("thisMonthDepreciation").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("thisMonthDepreciation"), "本期折旧", 24, 2);
                }
                if(!pageData.getString("accumulatedDepreciation").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("accumulatedDepreciation"), "累计折旧", 24, 2);
                }
                if(!pageData.getString("thisYearDepreciation").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("thisYearDepreciation"), "本年计提折旧", 24, 2);
                }
                if(!pageData.getString("thisYearAccumulated").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("thisYearAccumulated"), "本年累计发生", 24, 2);
                }

                if(!pageData.getString("assetOriginalValue").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("assetOriginalValue"), "资产原值", 24, 2);
                }
                if(!pageData.getString("assetNumber").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("assetNumber"), "资产数量", 24, 2);
                }
                if(!pageData.getString("assetNetValue").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("assetNetValue"), "资产净值", 24, 2);
                }
                if(!pageData.getString("residualValue").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("residualValue"), "净残值", 24, 2);
                }
                if(!pageData.getString("orgCode").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("orgCode"), "单位编码", 255);
                }
                if(!pageData.getString("orgUse").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("orgUse"), "使用单位", 255);
                }
                if(!pageData.getString("useDepartment").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("useDepartment"), "使用部门", 100);
                }
                CheckParameter.intNumberLength(pageData.getString("paramSource"), "导入模块数据标识",10);
            }
        }else {
            throw new MyException("设备折旧明细不能为空");
        }
    }
}
