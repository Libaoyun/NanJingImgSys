package com.rdexpense.manager.service.analysis.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.rdexpense.manager.service.analysis.ExpensesBillService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class ExpensesBillServiceImpl implements ExpensesBillService {


    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;


    private static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    /**
     * 查询列表
     *
     * @param
     * @return
     */
    @Override
    public List queryList(PageData pd) {

        //查询数据信息
        List<String> completionStatusCode = JSONArray.parseArray(pd.getString("completionStatusCode"), String.class);
        pd.put("completionStatusCodeList", completionStatusCode);

        List<String> expensesTypeCode = JSONArray.parseArray(pd.getString("expensesTypeCode"), String.class);
        pd.put("expensesTypeCodeList", expensesTypeCode);

        //处理日期搜索条件
        String createTime = pd.getString("createTime");
        if(StringUtils.isNotBlank(createTime)){
            JSONArray createTimeArr = JSONArray.parseArray(createTime);
            pd.put("beginCreateTime",createTimeArr.get(0) + " 00:00:00");
            pd.put("lastCreateTime",createTimeArr.get(1) + " 23:59:59");
        }

        List<PageData> list = (List<PageData>) dao.findForList("ExpensesBillMapper.queryByParams", pd);

        //查询金额的合计
        if(!CollectionUtils.isEmpty(list)){
            List<PageData> amountList = (List<PageData>) dao.findForList("ExpensesBillMapper.queryAmountList",list);
            if(!CollectionUtils.isEmpty(amountList)){
                for(PageData data : list){
                    String businessId = data.getString("businessId");
                    for(PageData costData : amountList){
                        if(costData.getString("businessId").equals(businessId)){
                            data.put("amount",costData.getString("amount"));
                        }
                    }
                }
            }
        }

        return list;
    }



    /**
     * 删除
     *
     * @param
     * @return
     */
    @Override
    @Transactional
    public void deleteApply(PageData pd) {


        String businessIdListStr = pd.getString("businessIdList");

        List<String> businessIdList = JSONObject.parseArray(businessIdListStr, String.class);


        List<String> removeList = new ArrayList<>();

        //根据businessIdList查询主表
        List<PageData> recordList = (List<PageData>) dao.findForList("ExpensesBillMapper.selectByBusinessId", businessIdList);
        if (!CollectionUtils.isEmpty(recordList)) {
            for (PageData data : recordList) {
                String creatorUserId = data.getString("creatorUserId");
                if (creatorUserId.equals(pd.getString("creatorUserId"))) {
                    removeList.add(data.getString("businessId"));
                }
            }
        }


        if (removeList.size() != businessIdList.size()) {
            throw new MyException("只能删除自己上传的辅助账");
        }


        //1、删除主表
        dao.batchDelete("ExpensesBillMapper.deleteMain", businessIdList);

        //2、删除明细表
        dao.batchDelete("ExpensesBillMapper.deleteDetail", businessIdList);


    }


    /**
     * 导入数据
     *
     * @param file
     * @param pd
     * @throws Exception
     */
    @Override
    @Transactional
    public void uploadAll(MultipartFile file, PageData pd) {

        String businessId = "ZCTZ" + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber = SequenceUtil.generateSerialNo("ZCTZ");//生成流水号

        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("creatorUserId",pd.getString("createUserId"));
        pd.put("creatorUserName",pd.getString("createUser"));


        XSSFWorkbook wb = null;
        try {
            wb = getWorkbook(file);
        } catch (Exception e) {
            throw new MyException("请上传正确的支出辅助账模板");
        }

        XSSFSheet sheet = wb.getSheet("支出辅助账");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查sheet页是否正确");
        }

        //1、读取主表信息
        List<String> dicList = new ArrayList<>();
        dicList.add("1027");//完成情况
        dicList.add("1028");//项支出类型

        List<PageData> dicTypeList = (List<PageData>) dao.findForList("DictionaryMapper.getDicValue", dicList);
        Map<String, String> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(dicTypeList)) {
            for (PageData dic : dicTypeList) {
                map.put(dic.getString("dicEnumName"), dic.getString("dicEnumId"));
            }
        }

        //1、年份
        XSSFRow row = sheet.getRow(0);
        XSSFCell cell = row.getCell(0);
        String years = ReadExcelUtil.readCellStr(cell, 2, "年份", false, 256);
        if(years.length() >4){
            years = years.substring(0,4);
        }
        try {
            yearFormat.parse(years);
        } catch (Exception e) {
            throw new MyException("模板标题错误，必须以年份数字开头");
        }
        pd.put("years",years);

        //2、读取项目编号
        row = sheet.getRow(1);
        cell = row.getCell(3);
        String projectCode = ReadExcelUtil.readCellStr(cell, 2, "项目编号", true, 256);
        pd.put("projectCode",projectCode);

        //3、读取项目名称
        cell = row.getCell(6);
        String projectName = ReadExcelUtil.readCellStr(cell, 2, "项目名称", true, 256);
        pd.put("projectName",projectName);

        if(StringUtils.isNotBlank(projectName)){
            //判断该项目是不是，系统中已完成的项目
            List<PageData> list1 = (List<PageData>) dao.findForList("ExpensesBillMapper.queryProjectByName",pd);
            if(CollectionUtils.isEmpty(list1)){
                throw new MyException("该项目不存在或未审批完成，请确认后重新上传");
            }else {
                PageData data = list1.get(0);
                int startYear = Integer.valueOf(data.getString("startYear"));
                int endYear = Integer.valueOf(data.getString("endYear"));

                if(Integer.valueOf(years) < startYear || Integer.valueOf(years) > endYear){
                    throw new MyException("模板中的年份不在该项目的周期内");
                }
            }

            //判断该项目是不是，已上传相同年份的数据
            List<PageData> list2 = (List<PageData>) dao.findForList("ExpensesBillMapper.queryProjectByNameAndYear",pd);
            if(!CollectionUtils.isEmpty(list2)){
                throw new MyException("该项目该年度的研发支出辅助账已存在，请确认后重新上传");
            }
        }

        //4、完成情况
        cell = row.getCell(8);
        String completionStatus = ReadExcelUtil.readCellStr(cell, 5, "完成情况", true, 256);
        pd.put("completionStatus",completionStatus);
        pd.put("completionStatusCode",getCode(completionStatus,map));

        //5、支出类型
        cell = row.getCell(10);
        String expensesType = ReadExcelUtil.readCellFormat(cell, 5, "支出类型", true, 256);
        pd.put("expensesType",expensesType);
        pd.put("expensesTypeCode",getCode(expensesType,map));

        int end = sheet.getLastRowNum();

        //6、会计主管
        row = sheet.getRow(end);
        cell = row.getCell(1);
        String accountingSupervisor = ReadExcelUtil.readCellFormat(cell, end+1, "会计主管", false, 256);
        pd.put("accountingSupervisor",accountingSupervisor);

        //7、读取金额明细
        List<PageData> list = new ArrayList<>();
        //遍历文件

        for (int i = 5; i <= end-2; i++) {
            PageData data = new PageData();
            data.put("businessId", businessId);

            row = sheet.getRow(i);
            XSSFCell cell0 = row.getCell(0);
            XSSFCell cell1 = row.getCell(1);
            XSSFCell cell2 = row.getCell(2);
            XSSFCell cell3 = row.getCell(3);
            XSSFCell cell4 = row.getCell(4);
            XSSFCell cell5 = row.getCell(5);
            XSSFCell cell6 = row.getCell(6);
            XSSFCell cell7 = row.getCell(7);
            XSSFCell cell8 = row.getCell(8);
            XSSFCell cell9 = row.getCell(9);
            XSSFCell cell10 = row.getCell(10);
            XSSFCell cell11 = row.getCell(11);
            XSSFCell cell12 = row.getCell(12);
            XSSFCell cell13 = row.getCell(13);

            int rowNumber = i + 1;
            //解析第一个单元格 日期
            String expensesDate = ReadExcelUtil.readCellMonth(cell0, rowNumber, "日期", true);
            data.put("expensesDate",expensesDate);

            //解析第二个单元格 种类
            String classify = ReadExcelUtil.readCellStr(cell1, rowNumber, "种类", true, 255);
            data.put("classify",classify);

            //解析第三个单元格 号数
            String numbers = ReadExcelUtil.readCellStr(cell2, rowNumber, "号数", true,255);
            data.put("numbers",numbers);

            //解析第四个单元格 摘要
            String abstracts = ReadExcelUtil.readCellStr(cell3, rowNumber, "摘要", true, 1024);
            data.put("abstracts",abstracts);

            //解析第五个单元格 会计凭证记载金额
            String recordAmount = ReadExcelUtil.readCellDecimal(cell4, rowNumber, "会计凭证记载金额", true,20,2);
            data.put("recordAmount",recordAmount);

            //解析第六个单元格 税法规定的归集金额
            String collectionAmount = ReadExcelUtil.readCellDecimal(cell5, rowNumber, "税法规定的归集金额", true,20,2);
            data.put("collectionAmount",collectionAmount);

            //解析第七个单元格 人员人工费用
            String userAmount = ReadExcelUtil.readCellDecimal(cell6, rowNumber, "人员人工费用", true,20,2);
            data.put("userAmount",userAmount);

            //解析第八个单元格 直接投入费用
            String inputAmount = ReadExcelUtil.readCellDecimal(cell7, rowNumber, "直接投入费用", true,20,2);
            data.put("inputAmount",inputAmount);

            //解析第九个单元格 折旧费用
            String depreciationAmount = ReadExcelUtil.readCellDecimal(cell8, rowNumber, "折旧费用", true,20,2);
            data.put("depreciationAmount",depreciationAmount);

            //解析第十个单元格 无形资产摊销
            String amortizationAmount = ReadExcelUtil.readCellDecimal(cell9, rowNumber, "无形资产摊销", true,20,2);
            data.put("amortizationAmount",amortizationAmount);

            //解析第十一个单元格 新产品设计费等
            String desginAmount = ReadExcelUtil.readCellDecimal(cell10, rowNumber, "新产品设计费等", true,20,2);
            data.put("desginAmount",desginAmount);

            //解析第十二个单元格 其他相关费用
            String otherAmount = ReadExcelUtil.readCellDecimal(cell11, rowNumber, "其他相关费用", true,20,2);
            data.put("otherAmount",otherAmount);

            //解析第十三个单元格 境内活动费
            String territoryAmount = ReadExcelUtil.readCellDecimal(cell12, rowNumber, "境内活动费", true,20,2);
            data.put("territoryAmount",territoryAmount);

            //解析第十四个单元格 境外活动费
            String abroadAmount = ReadExcelUtil.readCellDecimal(cell13, rowNumber, "境外活动费", true,20,2);
            data.put("abroadAmount",abroadAmount);

            if(StringUtils.isBlank(expensesDate) && StringUtils.isBlank(classify) && StringUtils.isBlank(numbers) && StringUtils.isBlank(abstracts)){
                continue;
            }

            list.add(data);


        }


        //将数据插入数据库
        //1、插入主表
        dao.insert("ExpensesBillMapper.insertMain", pd);


        //3、插入明细表
        if (!CollectionUtils.isEmpty(list)) {
            dao.batchInsert("ExpensesBillMapper.batchDetailList", list);

        }



    }






    private XSSFWorkbook getWorkbook(MultipartFile file){
        XSSFWorkbook wb = null;
        String fileName = file.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(file.getBytes());
            //判断文件格式
            if (fileName.endsWith("xlsx") || fileName.endsWith("xls")) {
                wb = new XSSFWorkbook(inputStream);

            } else {
                throw new MyException(ConstantMsgUtil.ERR_FILE_TYPE.desc());
            }

        } catch (IOException e) {
            throw new MyException("请上传正确的立项模板");
        }


        return wb;
    }





    /**
     * 根据名称查询字典表的编码
     * @param name
     * @param map
     * @return
     */
    private String getCode(String name ,Map<String, String> map){
        String code = null;
        if(map != null && map.get(name) != null){
            code = map.get(name);
        }

        return code;


    }



    /**
     * 导出压缩包
     * @param flag 文件类型标识 1:excel 2:pdf
     * @param businessIdList
     * @param zos
     * @param bos
     * @param filePrefix
     * @throws Exception
     */
    @Override
    public void exportZip(int flag,List<String> businessIdList, ZipOutputStream zos, ByteArrayOutputStream bos, String filePrefix) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        XwpfTUtil xwpfTUtil = new XwpfTUtil();

        if(flag == 1){//导出excel

            for(String businessId : businessIdList){
                HSSFWorkbook wb = exportExcel(businessId);
                String number = SerialNumberUtil.generateSerialNo("expensesBillExcel");
                //文件名
                String fileName = filePrefix + date + "_" + number + ".xls";
                //重点开始,创建压缩文件
                //后端生成文件,可直接put
                ZipEntry z = new ZipEntry(fileName);
                zos.putNextEntry(z);
                wb.write(zos);
            }


        }else if(flag == 2){//导出pdf

            for(String businessId : businessIdList){

                String number = SerialNumberUtil.generateSerialNo("expensesBillPdf");

                String wordFileName = filePrefix +date+"_"+number+ ".docx";
                String pdfFileName = filePrefix +date+"_"+number+".pdf";

                File file = FileUtil.createFile();
                XWPFDocument doc = null;
                InputStream is = null;

                try {
                    is = this.getClass().getResourceAsStream("/template/研发支出辅助账.docx");
                    doc = new XWPFDocument(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    File newWordTempFile = packageWord( businessId, doc, is,wordFileName,file);

                    String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                    File pdfTempFile = new File(file, pdfFileName);
                    String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                    //下载到临时word文件中 
                    FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                    doc.write(os);
                    xwpfTUtil.close(is);
                    xwpfTUtil.close(os);
                    //将word转为pdf
                    Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);

                    //将数据写入到zip流中
                    //已读出图片
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(getBytes(pdfTempFilePath));
                    DownloadUtil.zipFile(pdfFileName, inputStream, zos);
                    inputStream.close();
                    //删除临时文件
                    newWordTempFile.delete();
                    pdfTempFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }



        }

    }


    private static byte[] getBytes(String filePath){
        ByteArrayOutputStream out = null;
        File file = new File(filePath);
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i=0;
            while ((i = in.read(b)) != -1){
                out.write(b,0,b.length);

            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


        byte[] result = out.toByteArray();

        return result;

    }


    /**
     * 导出pdf
     * @param businessId
     */

    @Override
    public void exportPdf(String businessId,HttpServletResponse response,String filePrefix) {
        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String number = SerialNumberUtil.generateSerialNo("expensesBillPdf");
        String wordFileName = filePrefix +date+"_"+number+ ".docx";
        String pdfFileName = filePrefix +date+"_"+number+ ".pdf";

        File file = FileUtil.createFile();
        XWPFDocument doc = null;
        InputStream is = null;
        try {
            is = this.getClass().getResourceAsStream("/template/研发支出辅助账.docx");
            doc = new XWPFDocument(is);


            File newWordTempFile = packageWord( businessId, doc, is,wordFileName,file);


            String newWordTempFilePath = newWordTempFile.getCanonicalPath();
            File pdfTempFile = new File(file, pdfFileName);
            String pdfTempFilePath = pdfTempFile.getCanonicalPath();

            //下载到临时word文件中 
            FileOutputStream os = new FileOutputStream(newWordTempFilePath);
            doc.write(os);
            xwpfTUtil.close(is);
            xwpfTUtil.close(os);
            //将word转为pdf
            Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);
            //输出pdf文件
            Word2pdfUtil.fileResponse(pdfTempFilePath, response);
            //删除临时文件
            newWordTempFile.delete();
            pdfTempFile.delete();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private File packageWord(String businessId,XWPFDocument doc,InputStream is,String fileName,File file){

        PageData data = getDetail(businessId);
        List<PageData> detailList = (List<PageData>)data.get("detailList");

        XwpfTUtil xwpfTUtil = new XwpfTUtil();

        try {
            //文本
            Map<String, Object> params = new HashMap<>();
            //1、主信息
            params.put("(years)", data.getString("years"));
            params.put("(projectName)", data.getString("projectName"));
            params.put("(projectCode)", data.getString("projectCode"));
            params.put("(completionStatus)", data.getString("completionStatus"));
            params.put("(expensesType)", data.getString("expensesType"));
            params.put("(accountingSupervisor)", data.getString("accountingSupervisor"));
            params.put("(createUser)", data.getString("createUser"));
            //替换掉文档中对应的字段
            xwpfTUtil.replaceInPara(doc, params);

            ExportWordHelper exportWordHelper = new ExportWordHelper();
            //4、进度计划
            int len = 0;
            List<List<String>> detailListStr = new ArrayList<>();
            if(!CollectionUtils.isEmpty(detailList)){
                len = detailList.size();
                for(PageData progress : detailList){
                    List<String> progressDetailList = new ArrayList<>();
                    progressDetailList.add(progress.getString("expensesDate"));
                    progressDetailList.add(progress.getString("classify"));
                    progressDetailList.add(progress.getString("numbers"));
                    progressDetailList.add(progress.getString("abstracts"));
                    progressDetailList.add(progress.getString("recordAmount"));
                    progressDetailList.add(progress.getString("collectionAmount"));
                    progressDetailList.add(progress.getString("userAmount"));
                    progressDetailList.add(progress.getString("inputAmount"));
                    progressDetailList.add(progress.getString("depreciationAmount"));
                    progressDetailList.add(progress.getString("amortizationAmount"));
                    progressDetailList.add(progress.getString("desginAmount"));
                    progressDetailList.add(progress.getString("otherAmount"));
                    progressDetailList.add(progress.getString("territoryAmount"));
                    progressDetailList.add(progress.getString("abroadAmount"));

                    detailListStr.add(progressDetailList);

                }

            }


            List<String> progressDetailList = new ArrayList<>();
            progressDetailList.add("合计金额");
            progressDetailList.add("");
            progressDetailList.add("");
            progressDetailList.add("");
            progressDetailList.add(data.getString("recordAmount"));
            progressDetailList.add(data.getString("collectionAmount"));
            progressDetailList.add(data.getString("userAmount"));
            progressDetailList.add(data.getString("inputAmount"));
            progressDetailList.add(data.getString("depreciationAmount"));
            progressDetailList.add(data.getString("amortizationAmount"));
            progressDetailList.add(data.getString("desginAmount"));
            progressDetailList.add(data.getString("otherAmount"));
            progressDetailList.add(data.getString("territoryAmount"));
            progressDetailList.add(data.getString("abroadAmount"));

            detailListStr.add(progressDetailList);

            exportWordHelper.exportWordDisorder(detailListStr, doc, 0);

            exportWordHelper.mergeCellsVertically(doc,0,0,3,len+4);

            exportWordHelper.setCellStyle(doc,0,0,len+4);
            exportWordHelper.setCellStyle(doc,0,0,1);
            exportWordHelper.setCellStyle(doc,0,3,1);

            File newWordTempFile = new File(file, fileName);

            return newWordTempFile;

        } catch (Exception e) {
            xwpfTUtil.close(is);
            throw new MyException("辅助账导出异常");

        }
    }


    /**
     * 导出Excel
     *
     * @param businessId
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(String businessId) {
        //查询单据的所有数据

        PageData data = getDetail(businessId);
        String title = data.getString("years")+"年研发支出辅助账";
        List<PageData> detailList = (List<PageData>) data.get("detailList");

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("支出辅助账");
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setWrapCell(wb, sheet);// 单元格
        // 创建标题
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, 13);


        //创建第二行
        //1、项目编码
        rows = sheet.createRow(1);
        cells = rows.createCell(0);
        cells.setCellValue("项目编码:");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 0, 2);

        cells = rows.createCell(3);
        cells.setCellValue(data.getString("projectCode"));
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 1, 1, 3, 4);

        //2、项目名称
        cells = rows.createCell(5);
        cells.setCellValue("项目名称:");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(6);
        cells.setCellValue(data.getString("projectName"));
        cells.setCellStyle(styleCell);


        //3、项目名称
        cells = rows.createCell(7);
        cells.setCellValue("完成情况:");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(8);
        cells.setCellValue(data.getString("completionStatus"));
        cells.setCellStyle(styleCell);

        //4、支出类型
        cells = rows.createCell(9);
        cells.setCellValue("支出情况:");
        cells.setCellStyle(styleHeader);

        cells = rows.createCell(10);
        cells.setCellValue(data.getString("expensesType"));
        cells.setCellStyle(styleCell);


        //
        cells = rows.createCell(11);
        cells.setCellValue("金额单位：元");
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 1, 1, 11, 13);


        //创建第三行
        rows = sheet.createRow(2);
        cells = rows.createCell(0);
        cells.setCellValue("凭证信息");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 2, 3, 0, 3);

        cells = rows.createCell(4);
        cells.setCellValue("会计凭证记载金额");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(5);
        cells.setCellValue("税法规定的归集金额");
        cells.setCellStyle(styleCell);



        cells = rows.createCell(6);
        cells.setCellValue("费用明细（税法规定）");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 2, 2, 6, 13);


        //创建第四行
        rows = sheet.createRow(3);
        cells = rows.createCell(6);
        cells.setCellValue("人员人工费用");
        cells.setCellStyle(styleCell);



        cells = rows.createCell(7);
        cells.setCellValue("直接投入费用");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(8);
        cells.setCellValue("折旧费用");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(9);
        cells.setCellValue("无形资产摊销");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(10);
        cells.setCellValue("新产品设计费等");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(11);
        cells.setCellValue("其他相关费用");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(12);
        cells.setCellValue("委托研发费用");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 3, 3, 12, 13);




        //创建第五行
        rows = sheet.createRow(4);
        cells = rows.createCell(0);
        cells.setCellValue("日期");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(1);
        cells.setCellValue("种类");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(2);
        cells.setCellValue("号数");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(3);
        cells.setCellValue("摘要");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(12);
        cells.setCellValue("委托境内机构或个人进行研发活动所发生的费用");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(13);
        cells.setCellValue("委托境外机构进行研发活动所发生的费用");
        cells.setCellStyle(styleCell);




        ExcelUtil.merge(wb, sheet, 2, 4, 4, 4);
        ExcelUtil.merge(wb, sheet, 2, 4, 5, 5);


        ExcelUtil.merge(wb, sheet, 3, 4, 6, 6);
        ExcelUtil.merge(wb, sheet, 3, 4, 7, 7);
        ExcelUtil.merge(wb, sheet, 3, 4, 8, 8);
        ExcelUtil.merge(wb, sheet, 3, 4, 9, 9);

        ExcelUtil.merge(wb, sheet, 3, 4, 10, 10);

        ExcelUtil.merge(wb, sheet, 3, 4, 11, 11);


        int len = 0;
        if(!CollectionUtils.isEmpty(detailList)){
            len = detailList.size();
            for(int i=0;i<detailList.size();i++){
                PageData pd = detailList.get(i);

                HSSFRow row = sheet.createRow(i + 5);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(pd.getString("expensesDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("classify"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("numbers"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("abstracts"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("recordAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("collectionAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("inputAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("depreciationAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("amortizationAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("desginAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("otherAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("territoryAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("abroadAmount"));
                cell.setCellStyle(styleCell);

            }
        }

        HSSFRow row = sheet.createRow(len + 5);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("合计金额");
        cell.setCellStyle(styleCell);

        ExcelUtil.merge(wb, sheet, len + 5, len +5, 0, 3);


        cell = row.createCell(4);
        cell.setCellValue(data.getString("recordAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(5);
        cell.setCellValue(data.getString("collectionAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(6);
        cell.setCellValue(data.getString("userAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(7);
        cell.setCellValue(data.getString("inputAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(8);
        cell.setCellValue(data.getString("depreciationAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(9);
        cell.setCellValue(data.getString("amortizationAmount"));
        cell.setCellStyle(styleCell);


        cell = row.createCell(10);
        cell.setCellValue(data.getString("desginAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(11);
        cell.setCellValue(data.getString("otherAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(12);
        cell.setCellValue(data.getString("territoryAmount"));
        cell.setCellStyle(styleCell);

        cell = row.createCell(13);
        cell.setCellValue(data.getString("abroadAmount"));
        cell.setCellStyle(styleCell);


        row = sheet.createRow(len + 6);
        cell = row.createCell(0);
        cell.setCellValue("会计主管：");

        cell = row.createCell(1);
        cell.setCellValue(data.getString("accountingSupervisor"));


        cell = row.createCell(10);
        cell.setCellValue("录入人：");


        cell = row.createCell(11);
        cell.setCellValue(data.getString("createUser"));


        sheet.setAutobreaks(true);


        return wb;
    }



    private PageData getDetail(String businessId){
        PageData pd = new PageData();
        pd.put("businessId",businessId);

        PageData data = (PageData) dao.findForObject("ExpensesBillMapper.queryByBusinessId",pd);

        if(data != null){
            List<PageData> detailList = (List<PageData>) dao.findForList("ExpensesBillMapper.queryDetailByBusinessId",pd);
            data.put("detailList",detailList);
            BigDecimal recordAmount = new BigDecimal(0);
            BigDecimal collectionAmount = new BigDecimal(0);
            BigDecimal userAmount = new BigDecimal(0);
            BigDecimal inputAmount = new BigDecimal(0);
            BigDecimal depreciationAmount = new BigDecimal(0);
            BigDecimal amortizationAmount = new BigDecimal(0);
            BigDecimal desginAmount = new BigDecimal(0);
            BigDecimal otherAmount = new BigDecimal(0);
            BigDecimal territoryAmount = new BigDecimal(0);
            BigDecimal abroadAmount = new BigDecimal(0);

            if(!CollectionUtils.isEmpty(detailList)){
                for(PageData data1: detailList){
                    recordAmount = recordAmount.add(new BigDecimal(data1.getString("recordAmount")));
                    collectionAmount = collectionAmount.add(new BigDecimal(data1.getString("collectionAmount")));
                    userAmount = userAmount.add(new BigDecimal(data1.getString("userAmount")));
                    inputAmount = inputAmount.add(new BigDecimal(data1.getString("inputAmount")));
                    depreciationAmount = depreciationAmount.add(new BigDecimal(data1.getString("depreciationAmount")));
                    amortizationAmount = amortizationAmount.add(new BigDecimal(data1.getString("amortizationAmount")));
                    desginAmount = desginAmount.add(new BigDecimal(data1.getString("desginAmount")));
                    otherAmount = otherAmount.add(new BigDecimal(data1.getString("otherAmount")));
                    territoryAmount = territoryAmount.add(new BigDecimal(data1.getString("territoryAmount")));
                    abroadAmount = abroadAmount.add(new BigDecimal(data1.getString("abroadAmount")));
                }
            }

            data.put("recordAmount",recordAmount.toString());
            data.put("collectionAmount",collectionAmount.toString());
            data.put("userAmount",userAmount.toString());
            data.put("inputAmount",inputAmount.toString());
            data.put("depreciationAmount",depreciationAmount.toString());
            data.put("amortizationAmount",amortizationAmount.toString());
            data.put("desginAmount",desginAmount.toString());
            data.put("otherAmount",otherAmount.toString());
            data.put("territoryAmount",territoryAmount.toString());
            data.put("abroadAmount",abroadAmount.toString());

        }




        return data;

    }

}
