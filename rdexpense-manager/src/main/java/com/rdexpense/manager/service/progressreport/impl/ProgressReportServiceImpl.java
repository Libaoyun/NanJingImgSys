package com.rdexpense.manager.service.progressreport.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.rdexpense.manager.enums.ApprovalstatusEnum;
import com.rdexpense.manager.service.progressreport.ProgressReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.INFO_EXPORT_SUCCESS;

/**
 * @author dengteng
 * @title: ProgressReportServiceImpl
 * @projectName rdexpense-back
 * @description: TODO
 * @date 2021/11/24
 */
@Service("ProgressReportService")
public class ProgressReportServiceImpl implements ProgressReportService {

    private static final Logger logger = LoggerFactory.getLogger(ProgressReportServiceImpl.class);
    private static final String PREFIX = "XMJZ";

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    /**
     * 列表查询
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryReportList(PageData pd) {
//      前端的多个状态
        List<String> processStatusList = JSONArray.parseArray(pd.getString("processStatus"), String.class);
        pd.put("processStatus", processStatusList);

        //查询数据信息
        pd.put("saveStatus", ApprovalstatusEnum.PASSED.getCode());
        pd.put("creatorUserId",pd.getString("createUserId"));
        List<PageData> reportInfoList = (List<PageData>) baseDao.findForList("ProgressReportMapper.selectReportInfoAll", pd);
        return reportInfoList;

    }

    /**
     * 新增报告
     * @param pd
     */
    @Override
    @Transactional
    public void addReport(PageData pd) {
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("ProgressReportMapper.checkReport", pd);
        if (!CollectionUtils.isEmpty(userInfoList)) {
            throw new MyException("研发项目进展报告编号已存在");
        }

        // 项目概况
        String projectOverview = pd.getString("projectOverview");
        if (projectOverview == null || projectOverview == "") {
            pd.put("projectOverview"," ");
        }
        // 研发过程
        String developmentProcess = pd.getString("developmentProcess");
        if (developmentProcess == null || developmentProcess == "") {
            pd.put("developmentProcess"," ");
        }
        // 关键技术及创新
        String keyTechnology = pd.getString("keyTechnology");
        if (keyTechnology == null || keyTechnology == "") {
            pd.put("keyTechnology"," ");
        }
        // 取得的阶段性成果
        String achieveResults = pd.getString("achieveResults");
        if (achieveResults == null || achieveResults == "") {
            pd.put("achieveResults"," ");
        }
        // 经济效益和社会、环保效益
        String beneficialResult = pd.getString("beneficialResult");
        if (beneficialResult == null || beneficialResult == "") {
            pd.put("beneficialResult"," ");
        }
        // 成果报告说明
        String reportDescription = pd.getString("reportDescription");
        if (reportDescription == null || reportDescription == "") {
            pd.put("reportDescription"," ");
        }

        String businessId = "XMJZ" + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber =PREFIX + SequenceUtil.generateSerialNo().substring(4);//生成流水号
        System.out.println("=========="+serialNumber+"================");

        String saveStaus = pd.getString("saveStaus");
        if (("0").equals(saveStaus) ) {
            pd.put("processStatus",ApprovalstatusEnum.NOT_SUBMITTED.getCode());
        } else {
            pd.put("processStatus",ApprovalstatusEnum.UNDER_APPROVAL.getCode());
        }


        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("creatorUserId", pd.getString("createUserId"));
        pd.put("creatorUserName", pd.getString("createUser"));

        baseDao.insert("ProgressReportMapper.insertReport", pd);

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

    /**
     * 查询单条报告详情
     * @param pd
     * @return
     */
    @Override
    public PageData getReportDetail(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("ProgressReportMapper.getReportDetail", pd);

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
    public void updateReport(PageData pd) {

        //修改项目进展报告表
        baseDao.update("ProgressReportMapper.updateReport",pd);

        //编辑附件表
        //更新附件表，前端只传无id的，将这些数据入库
        List<PageData> fileList = new ArrayList<PageData>();
        String attachmentDetailStr = pd.getString("attachmentList");
        if (StringUtils.isNotBlank(attachmentDetailStr)) {
            List<PageData> listFile = JSONObject.parseArray(attachmentDetailStr, PageData.class);
            if(!CollectionUtils.isEmpty(listFile)){
                for (PageData data : listFile) {
                    String frontId = data.getString("id");
                    if (frontId == null || frontId.equals("")) {
                        data.put("businessId", pd.getString("businessId"));
                        fileList.add(data);
                    }
                }
                if (fileList.size() > 0) {
                    baseDao.batchInsert("FileMapper.saveFileDetail", fileList);
                }
            }
        }

    }

    @Override
    public void deleteReport(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if(!CollectionUtils.isEmpty(idList)) {
            baseDao.batchDelete("ProgressReportMapper.deleteReport", idList);

        }
    }

    @Override
    @Transactional
    public void submitReport(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if(!CollectionUtils.isEmpty(idList)) {
            List<PageData> reportInfoList = (List<PageData>) baseDao.findForList("ProgressReportMapper.getReportDetailList", idList);

            for (int i = 0; i < reportInfoList.size(); i++) {
                PageData pageData = reportInfoList.get(i);
                checkParam(pageData);
                pageData.put("processStatus",ApprovalstatusEnum.UNDER_APPROVAL.getCode());
                baseDao.update("ProgressReportMapper.modifyReportState", pageData);
            }
        }

    }

    @Override
    @Transactional
    public void repealedReport(PageData pd) {

        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if(!CollectionUtils.isEmpty(idList)) {
            List<PageData> reportInfoList = (List<PageData>) baseDao.findForList("ProgressReportMapper.getReportDetailList", idList);

            for (int i = 0; i < reportInfoList.size(); i++) {
                PageData pageData = reportInfoList.get(i);
                checkParam(pageData);
                pageData.put("processStatus",ApprovalstatusEnum.REPEALED.getCode());
                baseDao.update("ProgressReportMapper.modifyReportState", pageData);
            }
        }

    }

    @Override
    public List<PageData> getReportDetailList(PageData pd) {

        // 前端的多个状态
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        // 查询数据信息
        List<PageData> reportInfoList = (List<PageData>) baseDao.findForList("ProgressReportMapper.getReportDetailList", idList);
        return reportInfoList;
    }


    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "研发项目进展报告";
        String[] head = {"序号", "单据编号", "项目名称", "项目负责人", "负责人岗位", "联系电话", "单位名称", "起始年度", "结束年度", "报告人", "报告日期",
                "一、项目概况", "二、研发过程","三、关键技术及创新","四、取得的阶段性成果","五、经济效益和社会、环保效益","六、成果报告说明","编制人","创建日期","更新日期"};
        String idStr = pageData.getString("idList");
        List<String> idList = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> userInfoList = (List<PageData>) baseDao.findForList("ProgressReportMapper.getReportDetailList", idList);

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

        if (!CollectionUtils.isEmpty(userInfoList)) {
            for (int i = 0; i < userInfoList.size(); i++) {
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
                cell.setCellValue(pd.getString("projectLeaderName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("leaderPostName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("contactNumber"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("unitName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("startDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("endDate"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("reporterName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time = pd.getString("reporterDate");
                cell.setCellValue(time);
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectOverview"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("developmentProcess"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("keyTechnology"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("achieveResults"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("beneficialResult"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("reportDescription"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("creatorUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time2 = pd.getString("createdDate");
                cell.setCellValue(time2);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String time1 = pd.getString("updateTime");
                cell.setCellValue(time1);
                cell.setCellStyle(styleCell);


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


    @Override
    public void export(PageData pd, ZipOutputStream zos, ByteArrayOutputStream bos, String serialNumber) throws Exception {

        String idStr = pd.getString("idList");
        List<PageData> idList = (List<PageData>) JSONArray.parse(idStr);

        List<PageData> fileList = (List<PageData>) baseDao.findForList("ProgressReportMapper.getReportDetailList", idList);
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
                    is = this.getClass().getResourceAsStream("/template/研发项目进展报告.docx");
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
                        //起始年度
                        params.put("(startDate)", data.getString("startDate"));
                        //结束年度
                        params.put("(endDate)", data.getString("endDate"));
                        //报告人
                        params.put("(reporterName)", data.getString("reporterName"));
                        //报告日期
                        params.put("(reporterDate)", data.getString("reporterDate"));
                        //项目概况
                        params.put("(projectOverview)", data.getString("projectOverview"));
                        //研发过程
                        params.put("(developmentProcess)", data.getString("developmentProcess"));
                        //关键技术及创新
                        params.put("(keyTechnology)", data.getString("keyTechnology"));
                        //取得的阶段性成果
                        params.put("(achieveResults)", data.getString("achieveResults"));
                        //经济效益和社会、环保效益
                        params.put("(beneficialResult)", data.getString("beneficialResult"));
                        //成果报告说明
                        params.put("(reportDescription)", data.getString("reportDescription"));

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
                    logger.error("项目研发进展报告--导出报告异常");

                }



            }
        }
    }


    @Override
    public ResponseEntity export2(PageData pd, HttpServletResponse response) throws Exception {

        String idStr = pd.getString("idList");
        List<PageData> idList = (List<PageData>) JSONArray.parse(idStr);

        List<PageData> fileList = (List<PageData>) baseDao.findForList("ProgressReportMapper.getReportDetailList", idList);
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
                    is = this.getClass().getResourceAsStream("/template/研发项目进展报告.docx");
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
                        //起始年度
                        params.put("(startDate)", data.getString("startDate"));
                        //结束年度
                        params.put("(endDate)", data.getString("endDate"));
                        //报告人
                        params.put("(reporterName)", data.getString("reporterName"));
                        //报告日期
                        params.put("(reporterDate)", data.getString("reporterDate"));
                        //项目概况
                        params.put("(projectOverview)", data.getString("projectOverview"));
                        //研发过程
                        params.put("(developmentProcess)", data.getString("developmentProcess"));
                        //关键技术及创新
                        params.put("(keyTechnology)", data.getString("keyTechnology"));
                        //取得的阶段性成果
                        params.put("(achieveResults)", data.getString("achieveResults"));
                        //经济效益和社会、环保效益
                        params.put("(beneficialResult)", data.getString("beneficialResult"));
                        //成果报告说明
                        params.put("(reportDescription)", data.getString("reportDescription"));

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
                    logger.error("项目研发进展报告--导出报告异常");

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

    /**
     * @Description:  校验新增进展报告
     * @Param: [pd]
     * @return: void
     * @Author: dengteng
     * @Date: 2021/11/24
     */
    private void checkParam(PageData pd) {
        CheckParameter.stringIsNull(pd.getString("projectName"), "项目名称");
        CheckParameter.stringIsNull(pd.getString("contactNumber"), "联系电话");
        CheckParameter.stringIsNull(pd.getString("startDate"), "起始年度");
        CheckParameter.stringIsNull(pd.getString("endDate"), "结束年度");
        CheckParameter.stringIsNull(pd.getString("reporterName"), "报告人");
        CheckParameter.stringIsNull(pd.getString("reporterDate"), "报告日期");
        CheckParameter.stringIsNull(pd.getString("projectOverview"), "项目概况");
        CheckParameter.stringIsNull(pd.getString("developmentProcess"), "研发过程");
        CheckParameter.stringIsNull(pd.getString("keyTechnology"), "关键技术及创新");
        CheckParameter.stringIsNull(pd.getString("achieveResults"), "取得的阶段性成果");
        CheckParameter.stringIsNull(pd.getString("beneficialResult"), "经济效益和社会、环保效益");
        CheckParameter.stringIsNull(pd.getString("reportDescription"), "成果报告说明");
    }
}
