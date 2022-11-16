package com.rdexpense.manager.service.progressreport.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.rdexpense.manager.enums.ApprovalstatusEnum;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.progressreport.ProgressReportService;
import com.rdexpense.manager.util.Date2CnDate;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.ERR_SAVE_FAIL;
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

    @Autowired
    @Resource(name = "FlowService")
    private FlowService flowService;

    @Autowired
    private FileService fileService;

    /**
     * 列表查询
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryReportList(PageData pd) {
//      前端的多个状态
        String createTime = pd.getString("createTime");
        if(StringUtils.isNotBlank(createTime)){
            JSONArray createTimeArr = JSONArray.parseArray(createTime);
            pd.put("beginCreateTime",createTimeArr.get(0) + " 00:00:00");
            pd.put("lastCreateTime",createTimeArr.get(1) + " 23:59:59");
        }

        String reporterDate = pd.getString("reporterDate");
        if(StringUtils.isNotBlank(reporterDate)){
            JSONArray reporterDateArr = JSONArray.parseArray(reporterDate);
            pd.put("beginReporterDate",reporterDateArr.get(0));
            pd.put("lastReporterDate",reporterDateArr.get(1));
        }

        String startDate = pd.getString("startDate");
        if(StringUtils.isNotBlank(startDate)){
            JSONArray startDateArr = JSONArray.parseArray(startDate);
            pd.put("beginStartDate",startDateArr.get(0));
            pd.put("lastStartDate",startDateArr.get(1));
        }

        String endDate = pd.getString("endDate");
        if(StringUtils.isNotBlank(endDate)){
            JSONArray endDateArr = JSONArray.parseArray(endDate);
            pd.put("beginEndDate",endDateArr.get(0));
            pd.put("lastEndDate",endDateArr.get(1));
        }

        List<String> processStatusList = JSONArray.parseArray(pd.getString("statusList"), String.class);
        pd.put("processStatus", processStatusList);
        pd.put("processStatus-sql", ConstantValUtil.APPROVAL_STATUS[0]);
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
        String saveStaus = pd.getString("saveStaus");

        String businessId = PREFIX + UUID.randomUUID().toString();//生成业务主键ID
        String serialNumber =SequenceUtil.generateSerialNo(PREFIX);//生成流水号
        System.out.println("=========="+serialNumber+"================");


        if (("0").equals(saveStaus) ) {
            pd.put("processStatus",ConstantValUtil.APPROVAL_STATUS[0]);
        } else {
            pd.put("processStatus",ConstantValUtil.APPROVAL_STATUS[1]);

        }


        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);
        pd.put("creatorUserId", pd.getString("createUserId"));
        pd.put("creatorUserName", pd.getString("createUser"));


        if (("1").equals(saveStaus) ) {
            //开启工作流
            CheckParameter.stringIsNull(pd.getString("menuCode"), "菜单编码");
            pd.put("createUser",pd.getString("creatorUserName"));
            pd.put("createUserId",pd.getString("creatorUserId"));
            flowService.startFlow(pd);
        }

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
    public PageData getDetailByBusinessid(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("ProgressReportMapper.getReportDetailByBusinessid", pd);

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
    @Transactional
    public void updateReport(PageData pd) {

        String saveStaus = pd.getString("saveStaus");
        if("0".equals(saveStaus)){
            //保存
            //修改项目进展报告表
            baseDao.update("ProgressReportMapper.updateReport",pd);
            //编辑附件表
            fileService.update(pd);
        } else {
            //提交
//            PageData pagedata = getReportDetail(pd);
            checksaveParams(pd);
            checkParam(pd);

            pd.put("menuCode",pd.getString("menuCode"));
            pd.put("createUser",pd.getString("creatorUserName"));
            pd.put("createUserId",pd.getString("creatorUserId"));
            flowService.startFlow(pd);
            //修改项目进展报告表updateReportStatus
            baseDao.update("ProgressReportMapper.updateReport",pd);
            baseDao.update("ProgressReportMapper.updateReportStatus",pd);
            //编辑附件表
            fileService.update(pd);
        }



    }

    @Override
    public void deleteReport(PageData pd) {

        List<String> businessIdList = JSONArray.parseArray(pd.getString("businessIdList"), String.class);
        //删除主表
        baseDao.batchDelete("ProgressReportMapper.deleteReport", businessIdList);
        //先删除附件表
        fileService.deleteAttachment(pd);

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
                checksaveParams(pageData);
//                CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 128);
//                CheckParameter.stringLengthAndEmpty(pd.getString("menuCode"), "菜单编码", 128);
//                CheckParameter.stringLengthAndEmpty(pd.getString("serialNumber"), "单据号", 128);
                pageData.put("menuCode",pd.getString("menuCode"));
                pageData.put("createUser",pageData.getString("creatorUserName"));
                pageData.put("createUserId",pageData.getString("creatorUserId"));

                flowService.startFlow(pageData);

//                pageData.put("processStatus",ConstantValUtil.APPROVAL_STATUS[1]);
                baseDao.update("ProgressReportMapper.updateReportStatus", pageData);
            }
        }
    }

    @Override
    public void approveRecord(PageData pd) {
        //插入附件表，并返回主键id的拼接字符串
        PageData fileIdStr = fileService.insertApproveFile(pd);
        pd.put("fileId", fileIdStr);

        //审批类型 1:同意 2:回退上一个节点 3：回退到发起人
        String approveType = pd.getString("approveType");
        if (approveType.equals("1")) {
            flowService.approveFlow(pd);

        } else if (approveType.equals("2")) {
            flowService.backPreviousNode(pd);

        } else if (approveType.equals("3")) {
            flowService.backOriginalNode(pd);
        }

        //编辑主表的审批状态、审批人等信息
        baseDao.update("ProgressReportMapper.updateReportStatus", pd);
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

    @Override
    public PageData uploadSurvey(MultipartFile file, PageData pd) throws Exception {
        XSSFWorkbook wb = getWorkbook(file);

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

    private PageData getSurveyInfo(XSSFWorkbook wb,PageData pd) {

        XSSFSheet sheet = wb.getSheet("研发项目进展报告");
        if (sheet == null) {
            throw new MyException("请上传正确的模板，检查研发项目进展报告sheet页是否正确");
        }
        // 课题名称
        XSSFRow row = sheet.getRow(1);
        XSSFCell cell = row.getCell(0);
        String projectName = ReadExcelUtil.readCellMinStr(cell, 1, "课题名称", 7);
        if(projectName.length() > 7){
            projectName = projectName.substring(7);
        }else {
            throw new MyException("课题项目名称有误！请根据模板填写");
        }
        pd.put("projectName",projectName);

        //一、项目概况
        row = sheet.getRow(3);
        cell = row.getCell(0);
        String currentSituation = ReadExcelUtil.readCellMinStr(cell, 3, "项目概况", 200);
        pd.put("projectOverview",currentSituation);

        //二、研发过程
        row = sheet.getRow(5);
        cell = row.getCell(0);
        String purposeSignificance = ReadExcelUtil.readCellMinStr(cell, 5, "研发过程", 200);
        pd.put("developmentProcess",purposeSignificance);

        //三、关键技术及创新
        row = sheet.getRow(7);
        cell = row.getCell(0);
        String contentMethod = ReadExcelUtil.readCellMinStr(cell, 7, "关键技术及创新", 200);
        pd.put("keyTechnology",contentMethod);

        //四、取得的阶段性成果
        row = sheet.getRow(9);
        cell = row.getCell(0);
        String targetResults = ReadExcelUtil.readCellMinStr(cell, 9, "取得的阶段性成果", 200);
        pd.put("achieveResults",targetResults);

        //五、经济效益和社会、环保效益
        row = sheet.getRow(11);
        cell = row.getCell(0);
        String basicConditions = ReadExcelUtil.readCellMinStr(cell, 11, "经济效益和社会、环保效益",200);
        pd.put("beneficialResult",basicConditions);

        //六、成果报告说明
        row = sheet.getRow(13);
        cell = row.getCell(0);
        String innovationPoints = ReadExcelUtil.readCellMinStr(cell, 13, "成果报告说明", 200);
        pd.put("reportDescription",innovationPoints);

        return pd;
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
                String time2 = pd.getString("createTime");
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
                    List<PageData> attachment = fileService.query(data);
                    //获取文件路径
                    logger.info("==============获取文件路径================");
                    is = this.getClass().getResourceAsStream("/template/进展报告导出.docx");
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

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                        String date = df.format(new Date());
                        params.put("(now)", Date2CnDate.convertDate2CnDate(date));

                        //替换文字
                        xwpfTUtil.replaceInPara(doc, params);

                        //插入附件清单
                        if(attachment.size() > 0){
                            XWPFTable table1 = doc.createTable(attachment.size()+1, 4);
                            CTTblPr tblPr = table1.getCTTbl().getTblPr();
                            tblPr.getTblW().setType(STTblWidth.DXA);
                            tblPr.getTblW().setW(new BigInteger("7000"));
                            // 获取到刚刚插入的行
                            XWPFTableRow row1 = table1.getRow(0);
                            // 设置单元格内容
                            row1.getCell(0).setText("序号");
                            row1.getCell(1).setText("附件名称");
                            row1.getCell(2).setText("附件大小");
                            row1.getCell(3).setText("上传人");

                            for (int index = 1; index <= attachment.size(); index++) {
                                // 获取到刚刚插入的行
                                XWPFTableRow row = table1.getRow(index);
                                row.setCantSplitRow(true);
                                row.setRepeatHeader(true);
                                // 设置单元格内容
                                row.getCell(0).setText(index+"");
                                row.getCell(1).setText(attachment.get(index-1).getString("fileName"));
                                row.getCell(2).setText(attachment.get(index-1).getString("fileSize"));
                                row.getCell(3).setText(attachment.get(index-1).getString("uploadUserName"));
                            }
                            doc.setTable(0, table1);
                        }
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
                    is = this.getClass().getResourceAsStream("/template/进展报告导出.docx");
                    doc = new XWPFDocument(is);
                    if (data.size() > 0) {
                        //文本
                        Map<String, Object> params = new HashMap<>();
                        List<PageData> attachment = fileService.query(data);
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
                        String reporterDate = data.getString("reporterDate");
                        if(StringUtils.isNotBlank(reporterDate) && reporterDate.length()>4){
                            reporterDate = data.getString("reporterDate").substring(0,4);
                        }
                        params.put("(reporterDate)", reporterDate);
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

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                        String date = df.format(new Date());
                        params.put("(now)", Date2CnDate.convertDate2CnDate(date));

                        //替换文字
                        xwpfTUtil.replaceInPara(doc, params);

                        //插入附件清单
                        if(attachment.size() > 0){
                            XWPFTable table1 = doc.createTable(attachment.size()+1, 4);
                            CTTblPr tblPr = table1.getCTTbl().getTblPr();
                            tblPr.getTblW().setType(STTblWidth.DXA);
                            tblPr.getTblW().setW(new BigInteger("7000"));
                            // 获取到刚刚插入的行
                            XWPFTableRow row1 = table1.getRow(0);
                            // 设置单元格内容
                            row1.getCell(0).setText("序号");
                            row1.getCell(1).setText("附件名称");
                            row1.getCell(2).setText("附件大小");
                            row1.getCell(3).setText("上传人");

                            for (int index = 1; index <= attachment.size(); index++) {
                                // 获取到刚刚插入的行
                                XWPFTableRow row = table1.getRow(index);
                                row.setCantSplitRow(true);
                                row.setRepeatHeader(true);
                                // 设置单元格内容
                                row.getCell(0).setText(index+"");
                                row.getCell(1).setText(attachment.get(index-1).getString("fileName"));
                                row.getCell(2).setText(attachment.get(index-1).getString("fileSize"));
                                row.getCell(3).setText(attachment.get(index-1).getString("uploadUserName"));
                            }
                            doc.setTable(0, table1);


                        }

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


//    private void saveParams(PageData pd){
//        // 项目概况
//        String projectOverview = pd.getString("projectOverview");
//        if (projectOverview == null || projectOverview == "") {
//            pd.put("projectOverview", " ");
//        }
//        // 研发过程
//        String developmentProcess = pd.getString("developmentProcess");
//        if (developmentProcess == null || developmentProcess == "") {
//            pd.put("developmentProcess", " ");
//        }
//        // 关键技术及创新
//        String keyTechnology = pd.getString("keyTechnology");
//        if (keyTechnology == null || keyTechnology == "") {
//            pd.put("keyTechnology", " ");
//        }
//        // 取得的阶段性成果
//        String achieveResults = pd.getString("achieveResults");
//        if (achieveResults == null || achieveResults == "") {
//            pd.put("achieveResults", " ");
//        }
//        // 经济效益和社会、环保效益
//        String beneficialResult = pd.getString("beneficialResult");
//        if (beneficialResult == null || beneficialResult == "") {
//            pd.put("beneficialResult", " ");
//        }
//        // 成果报告说明
//        String reportDescription = pd.getString("reportDescription");
//        if (reportDescription == null || reportDescription == "") {
//            pd.put("reportDescription", " ");
//        }
//    }

    private void checksaveParams(PageData pageData){
        // 项目概况
        String projectOverview = pageData.getString("projectOverview");
        if (projectOverview.equals(" ")) {
            throw new MyException("项目概况不能为空");
        }
        // 研发过程
        String developmentProcess = pageData.getString("developmentProcess");
        if (developmentProcess.equals(" ")) {
            throw new MyException("研发过程不能为空");
        }
        // 关键技术及创新
        String keyTechnology = pageData.getString("keyTechnology");
        if (keyTechnology.equals(" ")) {
            throw new MyException("关键技术及创新不能为空");
        }
        // 取得的阶段性成果
        String achieveResults = pageData.getString("achieveResults");
        if (achieveResults.equals(" ")) {
            throw new MyException("取得的阶段性成果不能为空");
        }
        // 经济效益和社会、环保效益
        String beneficialResult = pageData.getString("beneficialResult");
        if (beneficialResult.equals(" ")) {
            throw new MyException("经济效益和社会、环保效益不能为空");
        }
        // 成果报告说明
        String reportDescription = pageData.getString("reportDescription");
        if (reportDescription.equals(" ")) {
            throw new MyException("成果报告说明不能为空");
        }
    }

    private void editParams(PageData pageData){
        // 项目概况
        String projectOverview = pageData.getString("projectOverview");
        if (projectOverview.equals(" ")) {
            pageData.put("projectOverview", "");
        }
        // 研发过程
        String developmentProcess = pageData.getString("developmentProcess");
        if (developmentProcess.equals(" ")) {
            pageData.put("developmentProcess", "");
        }
        // 关键技术及创新
        String keyTechnology = pageData.getString("keyTechnology");
        if (keyTechnology.equals(" ")) {
            pageData.put("keyTechnology", "");
        }
        // 取得的阶段性成果
        String achieveResults = pageData.getString("achieveResults");
        if (achieveResults.equals(" ")) {
            pageData.put("achieveResults", "");
        }
        // 经济效益和社会、环保效益
        String beneficialResult = pageData.getString("beneficialResult");
        if (beneficialResult.equals(" ")) {
            pageData.put("beneficialResult", "");
        }
        // 成果报告说明
        String reportDescription = pageData.getString("reportDescription");
        if (reportDescription.equals(" ")) {
            pageData.put("reportDescription", "");
        }
    }
}
