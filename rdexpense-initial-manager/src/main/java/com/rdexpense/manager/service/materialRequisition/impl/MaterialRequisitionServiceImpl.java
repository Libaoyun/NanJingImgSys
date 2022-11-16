package com.rdexpense.manager.service.materialRequisition.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.materialRequisition.MaterialRequisitionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class MaterialRequisitionServiceImpl implements MaterialRequisitionService {


    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;

    @Autowired
    private FileService fileService;



    /**
     * 查询列表
     *
     * @param
     * @return
     */
    @Override
    public List queryList(PageData pd) {

        //查询数据信息
        List<String> pageDataList = JSONArray.parseArray(pd.getString("statusList"), String.class);
        pd.put("processStatusList", pageDataList);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        pd.put("processStatus1", ConstantValUtil.APPROVAL_STATUS[3]);

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

        List<PageData> list = (List<PageData>) dao.findForList("MaterialRequisitionMapper.queryByParams", pd);

        //查询单据的额不含税金额
        if(!CollectionUtils.isEmpty(list)){
            List<PageData> costList = (List<PageData>) dao.findForList("MaterialRequisitionMapper.queryAmountList",list);
            if(!CollectionUtils.isEmpty(costList)){
                for(PageData data : list){
                    String businessId = data.getString("businessId");
                    for(PageData costData : costList){
                        if(costData.getString("businessId").equals(businessId)){
                            data.put("noTaxAmount",costData.getString("amount"));
                        }
                    }
                }
            }

        }




        return list;
    }


    /**
     * 新增领料单
     *
     * @param pd
     * @return
     */
    @Transactional
    public void addMaterialRequisition(PageData pd) {

        String businessId = "YFZC" + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber = SequenceUtil.generateSerialNo("YFZC");//生成流水号

        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);


        String belongMonth = pd.getString("belongMonth");
        if(StringUtils.isNotBlank(belongMonth)){
            pd.put("belongMonth",belongMonth+"-01");
        }

        String billMonth = pd.getString("billMonth");
        if(StringUtils.isNotBlank(billMonth)){
            pd.put("billMonth",billMonth+"-01");
        }

        //1、插入主表
        dao.insert("MaterialRequisitionMapper.insertMain", pd);


        //2、明细
        String detailListStr = pd.getString("detailList");
        List<PageData> detailList = JSONObject.parseArray(detailListStr, PageData.class);
        if (!CollectionUtils.isEmpty(detailList)) {
            for (PageData detailData : detailList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("MaterialRequisitionMapper.batchInsertDetail", detailList);
        }


        // 插入到附件表
        fileService.insert(pd);

    }


    /**
     * 更新领料单
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateMaterialRequisition(PageData pd) {

        String businessId = pd.getString("businessId");
        List<String> removeList = new ArrayList<>();
        removeList.add(businessId);

        String belongMonth = pd.getString("belongMonth");
        if(StringUtils.isNotBlank(belongMonth)){
            pd.put("belongMonth",belongMonth+"-01");
        }

        String billMonth = pd.getString("billMonth");
        if(StringUtils.isNotBlank(billMonth)){
            pd.put("billMonth",billMonth+"-01");
        }

        //1、更新主表
        dao.insert("MaterialRequisitionMapper.updateMain", pd);


        //2、明细
        String detailListStr = pd.getString("detailList");
        dao.batchDelete("MaterialRequisitionMapper.deleteDetail", removeList);
        List<PageData> detailList = JSONObject.parseArray(detailListStr, PageData.class);
        if (!CollectionUtils.isEmpty(detailList)) {
            for (PageData detailData : detailList) {
                detailData.put("businessId", businessId);
            }

            dao.batchInsert("MaterialRequisitionMapper.batchInsertDetail", detailList);
        }


        //编辑附件表
        fileService.update(pd);

    }


    /**
     * 删除领料单
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void deleteMaterialRequisition(PageData pd) {

        //校验不通过的部分
        List<String> removeList = new ArrayList<>();
        String businessIdListStr = pd.getString("businessIdList");

        List<String> businessIdList = JSONObject.parseArray(businessIdListStr, String.class);
        //根据businessIdList查询主表
        List<PageData> recordList = (List<PageData>) dao.findForList("MaterialRequisitionMapper.selectByBusinessId", businessIdList);
        if (!CollectionUtils.isEmpty(recordList)) {
            for (PageData data : recordList) {
                String requestStatus = data.getString("processStatus");
                if (requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0]) || requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[3])) {
                    removeList.add(data.getString("businessId"));
                }
            }
        }


        if (removeList.size() != businessIdList.size()) {
            throw new MyException("只有未提交的单据才能删除");
        }

        pd.put("removeList", removeList);

        //1、删除主表
        dao.batchDelete("MaterialRequisitionMapper.deleteMain", removeList);

        //2、明细
        dao.batchDelete("MaterialRequisitionMapper.deleteDetail", removeList);


        //删除文件
        dao.batchDelete("MaterialRequisitionMapper.batchDeleteFile", removeList);

    }

    /**
     * 查询领料单详情
     *
     * @param pd
     * @return
     */
    @Override
    public PageData queryDetail(PageData pd) {

        //1、查询主表
        PageData request = (PageData) dao.findForObject("MaterialRequisitionMapper.queryDetail", pd);
        if (request != null) {
            // 2、明细
            List<PageData> detailList = (List<PageData>) dao.findForList("MaterialRequisitionMapper.queryDetailList", request);
            if (!CollectionUtils.isEmpty(detailList)) {
                request.put("detailList", detailList);
            }


            // 查询附件表
            fileService.queryFileByBusinessId(request);

        }


        return request;
    }


    /**
     *
     * 导入明细
     * @param file
     * @param pd
     * @return
     */
    @Override
    public PageData upload(MultipartFile file, PageData pd) throws Exception{
        PageData result = new PageData();
        XSSFWorkbook wb = null;
        try {
            wb = getWorkbook(file);
        } catch (Exception e) {
            throw new MyException("请上传正确的模板");
        }
        List<PageData> list = new ArrayList<>();

        //判断有无sheet页
        XSSFSheet sheet = wb.getSheet("发料单");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查发料单sheet页是否正确");
        }

        XSSFRow row = sheet.getRow(1);
        //课题
        XSSFCell cell = row.getCell(1);
        String project = ReadExcelUtil.readCellStr(cell, 2, "课题项目", false, 256);

        if(project == null || !project.equals(pd.getString("projectName"))){
            throw new MyException("模板中的项目名称与页面选择项目名称不一致，请确认后重新上传");
        }
        result.put("billProject",project);

        //月份
        cell = row.getCell(5);
        String billMonth = ReadExcelUtil.readCellMonth(cell, 2, "发料月份", false);

        if(billMonth == null || !billMonth.equals(pd.getString("belongMonth"))){
            throw new MyException("模板中的所属月份与页面选择所属月份不一致，请确认后重新上传");
        }
        result.put("billMonth",billMonth);

        String[] head = {"序号","材料名称","规格","计量单位","数量","不含税单价","不含税金额","备注"};
        checkHead(sheet, head,2);

        //遍历文件
        int end = sheet.getLastRowNum();
        for (int i = 3; i <= end; i++) {
            PageData data = new PageData();
            data.put("paramSource",0);

            row = sheet.getRow(i);

            XSSFCell cell1 = row.getCell(1);
            XSSFCell cell2 = row.getCell(2);
            XSSFCell cell3 = row.getCell(3);
            XSSFCell cell4 = row.getCell(4);
            XSSFCell cell5 = row.getCell(5);
            XSSFCell cell6 = row.getCell(6);
            XSSFCell cell7 = row.getCell(7);

            int rowNumber = i + 1;
            //解析第一个单元格 材料名称
            String materialName = ReadExcelUtil.readCellStr(cell1, rowNumber, "材料名称", false, 256);
            data.put("materialName",materialName);

            //解析第二个单元格 规格
            String specifications = ReadExcelUtil.readCellStr(cell2, rowNumber, "规格", false, 256);
            data.put("specifications",specifications);

            //解析第三个单元格 计量单位
            String unit = ReadExcelUtil.readCellStr(cell3, rowNumber, "计量单位", false, 256);
            data.put("unit",unit);

            //解析第四个单元格 数量
            String number = ReadExcelUtil.readCellDecimal(cell4, rowNumber, "数量", false, 20, 2);
            data.put("number",number);

            //解析第五个单元格 不含税单价
            String noTaxPrice = ReadExcelUtil.readCellDecimal(cell5, rowNumber, "不含税单价", false, 20, 2);
            data.put("noTaxPrice",noTaxPrice);

            //解析第六个单元格 不含税金额
            String noTaxAmount = ReadExcelUtil.readCellDecimal(cell6, rowNumber, "不含税金额", false, 20, 2);
            data.put("noTaxAmount",noTaxAmount);

            //解析第七个单元格 备注
            String remark = ReadExcelUtil.readCellStr(cell7, rowNumber, "备注", false, 256);
            data.put("remark",remark);


            if(StringUtils.isBlank(materialName) && StringUtils.isBlank(specifications) && StringUtils.isBlank(unit)){
                continue;
            }

            list.add(data);


        }

        result.put("detailList",list);

        return result;


    }


    /**
     * 判断表头是否正确
     * @param sheet
     * @param head
     */
    private void checkHead(XSSFSheet sheet,String[] head,int rowNum){
        XSSFRow row  = sheet.getRow(rowNum);
        //判断表头是否正确
        for (int i = 0; i < head.length; i++) {
            if (!row.getCell(i).getStringCellValue().trim().equals(head[i])) {
                throw new MyException(ConstantMsgUtil.ERR_FILE_HEAD.desc());
            }
        }
    }


    /**
     * 生成支出申请单
     * @param pd
     * @return
     */

    @Override
    public PageData generateApply(PageData pd) {

        //先查出当前的记录，获取到项目信息
        PageData materialRequisitionData = (PageData) dao.findForObject("MaterialRequisitionMapper.queryDetail",pd);
        if(materialRequisitionData == null){
            throw new MyException("该条单据不存在");
        }
        //查询不含税总额
        PageData costData = (PageData) dao.findForObject("MaterialRequisitionMapper.queryAmountData",materialRequisitionData);

        if(costData != null){
            materialRequisitionData.put("amount",costData.getString("amount"));
        }else {
            materialRequisitionData.put("amount","0.00");
        }

        PageData projectData = (PageData) dao.findForObject("MaterialRequisitionMapper.queryProject",materialRequisitionData);
        if(projectData != null){
            materialRequisitionData.put("businessId",projectData.getString("businessId"));
            materialRequisitionData.put("bureauLevel",projectData.getString("bureauLevel"));
            materialRequisitionData.put("startYear",projectData.getString("startYear"));
            materialRequisitionData.put("endYear",projectData.getString("endYear"));
        }

        return materialRequisitionData;
    }




    private XSSFWorkbook getWorkbook(MultipartFile file) throws Exception {
        XSSFWorkbook wb = null;
        String fileName = file.getOriginalFilename();
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());

        //判断文件格式
        if (fileName.endsWith("xlsx") || fileName.endsWith("xls")) {
            wb = new XSSFWorkbook(inputStream);
        } else {
            throw new MyException(ConstantMsgUtil.ERR_FILE_TYPE.desc());
        }

        return wb;
    }



    @Override
    public void exportPDF(PageData pd, Document document) throws Exception {

        // 中文字体,解决中文不能显示问题
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font keyfont = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font textfont = new com.itextpdf.text.Font(bfChinese, 10, com.itextpdf.text.Font.NORMAL);
        // 设置标题
        Paragraph blankRow1 = new Paragraph("研发项目领料单", keyfont);
        blankRow1.setAlignment(Element.ALIGN_CENTER);
        document.add(blankRow1);

        PageData data = queryDetail(pd);
        List<PageData> detailList = (List<PageData>)data.get("detailList");

        //创建一个表格,8为一行有几栏

        PdfPTable table1 = new PdfPTable(6);
        table1.setSpacingBefore(10f);
        table1.setWidthPercentage(100);
        int[] width1 = {150, 300, 150, 300, 150, 300};//每栏的宽度
        table1.setWidths(width1); //设置宽度

        String[] argArr1 = {"单据编号", data.getString("serialNumber"), "项目名称", data.getString("projectName"), "申请日期", data.getString("applyDate")};
        for (String arg : argArr1) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell);
        }


        PdfPTable table2 = new PdfPTable(6);
        table2.setSpacingBefore(10f);
        table2.setWidthPercentage(100);
        int[] width2 = {150, 300, 150, 300, 150, 300};//每栏的宽度
        table2.setWidths(width2); //设置宽度

        String[] argArr2 = {"项目负责人", data.getString("projectLeader"), "创建人", data.getString("creatorUserName"), "创建时间", data.getString("createdDate")};
        for (String arg : argArr2) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(cell);
        }

        PdfPTable table3 = new PdfPTable(6);
        table3.setSpacingBefore(10f);
        table3.setWidthPercentage(100);
        int[] width3 = {150, 300, 150, 300, 150, 300};//每栏的宽度
        table3.setWidths(width3); //设置宽度

        String[] argArr3 = {"所属月份", data.getString("belongMonth"),"备注", data.getString("remark"),"","","",""};
        for (String arg : argArr3) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
        }


        //设置明细标题，并合并单元格
        PdfPTable table4 = new PdfPTable(8);
        table4.setSpacingBefore(10f);
        //每栏的宽度
        int[] width4 = {100, 200, 200, 200, 200, 200, 200, 200};
        //设置宽度
        table4.setWidths(width4);
        table4.setWidthPercentage(100);
        PdfPCell keyCell = new PdfPCell(new Paragraph("领料单明细", textfont));
        keyCell.setMinimumHeight(20);
        keyCell.setRowspan(1);
        keyCell.setColspan(16);
        keyCell.setHorizontalAlignment(Element.ALIGN_CENTER);//水平居中
        table4.addCell(keyCell);


        String[] argArr4 = {"序号", "材料名称", "规格型号", "计量单位", "数量","不含税单价(元)", "不含税金额(元)","备注"};
        for (String arg : argArr4) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);
        }

        BigDecimal total = new BigDecimal(0);
        //查询明细数据
        if(!CollectionUtils.isEmpty(detailList)){
            for (int i = 0; i < detailList.size(); i++) {
                PageData detailData = detailList.get(i);

                if(StringUtils.isNotBlank(detailData.getString("noTaxAmount"))){
                    total = total.add(new BigDecimal(detailData.getString("noTaxAmount")));
                }
                String[] argArr6 =
                        {
                                String.valueOf(i + 1),//明细表序号
                                detailData.getString("materialName"),
                                detailData.getString("specifications"),
                                detailData.getString("unit"),
                                detailData.getString("number"),
                                detailData.getString("noTaxPrice"),
                                detailData.getString("noTaxAmount"),
                                detailData.getString("remark")
                        };
                for (String arg : argArr6) {
                    PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
                    cell.setMinimumHeight(20);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table4.addCell(cell);
                }
            }

        }

        String[] argArr6 =
                {
                        "合计",
                        "",
                        "",
                        "",
                        "",
                        "",
                        total.toString(),
                        ""
                };
        for (String arg : argArr6) {
            PdfPCell cell = new PdfPCell(new Paragraph(arg, textfont));
            cell.setMinimumHeight(20);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table4.addCell(cell);
        }


        //将表格二，表格三合入表格一中
        PDFUtil.mergeTable(table1,table2,table3,table4);

        // 添加表格
        document.add(table1);
    }

    /**
     * 导出压缩包
     * @param businessIdList
     * @param zos
     * @param filePrefix
     * @throws Exception
     */
    @Override
    public void exportZip(List<String> businessIdList, ZipOutputStream zos, String filePrefix) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        for(String businessId : businessIdList){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Document document = new Document(new RectangleReadOnly(842, 595));
            PdfWriter.getInstance(document, bos);
            document.open();
            PageData data = new PageData();
            data.put("businessId",businessId);
            exportPDF(data,document);

            document.close();
            String number = SerialNumberUtil.generateSerialNo("materialRequisitionPdf");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
            DownloadUtil.zipFile(filePrefix + date + "_" + number + ".pdf", inputStream, zos);
            inputStream.close();
        }


    }


    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "研发项目领料单";
        String[] head = {"序号","单据编号","项目名称","项目负责人","所属月份","申请日期","材料名称","规格","计量单位","数量",
                "不含税单价（元)","不含税金额（元）","备注","编制人","创建日期","更新日期"};
        String businessIdStr = pageData.getString("businessIdList");
        List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);
        //根据idList查询主表及明细表
        List<PageData> userInfoList = (List<PageData>) dao.findForList("MaterialRequisitionMapper.queryByBusinessIdList", businessIdList);


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
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length-1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }

        int len = userInfoList.size();
        BigDecimal total = new BigDecimal(0);
        if(!CollectionUtils.isEmpty(userInfoList)){
            for(int i=0;i<len;i++){
                PageData pd = userInfoList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("serialNumber"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectLeader"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String belongMonth = pd.getString("belongMonth");
                cell.setCellValue(belongMonth.substring(0,belongMonth.lastIndexOf("-")));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("applyDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("materialName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("specifications"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("unit"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("number"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("noTaxPrice"));
                cell.setCellStyle(styleCell);

                String noTaxAmount = pd.getString("noTaxAmount");
                cell = row.createCell(j++);
                cell.setCellValue(noTaxAmount);
                cell.setCellStyle(styleCell);

                if(StringUtils.isNotBlank(noTaxAmount)){
                    total = total.add(new BigDecimal(noTaxAmount));
                }

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("remark"));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("creatorUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time = pd.getString("createTime");
                cell.setCellValue(time.substring(0,time.lastIndexOf(".")));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time1 = pd.getString("updateTime");
                cell.setCellValue(time1.substring(0,time1.lastIndexOf(".")));
                cell.setCellStyle(styleCell);


            }
        }

        HSSFRow row = sheet.createRow(len + 2);
        for(int i=0;i<16;i++){
            HSSFCell cell = row.createCell(i);
            if(i == 0){
                cell.setCellValue("合计");
            }else if(i==11){
                cell.setCellValue(total.toString());
            }else {
                cell.setCellValue("");
            }

            cell.setCellStyle(styleCell);
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




}
