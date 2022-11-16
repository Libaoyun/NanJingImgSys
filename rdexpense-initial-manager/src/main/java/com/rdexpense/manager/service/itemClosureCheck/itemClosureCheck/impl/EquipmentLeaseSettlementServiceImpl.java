package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.itemClosureCheck.EquipmentLeaseSettlementService;
import com.rdexpense.manager.service.itemClosureCheck.ItemExpensesService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import jodd.util.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.janino.Java;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.ion.Decimal;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class EquipmentLeaseSettlementServiceImpl implements EquipmentLeaseSettlementService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    private FileService fileService;

    @Autowired
    @Resource(name = "FlowService")
    private FlowService flowService;

    /**
     * 查询列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryList(PageData pd) {
        List<String> pageDataList = JSONArray.parseArray(pd.getString("statusList"), String.class);
        pd.put("processStatusList", pageDataList);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        pd.put("processQuitStatus", ConstantValUtil.APPROVAL_STATUS[3]);
        pd.put("selectCreateUser", pd.getString("creatorUserName"));

        //处理日期搜索条件
        String createTime = pd.getString("createTime");
        if(StringUtils.isNotBlank(createTime)){
            JSONArray createTimeArr = JSONArray.parseArray(createTime);
            pd.put("beginCreateTime",createTimeArr.get(0) + " 00:00:00");
            pd.put("lastCreateTime",createTimeArr.get(1) + " 23:59:59");
        }

        String updateTime = pd.getString("updateTime");
        if(StringUtils.isNotBlank(updateTime)){
            JSONArray updateTimeArr = JSONArray.parseArray(updateTime);
            pd.put("beginUpdateTime",updateTimeArr.get(0) + " 00:00:00");
            pd.put("lastUpdateTime",updateTimeArr.get(1) + " 23:59:59");
        }
        List<PageData> leaseSettlementList = (List<PageData>) baseDao.findForList("EquipmentLeaseSettlementMapper.queryList", pd);

        return leaseSettlementList;
    }

    /**
     * 删除
     * @param pd
     */
    @Override
    @Transactional
    public void deleteEquipmentLeaseSettlement(PageData pd) {
        List<String> businessIdList = JSONArray.parseArray(pd.getString("businessIdList"), String.class);
        //删除主表
        baseDao.batchDelete("EquipmentLeaseSettlementMapper.deleteEquipmentLeaseSettlement", businessIdList);

        //删除明细
        baseDao.batchDelete("EquipmentLeaseSettlementMapper.deleteEquipmentLeaseSettlementDetail", businessIdList);

        //先删除附件表
        fileService.deleteAttachment(pd);
    }

    /**
     * 详情
     * @param pd
     * @return
     */
    @Override
    public PageData getEquipmentLeaseSettlementDetail(PageData pd) {
        DecimalFormat df = new DecimalFormat("0.00000000");
        //1、查询主表
        PageData request = (PageData) baseDao.findForObject("EquipmentLeaseSettlementMapper.queryDetail", pd);

        //获取明细
        List<PageData> detailList = (List<PageData>) baseDao.findForList("EquipmentLeaseSettlementMapper.querySettlementDetail", pd);
        if (!CollectionUtils.isEmpty(detailList)){
            for (PageData pageData : detailList) {
                pageData.put("unitPriceExcludingTax",df.format(new BigDecimal(pageData.getString("unitPriceExcludingTax"))));
                pageData.put("taxRate",df.format(new BigDecimal(pageData.getString("taxRate"))));
                pageData.put("priceExcludingTax",df.format(new BigDecimal(pageData.getString("priceExcludingTax"))));
            }
        }

        request.put("settlementDetailList",detailList);

        // 查询附件表
        fileService.queryFileByBusinessId(request);

        return request;
    }

    /**
     * 保存或编辑
     * @param pd
     */
    @Override
    @Transactional
    public void saveOrUpdate(PageData pd) {

        //明细中添加主表业务主键ID，及计算明细表中的含税金额总金额
        String settlementDetail = pd.getString("settlementDetailList");
        List<PageData> settlementDetailList = JSONObject.parseArray(settlementDetail, PageData.class);
        if (!CollectionUtils.isEmpty(settlementDetailList)) {
            BigDecimal contractAddAmountWithoutTax = new BigDecimal("0.00");
            for (PageData detailData : settlementDetailList) {
                if (StringUtil.isNotBlank(detailData.getString("settlementAmountExcludingTax"))){
                    contractAddAmountWithoutTax = contractAddAmountWithoutTax.add(new BigDecimal(String.valueOf(detailData.getString("settlementAmountExcludingTax"))));
                }
            }
            pd.put("contractAddAmountWithoutTax",contractAddAmountWithoutTax);
        }

        //如果ID为空，则为保存
        if (pd.getString("id").isEmpty()){
            //判断是直接保存，还是提交时保存
            //直接保存
            if (pd.getString("flag").isEmpty()){
                pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
                String serialNumber = SequenceUtil.generateSerialNo("RDMC");//生成流水号
                String businessId = "RDMC" + UUID.randomUUID().toString();//生成业务主键ID
                pd.put("serialNumber", serialNumber);
                pd.put("businessId", businessId);
            }

            //1、插入主表
            baseDao.insert("EquipmentLeaseSettlementMapper.insertMain", pd);

            //2、插入明细
            if (!CollectionUtils.isEmpty(settlementDetailList)) {
                for (PageData detailData : settlementDetailList) {
                    detailData.put("businessId", pd.getString("businessId"));
                }
                baseDao.insert("EquipmentLeaseSettlementMapper.insertSettlementDetail", settlementDetailList);
            }

            // 插入到附件表
            fileService.insert(pd);

        } else {
            //编辑
            //1、更新主表
            baseDao.update("EquipmentLeaseSettlementMapper.updateMain", pd);

            //删除明细
            baseDao.delete("EquipmentLeaseSettlementMapper.deleteEquipmentLeaseSettlementDetailByBusinessId", pd);

            //插入明细表
            if (!CollectionUtils.isEmpty(settlementDetailList)) {
                for (PageData detailData : settlementDetailList) {
                    detailData.put("businessId", pd.getString("businessId"));
                }
                baseDao.batchInsert("EquipmentLeaseSettlementMapper.insertSettlementDetail", settlementDetailList);
            }

            //先删除附件表
            fileService.deleteAttachment(pd);
            //再插入附件表
            fileService.insert(pd);
        }
    }

    /**
     * 导出excel
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        DecimalFormat df = new DecimalFormat("0.00000000");
        String title = "研发项目租赁设备结算单";
        String[] head = {"序号", "单据编号", "项目名称", "项目负责人", "所属月份", "申请日期", "合同编号", "设备名称", "管理号码", "规格型号", "结算开始时间",
                "结算截至时间","租赁方式","计费方式","不含税单价（元）","含税单价（元）","租期/工作量","税率（%）","税额（元）","其他费用（元）",
                "其他费用说明","扣款金额（元）","扣款金额说明","不含税结算金额（元）","含税结算金额（元）", "款项性质", "费用项目",
                "编制人", "创建时间", "更新时间"};
        String idStr = pageData.getString("businessIdList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> checkInfoList = (List<PageData>) baseDao.findForList("EquipmentLeaseSettlementMapper.queryDetailExportExcel", listId);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title + 1);
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }

        BigDecimal taxSum = new BigDecimal("0.00");
        BigDecimal settlementAmountExcludingSum = new BigDecimal("0.00");
        BigDecimal settlementAmountSum = new BigDecimal("0.00");

        if (!CollectionUtils.isEmpty(checkInfoList)) {
            for (int i = 0; i < checkInfoList.size(); i++) {
                PageData pd = checkInfoList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("serial_number"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("project_name"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("apply_user_name"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("belonging_month"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("apply_date"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("contract_code"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("equip_name"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("manage_code"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("spec"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("settlement_start_time"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("settlement_end_time"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("rent_type"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("fee_type"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String unit_price_excluding_tax = "0.00000000";
                if(StringUtils.isNotBlank(pd.getString("unit_price_excluding_tax"))){
                    unit_price_excluding_tax = df.format(new BigDecimal(pd.getString("unit_price_excluding_tax")));
                }
                cell.setCellValue(unit_price_excluding_tax);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String price_excluding_tax = "0.00000000";
                if(StringUtils.isNotBlank(pd.getString("price_excluding_tax"))){
                    price_excluding_tax = df.format(new BigDecimal(pd.getString("price_excluding_tax")));
                }
                cell.setCellValue(price_excluding_tax);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("planned_quantity"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
//                String tax_rate = "0.00000000";
//                if(StringUtils.isNotBlank(pd.getString("price_excluding_tax"))){
//                    tax_rate = df.format(new BigDecimal(pd.getString("tax_rate")));
//                }
                cell.setCellValue(pd.getString("tax_rate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("tax"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("other_fees"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("other_fees_cost"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("deductions_money"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("deductions_money_cost"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("settlement_amount_excluding_tax"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("settlement_amount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("nature_payment_name"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("cost_item_name"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("create_user"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String createTime = pd.getString("create_time");
                cell.setCellValue(createTime.substring(0, createTime.lastIndexOf(".")));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String update_time = pd.getString("update_time");
                cell.setCellValue(update_time.substring(0, update_time.lastIndexOf(".")));
                cell.setCellStyle(styleCell);

                if(StringUtils.isNotBlank(pd.getString("tax"))){
                    taxSum = taxSum.add(new BigDecimal(pd.getString("tax")));
                }

                if(StringUtils.isNotBlank(pd.getString("settlement_amount_excluding_tax"))){
                    settlementAmountExcludingSum = settlementAmountExcludingSum.add(new BigDecimal(pd.getString("settlement_amount_excluding_tax")));
                }

                if(StringUtils.isNotBlank(pd.getString("settlement_amount"))){
                    settlementAmountSum = settlementAmountSum.add(new BigDecimal(pd.getString("settlement_amount")));
                }
            }
        }

        HSSFRow row1 = sheet.createRow( checkInfoList.size() + 2);
        for (int i = 0; i < head.length; i++) {
            if (i == 0){
                HSSFCell cell0 = row1.createCell(0);
                cell0.setCellValue("合计");
                cell0.setCellStyle(styleCell);
            } else if (i == 18){
                HSSFCell cell18 = row1.createCell(18);
                cell18.setCellValue(String.valueOf(taxSum));
                cell18.setCellStyle(styleCell);
            } else if (i == 23){
                HSSFCell cell23 = row1.createCell(23);
                cell23.setCellValue(String.valueOf(settlementAmountExcludingSum));
                cell23.setCellStyle(styleCell);
            } else if (i == 24){
                HSSFCell cell24 = row1.createCell(24);
                cell24.setCellValue(String.valueOf(settlementAmountSum));
                cell24.setCellStyle(styleCell);
            } else {
                HSSFCell cell25 = row1.createCell(i);
                cell25.setCellValue("");
                cell25.setCellStyle(styleCell);
            }
        }

        //设置自适应宽度
        for (int j = 0; j < head.length; j++) {
            sheet.autoSizeColumn(j);
            int colWidth = sheet.getColumnWidth(j) * 17 / 10;
            if (colWidth < 255 * 256) {
                sheet.setColumnWidth(j, colWidth < 3000 ? 3000 : colWidth);
            } else {
                sheet.setColumnWidth(j, 6000);
            }
        }
        sheet.setAutobreaks(true);
        return wb;
    }

    /**
     * 导出PDF
     * @param data
     * @param document
     */
    @SneakyThrows
    @Override
    public void exportPDF(PageData data, Document document)  {
        DecimalFormat df = new DecimalFormat("0.00000000");
        //设置logo和标题
        PDFUtil.addTitle(document,"租赁设备结算单");
        //设置基本信息
        int width1[] = {140, 300, 140, 400, 140, 200};//每栏的宽度

        String time1 = data.getString("createTime");
        if(StringUtils.isNotBlank(time1)){
            time1 = time1.substring(0,time1.lastIndexOf("."));
        }
        String[] argArr1 = {
                "单据编号", data.getString("serialNumber"),"项目名称",data.getString("projectName"), "申请日期", data.getString("createdDate"),
                "项目负责人",data.getString("applyUserName"), "创建人", data.getString("createUser"), "创建时间", time1,
                "所属月份",data.getString("belongingMonth"),"","","",""
                };
//        HashMap<Integer,Integer> mergeMap=new HashMap<>();
//        mergeMap.put(33,7);
//        mergeMap.put(35, 7);
        PdfPTable baseTableHaveMerge = PDFUtil.createBaseTableHaveMerge(width1, argArr1, null);


        //明细信息
        String[] argArr2 = {"序号", "合同编号", "设备名称", "管理号码", "规格型号", "结算开始时间",
                "结算截至时间","租赁方式","计费方式","不含税单价（元）","含税单价（元）","租期/工作量","税率（%）","税额（元）","其他费用（元）",
                "其他费用说明","扣款金额（元）","扣款金额说明","不含税结算金额（元）","含税结算金额（元）", "款项性质", "费用项目"};
        int width2[] = {100, 200, 250, 150, 150, 150, 200, 200, 200, 200,200, 200, 200, 200, 200,200, 200, 200, 200, 200, 200, 200};//每栏的宽度
        //明细数据写入
        List<PageData> list = (List<PageData>) baseDao.findForList("EquipmentLeaseSettlementMapper.querySettlementDetail", data);

        String[] argDetail = null;
        List<String> detailList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PageData pageData = (PageData) list.get(i);
            argDetail = new String[]{String.valueOf(i + 1), pageData.getString("contractCode"), pageData.getString("equipName"),
                    pageData.getString("manageCode"), pageData.getString("spec"), pageData.getString("settlementStartTime"),
                    pageData.getString("settlementEndTime"), pageData.getString("rentType"), pageData.getString("feeType"),
                    df.format(new BigDecimal(pageData.getString("unitPriceExcludingTax"))), df.format(new BigDecimal(pageData.getString("priceExcludingTax"))), pageData.getString("plannedQuantity"),
                    df.format(new BigDecimal(pageData.getString("taxRate"))), pageData.getString("tax"), pageData.getString("otherFees"),
                    pageData.getString("otherFeesCost"), pageData.getString("deductionsMoney"), pageData.getString("deductionsMoneyCost"),
                    pageData.getString("settlementAmountExcludingTax"), pageData.getString("settlementAmount"), pageData.getString("naturePaymentName"),
                    pageData.getString("costItemName")
            };
            String jsonString2 = JSON.toJSONString(argDetail);
            detailList.add(jsonString2);
        }
        PdfPTable detailTable = PDFUtil.createDetailTableNotMerge("租赁设备结算明细", width2, argArr2, detailList, null, null);

        //设置审批记录
        flowService.getApproveTable2(data,detailTable);

        //将表2合到表1中
        PDFUtil.mergeTable(baseTableHaveMerge,detailTable);
        //添加表格
        document.add(baseTableHaveMerge);
    }

    /**
     * 导入模板信息
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> upload(MultipartFile file, PageData pd) throws Exception {

        XSSFWorkbook wb = null;
        try {
            wb = ReadExcelUtil.getWorkbook(file);
        } catch (Exception e) {
            throw new MyException("请上传正确的模板");
        }
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("设备租赁结算单");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查sheet页名称是否为设备租赁结算单");
        }

        XSSFRow row = sheet.getRow(1);
        //课题
        XSSFCell cell = row.getCell(1);
        String project = ReadExcelUtil.readCellStr(cell, 2, "课题项目", false, 256);

        if(project == null || !project.equals(pd.getString("projectName"))){
            throw new MyException("模板中的项目名称与页面选择项目名称不一致，请确认后重新上传");
        }

        //月份
        cell = row.getCell(12);
        String belongingMonth = ReadExcelUtil.readCellMonth(cell, 2, "结算月份", false);

        if(belongingMonth == null || !belongingMonth.equals(pd.getString("belongingMonth"))){
            throw new MyException("模板中的所属月份与页面选择所属月份不一致，请确认后重新上传");
        }


        String[] head = {"序号","合同编号","设备名称","管理号码","规格型号","结算起始时间","结算截止时间","租赁方式","计费方式",	"不含税单价(元)",
                "含税单价(元)","租期/工作量","税率(%)","税额(元)","其他费用(元)","其他费用说明","扣款金额(元)","扣款金额说明","结算金额（不含税)(元)",
                "结算金额(含税)(元)","款项性质","费用项目"};
        ReadExcelUtil.checkHead(sheet, head,2);

        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 3; i <= end; i++) {
            PageData data = new PageData();
            data.put("paramSource",0);
            row = sheet.getRow(i);

            if (row != null){
                int rowNumber = i + 1;
                //解析第一个单元格
                XSSFCell cell1 = row.getCell(1);
                String contractCode = ReadExcelUtil.readCellFormat(cell1, rowNumber, "合同编号", false, 128);
                data.put("contractCode",contractCode);

                //解析第二个单元格
                XSSFCell cell2 = row.getCell(2);
                String equipName = ReadExcelUtil.readCellFormat(cell2, rowNumber, "设备名称", false, 64);
                data.put("equipName",equipName);

                //解析第三个单元格
                XSSFCell cell3 = row.getCell(3);
                String manageCode = ReadExcelUtil.readCellFormat(cell3, rowNumber, "管理号码", false, 128);
                data.put("manageCode",manageCode);

                //解析第四个单元格
                XSSFCell cell4 = row.getCell(4);
                String spec = ReadExcelUtil.readCellFormat(cell4, rowNumber, "规格型号", false, 32);
                data.put("spec",spec);

                //解析第五个单元格
                XSSFCell cell5 = row.getCell(5);
                String settlementStartTime = ReadExcelUtil.readCellDate(cell5, rowNumber, "结算起始时间", false);
                data.put("settlementStartTime",settlementStartTime);

                XSSFCell cell6 = row.getCell(6);
                String settlementEndTime = ReadExcelUtil.readCellDate(cell6, rowNumber, "结算截止时间", false);
                data.put("settlementEndTime",settlementEndTime);

                //解析第六个单元格 不含税金额
                XSSFCell cell7 = row.getCell(7);
                String rentType = ReadExcelUtil.readCellFormat(cell7, rowNumber, "租赁方式", false, 32);
                data.put("rentType",rentType);

                //解析第七个单元格 备注
                XSSFCell cell8 = row.getCell(8);
                String feeType = ReadExcelUtil.readCellFormat(cell8, rowNumber, "计费方式", false, 32);
                data.put("feeType",feeType);

                XSSFCell cell9 = row.getCell(9);
                String unitPriceExcludingTax = ReadExcelUtil.readCellDecimal(cell9, rowNumber, "不含税单价(元)", false, 24,8);
                data.put("unitPriceExcludingTax",unitPriceExcludingTax);

                XSSFCell cell10 = row.getCell(10);
                String priceExcludingTax = ReadExcelUtil.readCellDecimal(cell10, rowNumber, "含税单价(元)", false, 24,8);
                data.put("priceExcludingTax",priceExcludingTax);

                XSSFCell cell11 = row.getCell(11);
                String plannedQuantity = ReadExcelUtil.readCellDecimal(cell11, rowNumber, "租期/工作量", false, 18,2);
                data.put("plannedQuantity",plannedQuantity);

                XSSFCell cell12= row.getCell(12);
                String taxRate = ReadExcelUtil.readCellDecimal(cell12, rowNumber, "税率(%)", false, 24,8);
                DecimalFormat df=new DecimalFormat("0.00");
                if (StringUtils.isNotBlank(taxRate)){
                    data.put("taxRate",df.format(new BigDecimal(taxRate)));
                }

                XSSFCell cell13 = row.getCell(13);
                String tax = ReadExcelUtil.readCellDecimal(cell13, rowNumber, "税额(元)", false, 24,2);
                data.put("tax",tax);

                XSSFCell cell14 = row.getCell(14);
                String otherFees = ReadExcelUtil.readCellDecimal(cell14, rowNumber, "其他费用(元)", false, 24,2);
                data.put("otherFees",otherFees);

                XSSFCell cell15 = row.getCell(15);
                String otherFeesCost = ReadExcelUtil.readCellFormat(cell15, rowNumber, "其他费用说明", false, 256);
                data.put("otherFeesCost",otherFeesCost);

                XSSFCell cell16 = row.getCell(16);
                String deductionsMoney = ReadExcelUtil.readCellDecimal(cell16, rowNumber, "扣款金额(元)", false, 24, 2);
                data.put("deductionsMoney",deductionsMoney);

                XSSFCell cell17 = row.getCell(17);
                String deductionsMoneyCost = ReadExcelUtil.readCellFormat(cell17, rowNumber, "扣款金额说明", false, 256);
                data.put("deductionsMoneyCost",deductionsMoneyCost);

                XSSFCell cell18 = row.getCell(18);
                String settlementAmountExcludingTax = ReadExcelUtil.readCellDecimal(cell18, rowNumber, "结算金额（不含税)(元)", false, 24, 2);
                data.put("settlementAmountExcludingTax",settlementAmountExcludingTax);

                XSSFCell cell19 = row.getCell(19);
                String settlementAmount = ReadExcelUtil.readCellDecimal(cell19, rowNumber, "结算金额(含税)(元)", false, 24, 2);
                data.put("settlementAmount",settlementAmount);

                XSSFCell cell20 = row.getCell(20);
                String naturePaymentName = ReadExcelUtil.readCellFormat(cell20, rowNumber, "款项性质", false, 255);
                data.put("naturePaymentName",naturePaymentName);

                XSSFCell cell21 = row.getCell(21);
                String costItemName = ReadExcelUtil.readCellFormat(cell21, rowNumber, "费用项目", false, 255);
                data.put("costItemName",costItemName);


                if(StringUtils.isBlank(contractCode) && StringUtils.isBlank(equipName) && StringUtils.isBlank(manageCode)){
                    continue;
                }

                list.add(data);
            }
        }

        return list;
    }
}
