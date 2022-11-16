package com.rdexpense.manager.service.disclosurePaper.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.rdexpense.manager.service.disclosurePaper.DisclosurePaperService;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.progressreport.impl.ProgressReportServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.INFO_EXPORT_SUCCESS;

@Service("DisclosurePaperService")
public class DisclosurePaperServiceImp implements DisclosurePaperService {
    private static final Logger logger = LoggerFactory.getLogger(ProgressReportServiceImpl.class);



    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    private FileService fileService;

    @Override
    public List<PageData> queryDisclosurePaperList(PageData pd) {
        String createTime = pd.getString("createTime");
        if(StringUtils.isNotBlank(createTime)){
            JSONArray createTimeArr = JSONArray.parseArray(createTime);
            pd.put("beginCreateTime",createTimeArr.get(0) + " 00:00:00");
            pd.put("lastCreateTime",createTimeArr.get(1) + " 11:59:59");
        }

        String updateTime = pd.getString("updateTime");
        if(StringUtils.isNotBlank(updateTime)){
            JSONArray updateTimeArr = JSONArray.parseArray(updateTime);
            pd.put("beginUpdateTime",updateTimeArr.get(0) + " 00:00:00");
            pd.put("lastUpdateTime",updateTimeArr.get(1) + " 11:59:59");
        }

        String preparedDate = pd.getString("preparedDate");
        if(StringUtils.isNotBlank(preparedDate)){
            JSONArray preparedDateArr = JSONArray.parseArray(preparedDate);
            pd.put("beginPreparedDate",preparedDateArr.get(0));
            pd.put("lastPreparedDate",preparedDateArr.get(1));
        }


        List<PageData> DisclosurePaperList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.selectDisclosurePaperInfoAll", pd);

        return DisclosurePaperList;

    }

    @Override
    @Transactional
    public void addDisclosurePaper(PageData pd) {
        String businessId = "TEJD" + UUID.randomUUID().toString(); //生成业务id主键
        String serialNumber =  SequenceUtil.generateSerialNo("TEJD"); //生成流水号
        pd.put("serialNumber", serialNumber);
        pd.put("businessId", businessId);
        pd.put("creatorUserId", pd.getString("createUserId"));
        pd.put("createUserName", pd.getString("createUser"));


        baseDao.insert("DisclosurePaperMapper.insertDisclosurePaper", pd);

        // 插入到附件表
        String file = pd.getString("attachmentList");
        if (StringUtils.isNotBlank(file)) {
            List<PageData> listFile = JSONObject.parseArray(file, PageData.class);
            if (listFile.size() > 0) {
                for (PageData data : listFile) {
                    data.put("businessId", businessId);
                }
                baseDao.batchInsert("FileMapper.saveFileDetail", listFile);
            }
        }

    }


    @Override
    @Transactional
    public void deleteDisclosurePaper(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if (!CollectionUtils.isEmpty(idList)) {
            baseDao.batchDelete("DisclosurePaperMapper.deleteDisclosurePaper", idList);
        }

    }

    @Override
    @Transactional
    public void updateDisclosurePaper(PageData pd) {
        // 修改项目表内容
        baseDao.update("DisclosurePaperMapper.updateDisclosurePaper", pd);

        //编辑附件表
        fileService.update(pd);
    }


    @Override
    public PageData getDisclosurePaperDetail(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("DisclosurePaperMapper.queryById", pd);

        if (request != null){
            // 查询附件表
            List<PageData> attachmentList = (List<PageData>) baseDao.findForList("FileMapper.queryFile", request);
            if(!CollectionUtils.isEmpty(attachmentList)){
                AwsUtil.queryOneUrl(attachmentList, ConstantValUtil.BUCKET_PRIVATE);
                request.put("attachmentList", attachmentList);

            }
        }
        return request;
    }

    @Override
    public PageData getDisclosurePaperDetailBybusinessId(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("DisclosurePaperMapper.queryByBusinessId", pd);

        if (request != null){
            // 查询附件表
            List<PageData> attachmentList = (List<PageData>) baseDao.findForList("FileMapper.queryFile", request);
            if(!CollectionUtils.isEmpty(attachmentList)){
                AwsUtil.queryOneUrl(attachmentList, ConstantValUtil.BUCKET_PRIVATE);
                request.put("attachmentList", attachmentList);

            }
        }
        return request;
    }


