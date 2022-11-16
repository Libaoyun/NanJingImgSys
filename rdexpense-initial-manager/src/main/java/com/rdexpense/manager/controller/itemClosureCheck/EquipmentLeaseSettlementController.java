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
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesBudgetAccumulatedDto;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesDetailDto;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesListDto;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.base.FlowAbolishDto;
import com.rdexpense.manager.dto.base.FlowApproveDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.EquipmentLeaseSettlementDetailDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.EquipmentLeaseSettlementListDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.EquipmentLeaseSettlementUploadFileDto;
import com.rdexpense.manager.dto.equipmentLeaseSettlement.SettlementDetailDto;
import com.rdexpense.manager.dto.materialRequisition.MaterialRequisitionDetailedDto;
import com.rdexpense.manager.dto.materialRequisition.MaterialRequisitionUploadFileDto;
import com.rdexpense.manager.service.itemClosureCheck.EquipmentLeaseSettlementService;
import com.rdexpense.manager.service.itemClosureCheck.ItemExpensesService;
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
@RequestMapping("/equipmentLeaseSettlement")
@Api(value = "租赁设备结算管理", tags = "租赁设备结算管理")
public class EquipmentLeaseSettlementController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentLeaseSettlementController.class);

    @Autowired
    private EquipmentLeaseSettlementService equipmentLeaseSettlementService;

    @Autowired
    private BaseDao dao;

    @Autowired
    private LogUtil logUtil;

    @PostMapping("/queryList")
    @ApiOperation(value = "查询租赁设备结算管理列表", notes = "查询租赁设备结算管理列表")
    public ResponseEntity<PageInfo<EquipmentLeaseSettlementListDto>> queryList(EquipmentLeaseSettlementListDto equipmentLeaseSettlementListDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = equipmentLeaseSettlementService.queryList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, EquipmentLeaseSettlementListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询租赁设备结算管理列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "删除租赁设备结算申请")
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
            equipmentLeaseSettlementService.deleteEquipmentLeaseSettlement(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除租赁设备结算申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "租赁设备结算申请", pd);
        }
    }

    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询租赁设备结算申请详情", notes = "查询租赁设备结算申请详情")
    @ApiImplicitParam(name = "businessId", value = "业务主键businessId", required = true, dataType = "String")
    public ResponseEntity<EquipmentLeaseSettlementDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键businessId",64);
        try {

            PageData pageData = equipmentLeaseSettlementService.getEquipmentLeaseSettlementDetail(pd);

            return PropertyUtil.pushData(pageData, EquipmentLeaseSettlementDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询租赁设备结算详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @ApiOperation(value = "新增或编辑")
    @PostMapping("/saveOrUpdate")
    public ResponseEntity saveOrUpdate(EquipmentLeaseSettlementDetailDto equipmentLeaseSettlementDetailDto) {
        PageData pd = this.getParams();
        saveCheckParam(pd);
        ResponseEntity result = null;
        try {

            equipmentLeaseSettlementService.saveOrUpdate(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增或编辑租赁设备结算失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "租赁设备结算", pd);
        }
    }


    @ApiOperation(value = "导入模板")
    @PostMapping(value = "/upload")
    public ResponseEntity upload(EquipmentLeaseSettlementUploadFileDto dto) {
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            List<PageData> list = equipmentLeaseSettlementService.upload(dto.getFile(),pd);

            result = ResponseEntity.success(PropertyUtil.covertListModel(list, SettlementDetailDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("导入租赁设备结算单失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
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
            HSSFWorkbook wb = equipmentLeaseSettlementService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "研发项目租赁设备结算单_" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            logUtil.saveLogData(result.getCode(), 6, "研发项目租赁设备结算单EXCEL", pd);
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
            String fileName =  "租赁设备结算_" + date + ".pdf";

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
                        data  = equipmentLeaseSettlementService.getEquipmentLeaseSettlementDetail(data);
                    }
                    //生成单个pdf
                    equipmentLeaseSettlementService.exportPDF(data, document);
                }
            } else {
                // 第一页
                if (listId.size() > 0) {
                    PageData data = new PageData();
                    data.put("businessId", listId.get(0));
                    if (data.size() == 1) {//导出选择部分
                        data  = equipmentLeaseSettlementService.getEquipmentLeaseSettlementDetail(data);
                    }
                    equipmentLeaseSettlementService.exportPDF(data, document);
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
            logUtil.saveLogData(result.getCode(), 6, "租赁设备结算PDF", pageData);
        }
    }


    /**
     * 导入模板时校验
     * @param dto
     * @return
     */
    public static PageData checkParams(EquipmentLeaseSettlementUploadFileDto dto) {
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
                throw new MyException("该设备租赁结算单所属月份不在当前项目研发周期内，请确认调整后重新选择");
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

        CheckParameter.stringLengthAndEmpty(pd.getString("startYear"), "研发项目开始年份", 16);
        CheckParameter.stringLengthAndEmpty(pd.getString("endYear"), "研发项目结束年份", 16);
        String startYear = pd.getString("startYear");
        String endYear = pd.getString("endYear");
        try {
            if (df.parse(belongingMonth).before(df.parse(startYear)) || df.parse(belongingMonth).after(df.parse(endYear))){
                throw new MyException("该设备租赁结算单所属月份不在当前项目研发周期内，请确认调整后重新保存");
            }
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        }
        CheckParameter.stringLength(pd.getString("remark"), "备注", 1024);

        List<PageData> pageDataList = JSONArray.parseArray(pd.getString("settlementDetailList"), PageData.class);
        if (pageDataList.size() > 0){
            for (int i = 0; i < pageDataList.size(); i++) {
                PageData pageData = pageDataList.get(i);

                int j = i + 1;
                if(!pageData.getString("contractCode").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("contractCode"), "合同编号", 128);
                } else {
                    throw new MyException("第" + j + "行的合同编号不能为空");
                }
                if(!pageData.getString("equipName").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("equipName"), "设备名称", 128);
                } else {
                    throw new MyException("第" + j + "行的设备名称不能为空");
                }
                if(!pageData.getString("manageCode").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("manageCode"), "管理号码", 128);
                } else {
                    throw new MyException("第" + j + "行的管理号码不能为空");
                }
                if(!pageData.getString("spec").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("spec"), "规格型号", 128);
                } else {
                    throw new MyException("第" + j + "行的规格型号不能为空");
                }
                if(!pageData.getString("settlementStartTime").isEmpty()){
                    CheckParameter.checkDate(pageData,pageData.getString("settlementStartTime"), "结算起始时间", "yyyy-MM-dd");
                } else {
                    throw new MyException("第" + j + "行的结算起始时间不能为空");
                }
                if(!pageData.getString("settlementEndTime").isEmpty()){
                    CheckParameter.checkDate(pageData,pageData.getString("settlementEndTime"), "结算截止时间", "yyyy-MM-dd");
                } else {
                    throw new MyException("第" + j + "行的结算截止时间不能为空");
                }
                if(!pageData.getString("rentType").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("rentType"), "租赁方式", 32);
                } else {
                    throw new MyException("第" + j + "行的租赁方式不能为空");
                }
                if(!pageData.getString("feeType").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("feeType"), "计费方式", 32);
                } else {
                    throw new MyException("第" + j + "行的计费方式不能为空");
                }
                if(!pageData.getString("unitPriceExcludingTax").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("unitPriceExcludingTax"), "不含税单价", 24, 8);
                } else {
                    throw new MyException("第" + j + "行的不含税单价不能为空");
                }
                if(!pageData.getString("priceExcludingTax").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("priceExcludingTax"), "含税单价", 24, 8);
                } else {
                    throw new MyException("第" + j + "行的含税单价不能为空");
                }
                if(!pageData.getString("plannedQuantity").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("plannedQuantity"), "租期/工作量", 18, 2);
                } else {
                    throw new MyException("第" + j + "行的租期/工作量不能为空");
                }

                if(!pageData.getString("taxRate").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("taxRate"), "税率", 24, 8);
                } else {
                    throw new MyException("第" + j + "行的税率不能为空");
                }

                if(!pageData.getString("tax").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("tax"), "税额", 24, 2);
                } else {
                    throw new MyException("第" + j + "行的税额不能为空");
                }
                if(!pageData.getString("otherFees").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("otherFees"), "其他费用", 24, 2);
                } else {
                    throw new MyException("第" + j + "行的其他费用不能为空");
                }
                if(!pageData.getString("otherFeesCost").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("otherFeesCost"), "其他费用说明", 256);
                } else {
                    throw new MyException("第" + j + "行的其他费用说明不能为空");
                }
                if(!pageData.getString("deductionsMoney").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("deductionsMoney"), "扣款金额", 24, 2);
                } else {
                    throw new MyException("第" + j + "行的扣款金额说明不能为空");
                }
                if(!pageData.getString("deductionsMoneyCost").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("deductionsMoneyCost"), "扣款金额说明", 256);
                } else {
                    throw new MyException("第" + j + "行的扣款金额说明不能为空");
                }
                if(!pageData.getString("settlementAmountExcludingTax").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("settlementAmountExcludingTax"), "结算金额（不含税）", 24, 2);
                } else {
                    throw new MyException("第" + j + "行的结算金额（不含税）不能为空");
                }
                if(!pageData.getString("settlementAmount").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("settlementAmount"), "结算金额（含税）", 24, 2);
                } else {
                    throw new MyException("第" + j + "行的结算金额（含税）不能为空");
                }
                if(!pageData.getString("naturePaymentCode").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("naturePaymentCode"), "款项性质编码", 64);
                }

                if(!pageData.getString("naturePaymentName").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("naturePaymentName"), "款项性质名称", 255);
                } else {
                    throw new MyException("第" + j + "行的款项性质名称不能为空");
                }
                if(!pageData.getString("costItemCode").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("costItemCode"), "费用项目编码", 64);
                }
                if(!pageData.getString("costItemName").isEmpty()){
                    CheckParameter.stringLength(pageData.getString("costItemName"), "费用项目名称", 255);
                } else {
                    throw new MyException("第" + j + "行的费用项目名称不能为空");
                }

                CheckParameter.intNumberLength(pageData.getString("paramSource"), "导入模块数据标识",10);

            }
        }else {
            throw new MyException("租赁设备结算明细不能为空");
        }
    }
}
