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
import com.rdexpense.manager.service.itemClosureCheck.EquipmentDepreciationExpensesService;
import com.rdexpense.manager.service.itemClosureCheck.EquipmentLeaseSettlementService;
import jodd.util.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class EquipmentDepreciationExpensesServiceImpl implements EquipmentDepreciationExpensesService {

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
        List<PageData> expensesList = (List<PageData>) baseDao.findForList("EquipmentDepreciationExpensesMapper.queryList", pd);

        return expensesList;
    }

    /**
     * 删除
     * @param pd
     */
    @Override
    public void deleteEquipmentDepreciationExpenses(PageData pd) {
        List<String> businessIdList = JSONArray.parseArray(pd.getString("businessIdList"), String.class);
        //删除主表
        baseDao.batchDelete("EquipmentDepreciationExpensesMapper.deleteEquipmentDepreciationExpenses", businessIdList);

        //删除明细
        baseDao.batchDelete("EquipmentDepreciationExpensesMapper.deleteEquipmentDepreciationExpensesDetail", businessIdList);

        //先删除附件表
        fileService.deleteAttachment(pd);
    }

    /**
     * 详情
     * @param pd
     * @return
     */
    @Override
    public PageData getDepreciationExpensesDetail(PageData pd) {
        //1、查询主表
        PageData request = (PageData) baseDao.findForObject("EquipmentDepreciationExpensesMapper.queryDetail", pd);

        //获取明细
        List<PageData> detailList = (List<PageData>) baseDao.findForList("EquipmentDepreciationExpensesMapper.queryDepreciationExpensesDetail", pd);
        request.put("depreciationExpensesDetailList",detailList);

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
        String depreciationExpensesDetail = pd.getString("depreciationExpensesDetailList");
        List<PageData> settlementDetailList = JSONObject.parseArray(depreciationExpensesDetail, PageData.class);
        if (!CollectionUtils.isEmpty(settlementDetailList)) {
            BigDecimal contractAddAmountWithoutTax = new BigDecimal("0.00");
            for (PageData detailData : settlementDetailList) {
                if (StringUtils.isNotBlank(detailData.getString("thisMonthDepreciation"))){
                    contractAddAmountWithoutTax = contractAddAmountWithoutTax.add(new BigDecimal(detailData.getString("thisMonthDepreciation")));
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
                String serialNumber = SequenceUtil.generateSerialNo("RDZJ");//生成流水号
                String businessId = "RDZJ" + UUID.randomUUID().toString();//生成业务主键ID
                pd.put("serialNumber", serialNumber);
                pd.put("businessId", businessId);
            }

            //1、插入主表
            baseDao.insert("EquipmentDepreciationExpensesMapper.insertMain", pd);

            //2、插入明细,把业务主键ID赋值给详细表
            if (!CollectionUtils.isEmpty(settlementDetailList)) {
                for (PageData detailData : settlementDetailList) {
                    detailData.put("businessId", pd.getString("businessId"));
                }
            }
            if (!CollectionUtils.isEmpty(settlementDetailList)) {
                baseDao.insert("EquipmentDepreciationExpensesMapper.insertDepreciationExpensesDetail", settlementDetailList);
            }

            // 插入到附件表
            fileService.insert(pd);

        } else {
            //编辑
            //1、更新主表
            baseDao.update("EquipmentDepreciationExpensesMapper.updateMain", pd);

            //删除明细
            baseDao.delete("EquipmentDepreciationExpensesMapper.deleteDetailByBusinessId", pd);

            //插入明细表
            if (!CollectionUtils.isEmpty(settlementDetailList)) {
                for (PageData detailData : settlementDetailList) {
                    detailData.put("businessId", pd.getString("businessId"));
                }
                baseDao.batchInsert("EquipmentDepreciationExpensesMapper.insertDepreciationExpensesDetail", settlementDetailList);
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
        String title = "设备折旧支出单";
        String[] head = {"序号", "单据编号", "项目名称", "项目负责人", "所属月份", "申请日期", "类别编号", "资产编号", "设备名称", "设备原值（元）", "购入日期",
                "本期折旧（元）","累计折旧（元）","本年计提折旧（元）","本年累计发生（元）","资产原值（元）","资产数量","资产净值（元）","净残值（元）","单位编号",
                "使用单位","使用人", "编制人", "创建时间", "更新时间"};
        String idStr = pageData.getString("businessIdList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> checkInfoList = (List<PageData>) baseDao.findForList("EquipmentDepreciationExpensesMapper.queryDetailExportExcel", listId);

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

        BigDecimal originalValueSum = new BigDecimal("0.00");
        BigDecimal thisMonthDepreciationSum = new BigDecimal("0.00");
        BigDecimal accumulatedDepreciationSum = new BigDecimal("0.00");
        BigDecimal thisYearDepreciationSum = new BigDecimal("0.00");
        BigDecimal thisYearAccumulatedSum = new BigDecimal("0.00");
        BigDecimal assetOriginalValueSum = new BigDecimal("0.00");
        BigDecimal assetNetValueSum = new BigDecimal("0.00");
        BigDecimal residualValueSum = new BigDecimal("0.00");

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
                cell.setCellValue(pd.getString("type_code"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("financial_code"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("equipment_name"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("original_value"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("date_in"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("this_month_depreciation"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("accumulated_depreciation"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("this_year_depreciation"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("this_year_accumulated"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("asset_original_value"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("asset_number"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("asset_net_value"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("residual_value"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("org_code"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("org_use"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("use_department"));
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


                if(StringUtils.isNotBlank(pd.getString("original_value"))){
                    originalValueSum = originalValueSum.add(new BigDecimal(pd.getString("original_value")));
                }

                if(StringUtils.isNotBlank(pd.getString("this_month_depreciation"))){
                    thisMonthDepreciationSum = thisMonthDepreciationSum.add(new BigDecimal(pd.getString("this_month_depreciation")));
                }

                if(StringUtils.isNotBlank(pd.getString("accumulated_depreciation"))){
                    accumulatedDepreciationSum = accumulatedDepreciationSum.add(new BigDecimal(pd.getString("accumulated_depreciation")));
                }

                if(StringUtils.isNotBlank(pd.getString("this_year_depreciation"))){
                    thisYearDepreciationSum = thisYearDepreciationSum.add(new BigDecimal(pd.getString("this_year_depreciation")));
                }

                if(StringUtils.isNotBlank(pd.getString("this_year_accumulated"))){
                    thisYearAccumulatedSum = thisYearAccumulatedSum.add(new BigDecimal(pd.getString("this_year_accumulated")));
                }

                if(StringUtils.isNotBlank(pd.getString("asset_original_value"))){
                    assetOriginalValueSum = assetOriginalValueSum.add(new BigDecimal(pd.getString("asset_original_value")));
                }

                if(StringUtils.isNotBlank(pd.getString("asset_net_value"))){
                    assetNetValueSum = assetNetValueSum.add(new BigDecimal(pd.getString("asset_net_value")));
                }

                if(StringUtils.isNotBlank(pd.getString("residual_value"))){
                    residualValueSum = residualValueSum.add(new BigDecimal(pd.getString("residual_value")));
                }
            }
        }

        HSSFRow row1 = sheet.createRow( checkInfoList.size() + 2);
        for (int i = 0; i < head.length; i++) {
            if (i == 0){
                HSSFCell cell0 = row1.createCell(0);
                cell0.setCellValue("合计");
                cell0.setCellStyle(styleCell);
            } else if (i == 9){
                HSSFCell cell9 = row1.createCell(9);
                cell9.setCellValue(String.valueOf(originalValueSum));
                cell9.setCellStyle(styleCell);
            } else if (i == 11){
                HSSFCell cell11 = row1.createCell(11);
                cell11.setCellValue(String.valueOf(thisMonthDepreciationSum));
                cell11.setCellStyle(styleCell);
            } else if (i == 12){
                HSSFCell cell12 = row1.createCell(12);
                cell12.setCellValue(String.valueOf(accumulatedDepreciationSum));
                cell12.setCellStyle(styleCell);
            } else if (i == 13){
                HSSFCell cell13 = row1.createCell(13);
                cell13.setCellValue(String.valueOf(thisYearDepreciationSum));
                cell13.setCellStyle(styleCell);
            } else if (i == 14){
                HSSFCell cell14 = row1.createCell(14);
                cell14.setCellValue(String.valueOf(thisYearAccumulatedSum));
                cell14.setCellStyle(styleCell);
            } else if (i == 15){
                HSSFCell cell15 = row1.createCell(15);
                cell15.setCellValue(String.valueOf(assetOriginalValueSum));
                cell15.setCellStyle(styleCell);
            } else if (i == 17){
                HSSFCell cell17 = row1.createCell(17);
                cell17.setCellValue(String.valueOf(assetNetValueSum));
                cell17.setCellStyle(styleCell);
            } else if (i == 18){
                HSSFCell cell18 = row1.createCell(18);
                cell18.setCellValue(String.valueOf(residualValueSum));
                cell18.setCellStyle(styleCell);
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
        //设置logo和标题
        PDFUtil.addTitle(document,"设备折旧支出单");
        //设置基本信息
        int width1[] = {140, 400, 140, 300, 140, 160};//每栏的宽度
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
        String[] argArr2 = {"序号", "类别编号", "资产编号", "设备名称", "设备原值（元）", "购入日期",
                "本期折旧（元）","累计折旧（元）","本年计提折旧（元）","本年累计发生（元）","资产原值（元）","资产数量","资产净值（元）","净残值（元）","单位编号",
                "使用单位","使用人"};
        int width2[] = {100, 200, 250, 150, 150, 150, 200, 200, 200, 200,200, 200, 200, 200, 200,200, 200};//每栏的宽度

        //明细数据写入
        List<PageData> list = (List<PageData>) baseDao.findForList("EquipmentDepreciationExpensesMapper.queryDepreciationExpensesDetail", data);

        BigDecimal originalValueSum = new BigDecimal("0.00");
        BigDecimal thisMonthDepreciationSum = new BigDecimal("0.00");
        BigDecimal accumulatedDepreciationSum = new BigDecimal("0.00");
        BigDecimal thisYearDepreciationSum = new BigDecimal("0.00");
        BigDecimal thisYearAccumulatedSum = new BigDecimal("0.00");
        BigDecimal assetOriginalValueSum = new BigDecimal("0.00");
        BigDecimal assetNetValueSum = new BigDecimal("0.00");
        BigDecimal residualValueSum = new BigDecimal("0.00");

        String[] argDetail = null;
        String[] argSum = null;
        List<String> detailList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PageData pageData = (PageData) list.get(i);
            argDetail = new String[]{String.valueOf(i + 1), pageData.getString("typeCode"), pageData.getString("financialCode"),
                    pageData.getString("equipmentName"), pageData.getString("originalValue"), pageData.getString("dateIn"),
                    pageData.getString("thisMonthDepreciation"), pageData.getString("accumulatedDepreciation"), pageData.getString("thisYearDepreciation"),
                    pageData.getString("thisYearAccumulated"), pageData.getString("assetOriginalValue"), pageData.getString("assetNumber"),
                    pageData.getString("assetNetValue"), pageData.getString("residualValue"), pageData.getString("orgCode"),
                    pageData.getString("orgUse"), pageData.getString("useDepartment")
            };

            originalValueSum = originalValueSum.add(new BigDecimal(pageData.getString("originalValue")));
            thisMonthDepreciationSum = thisMonthDepreciationSum.add(new BigDecimal(pageData.getString("thisMonthDepreciation")));
            accumulatedDepreciationSum = accumulatedDepreciationSum.add(new BigDecimal(pageData.getString("accumulatedDepreciation")));
            thisYearDepreciationSum = thisYearDepreciationSum.add(new BigDecimal(pageData.getString("thisYearDepreciation")));
            thisYearAccumulatedSum = thisYearAccumulatedSum.add(new BigDecimal(pageData.getString("thisYearAccumulated")));
            assetOriginalValueSum = assetOriginalValueSum.add(new BigDecimal(pageData.getString("assetOriginalValue")));
            assetNetValueSum = assetNetValueSum.add(new BigDecimal(pageData.getString("assetNetValue")));
            residualValueSum = residualValueSum.add(new BigDecimal(pageData.getString("residualValue")));

            String jsonString2 = JSON.toJSONString(argDetail);
            detailList.add(jsonString2);
        }



        argSum = new String[]{"合计", "", "",
                "", "", "",String.valueOf(thisMonthDepreciationSum), String.valueOf(accumulatedDepreciationSum), String.valueOf(thisYearDepreciationSum),
                String.valueOf(thisYearAccumulatedSum), String.valueOf(assetOriginalValueSum), "",String.valueOf(assetNetValueSum),String.valueOf(residualValueSum),
                "","",""
        };

        String jsonString3 = JSON.toJSONString(argSum);
        detailList.add(jsonString3);

        PdfPTable detailTable = PDFUtil.createDetailTableNotMerge("设备折旧支出单明细", width2, argArr2, detailList, null, null);

        //设置审批记录
        flowService.getApproveTable2(data,detailTable);

        //将表2合到表1中
        PDFUtil.mergeTable(baseTableHaveMerge,detailTable);
        //添加表格
        document.add(baseTableHaveMerge);
    }


    /**
     * 上传导入模板数据
     * @param file
     * @param pd
     * @return
     */
    @Override
    public List<PageData> upload(MultipartFile file, PageData pd) {
        XSSFWorkbook wb = null;
        try {
            wb = ReadExcelUtil.getWorkbook(file);
        } catch (Exception e) {
            throw new MyException("请上传正确的模板");
        }
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("设备折旧支出");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查sheet页名称是否为设备折旧支出");
        }

        XSSFRow row = sheet.getRow(1);
        //课题
        XSSFCell cell = row.getCell(1);
        String project = ReadExcelUtil.readCellStr(cell, 2, "课题项目", false, 256);

        if(project == null || !project.equals(pd.getString("projectName"))){
            throw new MyException("模板中的项目名称与页面选择项目名称不一致，请确认后重新上传");
        }

        //月份
        cell = row.getCell(9);
        String belongingMonth = ReadExcelUtil.readCellMonth(cell, 2, "折旧月份", false);

        if(belongingMonth == null || !belongingMonth.equals(pd.getString("belongingMonth"))){
            throw new MyException("模板中的所属月份与页面选择所属月份不一致，请确认后重新上传");
        }


        String[] head = {"序号","类别编号","资产编号","设备名称","设备原值","购入日期","本期折旧","累计折旧","本年计提折旧","本年累计发生",
                "资产原值","资产数量","资产净值","净残值","单位编号","使用单位","使用部门"};
        ReadExcelUtil.checkHead(sheet, head,2);


        //遍历文件
        int end = sheet.getLastRowNum();
        System.out.println("========"+end);
        for (int i = 3; i <= end; i++) {
            PageData data = new PageData();
            data.put("paramSource",0);

            row = sheet.getRow(i);
            if (row != null){
                int rowNumber = i + 1;
                //解析第一个单元格
                XSSFCell cell1 = row.getCell(1);
                String typeCode = ReadExcelUtil.readCellFormat(cell1, rowNumber, "类别编号", false, 128);
                data.put("typeCode",typeCode);


                //解析第二个单元格
                XSSFCell cell2 = row.getCell(2);
                String financialCode = ReadExcelUtil.readCellFormat(cell2, rowNumber, "资产编号", false, 128);
                if (i < end && financialCode.isEmpty()){
                    throw new MyException("第" + rowNumber + "行的资产编号不能为空");
                } else {
                    data.put("financialCode",financialCode);
                }

                //解析第三个单元格
                XSSFCell cell3 = row.getCell(3);
                String equipmentName = ReadExcelUtil.readCellFormat(cell3, rowNumber, "设备名称", false, 128);
                if (i < end && equipmentName.isEmpty()){
                    throw new MyException("第" + rowNumber + "行的设备名称不能为空");
                } else {
                    data.put("equipmentName",equipmentName);
                }

                //解析第四个单元格
                XSSFCell cell4 = row.getCell(4);
                String originalValue = ReadExcelUtil.readCellDecimal(cell4, rowNumber, "设备原值", false, 20, 2);
                if (i < end && originalValue.isEmpty()){
                    throw new MyException("第" + rowNumber + "行的设备原值不能为空");
                } else {
                    data.put("originalValue",originalValue);
                }


                //解析第五个单元格 不含税单价
                XSSFCell cell5 = row.getCell(5);
                String dateIn = ReadExcelUtil.readCellDate(cell5, rowNumber, "购入日期", false);
                if (i < end && dateIn.isEmpty()){
                    throw new MyException("第" + rowNumber + "行的购入日期不能为空");
                } else {
                    data.put("dateIn",dateIn);
                }

                //解析第六个单元格 不含税金额
                XSSFCell cell6 = row.getCell(6);
                String thisMonthDepreciation = ReadExcelUtil.readCellDecimal(cell6, rowNumber, "本期折旧金额", false, 20, 2);
                data.put("thisMonthDepreciation",thisMonthDepreciation);

                XSSFCell cell7 = row.getCell(7);
                String accumulatedDepreciation = ReadExcelUtil.readCellDecimal(cell7, rowNumber, "累计折旧", false, 20, 2);
                data.put("accumulatedDepreciation",accumulatedDepreciation);

                XSSFCell cell8 = row.getCell(8);
                String thisYearDepreciation = ReadExcelUtil.readCellDecimal(cell8, rowNumber, "本年计提折旧", false, 20, 2);
                data.put("thisYearDepreciation",thisYearDepreciation);

                XSSFCell cell9 = row.getCell(9);
                String thisYearAccumulated = ReadExcelUtil.readCellDecimal(cell9, rowNumber, "本年累计发生", false, 20, 2);
                data.put("thisYearAccumulated",thisYearAccumulated);

                XSSFCell cell10 = row.getCell(10);
                String assetOriginalValue = ReadExcelUtil.readCellDecimal(cell10, rowNumber, "资产原值", false, 20, 2);
                data.put("assetOriginalValue",assetOriginalValue);

                XSSFCell cell11= row.getCell(11);
                String assetNumber = ReadExcelUtil.readCellDecimal(cell11, rowNumber, "资产数量", false, 20, 2);
                data.put("assetNumber",assetNumber);

                XSSFCell cell12 = row.getCell(12);
                String assetNetValue = ReadExcelUtil.readCellDecimal(cell12, rowNumber, "资产净值", false, 20, 2);
                data.put("assetNetValue",assetNetValue);

                XSSFCell cell13 = row.getCell(13);
                String residualValue = ReadExcelUtil.readCellDecimal(cell13, rowNumber, "净残值", false, 20, 2);
                data.put("residualValue",residualValue);

                XSSFCell cell14 = row.getCell(14);
                String orgCode = ReadExcelUtil.readCellFormat(cell14, rowNumber, "单位编码", false, 255);
                data.put("orgCode",orgCode);

                XSSFCell cell15 = row.getCell(15);
                String orgUse = ReadExcelUtil.readCellFormat(cell15, rowNumber, "使用单位", false, 255);
                data.put("orgUse",orgUse);

                XSSFCell cell16 = row.getCell(16);
                String useDepartment = ReadExcelUtil.readCellFormat(cell16, rowNumber, "使用部门", false, 100);
                data.put("useDepartment",useDepartment);


                if(StringUtils.isBlank(financialCode) && StringUtils.isBlank(equipmentName) && StringUtils.isBlank(typeCode)){
                    continue;
                }

                list.add(data);
            }
        }

        return list;
    }

}