    @Override
    public List<PageData> getDisclosurePaperDetailList(PageData pd) {

        // 前端的多个状态
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        // 查询数据信息
        List<PageData> reportInfoList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.queryByIds", idList);
        return reportInfoList;
    }



    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "项目交底书信息";
        String[] head = {"序号", "单据编号", "项目名称", "项目负责人", "负责人岗位", "联系电话", "交底书编制人", "编制日期", "编制年度", "一、本期主要研发内容季关键（核心）技术及主要创新点","二、本期工作（进度）安排"
                ,"三、本期研究开发具体安排（一）人工安排","三、本期研究开发具体安排（二）研发仪器设备安排",
                "三、本期研究开发具体安排（三）材料、燃料、动力费用投入安排","三、本期研究开发具体安排（四）模具、工艺装备开发及制造及设备调试安排","三、本期研究开发具体安排（五）其他"
                ,"编制人", "创建日期", "更新日期"};
        String idStr = pageData.getString("idList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        List<PageData> DisclosurePaperList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.queryByIds", listId);
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

        if (!CollectionUtils.isEmpty(DisclosurePaperList)) {
            for (int i = 0; i < DisclosurePaperList.size(); i++) {
                PageData pd = DisclosurePaperList.get(i);

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
                cell.setCellValue(pd.getString("projectLeaderName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("leaderPostName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("contactNumber"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("preparedName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("preparedDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("preparedYear"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("keyTechnology"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("workPlan"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("artificialPlan"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("equipmentPlan"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("costPlan"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("testPlan"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("otherPlan"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("creatorUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time1 = pd.getString("createTime");
                cell.setCellValue(time1.substring(0,time1.lastIndexOf(".")));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                String time2 = pd.getString("updateTime");
                cell.setCellValue(time2.substring(0,time2.lastIndexOf(".")));
                cell.setCellStyle(styleCell);


            }
        }
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
     * 创建存放临时文件的文件夹
     *
     * @return
     */
    @Override
    public File createFile() {
        //根据文件的项目路径，获得文件的系统路径。
        String projectPath = System.getProperty("user.dir");
        //存放临时文件
        File file = new File(projectPath + "\\temporaryfiles");
        //如果文件夹不存在
        if (!file.exists()) {
            //创建文件夹
            file.mkdir();
        }
        return file;
    }


    private PageData getSurveyInfo(XSSFWorkbook wb,PageData pd) {

        XSSFSheet sheet = wb.getSheet("导入模板");
        if (sheet == null) {
            throw new MyException("请按规定模板导入内容");
        }


        //课题项目名称：
        XSSFRow row = sheet.getRow(1);
        XSSFCell cell = row.getCell(0);
        String currentSituation = ReadExcelUtil.readCellMinStr(cell, 1, "课题项目名称：",7);
        pd.put("projectName",currentSituation.substring(7));

        //一、本期主要研发内容季关键（核心）技术及主要创新点
        row = sheet.getRow(3);
        cell = row.getCell(0);
        currentSituation = ReadExcelUtil.readCellMinStr(cell, 3, "关键（核心）技术",200);
        pd.put("keyTechnology",currentSituation);


        //二、本期工作（进度）安排
        row = sheet.getRow(5);
        cell = row.getCell(0);
        String workPlan = ReadExcelUtil.readCellMinStr(cell, 5, "二、本期工作（进度）安排",200);
        pd.put("workPlan",workPlan);

        //（一）人工安排
        row = sheet.getRow(8);
        cell = row.getCell(0);
        String artificialPlan = ReadExcelUtil.readCellMinStr(cell, 8, "人工安排",200);
        pd.put("artificialPlan",artificialPlan);

        //（二）研发仪器设备安排
        row = sheet.getRow(10);
        cell = row.getCell(0);
        String equipmentPlan = ReadExcelUtil.readCellMinStr(cell, 10, "研发仪器设备安排", 200);
        pd.put("equipmentPlan",equipmentPlan);

        //（三）材料、燃料、动力费用投入安排
        row = sheet.getRow(12);
        cell = row.getCell(0);
        String costPlan = ReadExcelUtil.readCellMinStr(cell, 12, "材料、燃料、动力费用投入安排",200);
        pd.put("costPlan",costPlan);

        //（四）模具、工艺装备开发及制造及设备调试安排
        row = sheet.getRow(14);
        cell = row.getCell(0);
        String testPlan = ReadExcelUtil.readCellMinStr(cell, 14, "模具、工艺装备开发及制造及设备调试安排",200);
        pd.put("testPlan",testPlan);

        //（五）其他
        row = sheet.getRow(16);
        cell = row.getCell(0);
        String otherPlan = ReadExcelUtil.readCellMinStr(cell, 16, "其他安排",200);
        pd.put("otherPlan",otherPlan);

        return pd;
    }

    @Override
    public PageData uploadSurvey(MultipartFile file, PageData pd) throws Exception{
        XSSFWorkbook wb = null;
        try {
            wb = getWorkbook(file);
        } catch (Exception e) {
            throw new MyException("请按规定模板导入内容");
        }

        getSurveyInfo(wb,pd);

        return pd;
    }

    private XSSFWorkbook getWorkbook(MultipartFile file) throws Exception{
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
    public void export(PageData pd, ZipOutputStream zos, ByteArrayOutputStream bos, String serialNumber) throws Exception {

        String idStr = pd.getString("idList");
        List<PageData> idList = (List<PageData>) JSONArray.parse(idStr);

        List<PageData> fileList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.queryByIds", idList);
        if (fileList.size() > 0) {
            int i =1;
            for (PageData data : fileList) {
                XwpfTUtil xwpfTUtil = new XwpfTUtil();
                XWPFDocument doc;
                InputStream is = null;
                File file = this.createFile();
                try {
                    //获取文件路径
                    logger.info("==============获取文件路径================");
                    is = this.getClass().getResourceAsStream("/template/技术经济交底书导出模板.docx");
                    doc = new XWPFDocument(is);
                    if (data.size() > 0) {
                        //文本
                        Map<String, Object> params = new HashMap<>();

                        //单据编号
                        params.put("(serialNumber)", data.getString("serialNumber"));
                        //编制人
                        params.put("(creatorUserName)", data.getString("creatorUserName"));
                        //创建日期
                        params.put("(createdDate)", data.getString("createdDate"));
                        //项目名称
                        params.put("(projectName)", data.getString("projectName"));
                        //所属单位名称
                        params.put("(unitName)", data.getString("unitName"));
                        //项目负责人
                        params.put("(projectLeaderName)", data.getString("projectLeaderName"));
                        //联系电话
                        params.put("(contactNumber)", data.getString("contactNumber"));
                        //岗位
                        params.put("(leaderPostName)", data.getString("leaderPostName"));
                        //交底书编制人
                        params.put("(preparedName)", data.getString("preparedName"));
                        //编制日期
                        params.put("(preparedDate)", data.getString("preparedDate"));
                        //编制年度
                        params.put("(preparedYear)", data.getString("preparedYear"));
                        //编制季度
                        params.put("(preparedQuarter)", data.getString("preparedQuarter"));
                        //核心技术
                        params.put("(keyTechnology)", data.getString("keyTechnology"));
                        //工作安排
                        params.put("(workPlan)", data.getString("workPlan"));
                        //忍冬安排
                        params.put("(artificialPlan)", data.getString("artificialPlan"));
                        //设备安排
                        params.put("(equipmentPlan)", data.getString("equipmentPlan"));
                        //费用安排
                        params.put("(costPlan)", data.getString("costPlan"));
                        //成果报告说明
                        params.put("(testPlan)", data.getString("testPlan"));
                        //其他安排
                        params.put("(otherPlan)", data.getString("otherPlan"));

                        //替换文字
                        xwpfTUtil.replaceInPara(doc, params);
                        //  在默认文件夹下创建临时文件
                        //prefix :临时文件名的前缀,   suffix :临时文件名的后缀
                        String fileName = "研发项目进展报告" + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
                        File newWordTempFile = new File(file, fileName + i + ".docx");

                        String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                        File pdfTempFile = new File(file, fileName + i  + ".pdf");

                        String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                        //下载到临时word文件中
                        FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                        doc.write(os);
                        xwpfTUtil.close(is);
                        xwpfTUtil.close(os);
                        //将word转为pdf
                        Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);
//                        //输出pdf文件
//                        Word2pdfUtil.fileResponse(pdfTempFilePath, response);
//                       String fileName2 = data.getString("fileName");

                        //将数据写入到zip流中
                        //已读出图片
                        DownloadUtil.zipFile(fileName + i +".pdf", pdfTempFile, zos);
                        i++;
                        is.close();

                        //删除临时文件
                        newWordTempFile.delete();
                        pdfTempFile.delete();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    xwpfTUtil.close(is);
                    logger.error("经济交底书--导出报告异常");

                }



            }
        }
    }


    @Override
    public ResponseEntity export2(PageData pd, HttpServletResponse response) throws Exception {

        String idStr = pd.getString("idList");
        List<PageData> idList = (List<PageData>) JSONArray.parse(idStr);

        List<PageData> fileList = (List<PageData>) baseDao.findForList("DisclosurePaperMapper.queryByIds", idList);
        if (fileList.size() > 0) {
            int i = 1;
            XWPFDocument doc1 = new XWPFDocument();
            File file = this.createFile();
            XwpfTUtil xwpfTUtil = null;
            InputStream is = null;
            for (PageData data : fileList) {
                xwpfTUtil = new XwpfTUtil();
                XWPFDocument doc;
                is = null;

                try {
                    //获取文件路径
                    logger.info("==============获取文件路径================");
                    is = this.getClass().getResourceAsStream("/template/技术经济交底书导出模板.docx");
                    doc = new XWPFDocument(is);
                    if (data.size() > 0) {
                        String taxRate = "";
                        //文本
                        Map<String, Object> params = new HashMap<>();

                        //单据编号
                        params.put("(serialNumber)", data.getString("serialNumber"));
                        //编制人
                        params.put("(creatorUserName)", data.getString("creatorUserName"));
                        //创建日期
                        params.put("(createdDate)", data.getString("createdDate"));
                        //项目名称
                        params.put("(projectName)", data.getString("projectName"));
                        //所属单位名称
                        params.put("(unitName)", data.getString("unitName"));
                        //项目负责人
                        params.put("(projectLeaderName)", data.getString("projectLeaderName"));
                        //联系电话
                        params.put("(contactNumber)", data.getString("contactNumber"));
                        //岗位
                        params.put("(leaderPostName)", data.getString("leaderPostName"));
                        //交底书编制人
                        params.put("(preparedName)", data.getString("preparedName"));
                        //编制日期
                        params.put("(preparedDate)", data.getString("preparedDate"));
                        //编制年度
                        params.put("(preparedYear)", data.getString("preparedYear"));
                        //编制季度
                        params.put("(preparedQuarter)", data.getString("preparedQuarter"));
                        //核心技术
                        params.put("(keyTechnology)", data.getString("keyTechnology"));
                        //工作安排
                        params.put("(workPlan)", data.getString("workPlan"));
                        //忍冬安排
                        params.put("(artificialPlan)", data.getString("artificialPlan"));
                        //设备安排
                        params.put("(equipmentPlan)", data.getString("equipmentPlan"));
                        //费用安排
                        params.put("(costPlan)", data.getString("costPlan"));
                        //成果报告说明
                        params.put("(testPlan)", data.getString("testPlan"));
                        //其他安排
                        params.put("(otherPlan)", data.getString("otherPlan"));

                        //替换文字
                        xwpfTUtil.replaceInPara(doc, params);
                        if (i == 1) {
                            doc1 = doc;
                            if (fileList.size() > 1) {
                                XWPFParagraph p = doc1.createParagraph();
                                p.setPageBreak(true);
                            }

                        } else {
                            if (i == fileList.size()) {
                                doc1 = mergeWord(doc1, doc,false);
                            }else {
                                doc1 = mergeWord(doc1, doc,true);
                            }

                        }
                        i++;
                        xwpfTUtil.close(is);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    xwpfTUtil.close(is);
                    logger.error("技术经济交底书--导出报告异常");

                }


            }

            //  在默认文件夹下创建临时文件
            //prefix :临时文件名的前缀,   suffix :临时文件名的后缀
            String fileName = "研发项目进展报告" + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
            File newWordTempFile = new File(file, fileName + i + ".docx");

            String newWordTempFilePath = newWordTempFile.getCanonicalPath();
            File pdfTempFile = new File(file, fileName + i + ".pdf");

            String pdfTempFilePath = pdfTempFile.getCanonicalPath();

            //下载到临时word文件中
            FileOutputStream os = new FileOutputStream(newWordTempFilePath);
            doc1.write(os);

            xwpfTUtil.close(os);
            //将word转为pdf
            Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);
            //输出pdf文件
            Word2pdfUtil.fileResponse(pdfTempFilePath, response);


            //将数据写入到zip流中
            //已读出图片
//                        DownloadUtil.zipFile(fileName + i +".pdf", pdfTempFile, zos);

            is.close();

            //删除临时文件
            newWordTempFile.delete();
            pdfTempFile.delete();

        }
        return ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
    }

    public  XWPFDocument mergeWord(XWPFDocument document1,XWPFDocument document2,Boolean flag) throws Exception {
        if (flag) {
            XWPFParagraph p = document2.createParagraph();
            p.setPageBreak(true);
        }
        CTBody src1Body = document1.getDocument().getBody();
        CTBody src2Body = document2.getDocument().getBody();
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = src2Body.xmlText(optionsOuter);
        String srcString = src1Body.xmlText();
        String prefix = srcString.substring(0,srcString.indexOf(">")+1);
        String mainPart = srcString.substring(srcString.indexOf(">")+1,srcString.lastIndexOf("<"));
        String sufix = srcString.substring( srcString.lastIndexOf("<") );
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody = CTBody.Factory.parse(prefix+mainPart+addPart+sufix);
        src1Body.set(makeBody);
        return document1;
    }

}

