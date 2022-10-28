package com.rdexpense.manager.service.projContract.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.rdexpense.manager.service.projContract.ProjContractSignService;
import com.rdexpense.manager.util.Date2CnDate;
import com.rdexpense.manager.util.NumberCN2Arab;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;


import static com.common.util.ConstantMsgUtil.INFO_EXPORT_SUCCESS;

@Slf4j
@Service
public class ProjContractSignServiceImpl<treeMap> implements ProjContractSignService {
    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    private static final Logger logger = LoggerFactory.getLogger(ProjContractSignServiceImpl.class);

    /**
     * 新增项目合同
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void addProjContract(PageData pd) {
        List<PageData> projContractInfoList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.check", pd);
//        if (!CollectionUtils.isEmpty(projContractInfoList)) {
//            throw new MyException("该项目已签订过合同");
//        }

        String businessId = "YFHT" + UUID.randomUUID().toString(); //生成业务主键ID
        String serialNumber = SequenceUtil.generateSerialNo("YFHT");

        pd.put("businessId", businessId);
        pd.put("serialNumber", serialNumber);

        //TODO: 获取项目合同中的其他信息：创建者信息、单位信息、项目信息、管理者信息等
        //TODO：以projectId为索引，去其他表中查找项目相关信息，projectId -> businessId

        // 将新签订的合同信息插入数据库
        baseDao.insert("ProjContractSignMapper.insert", pd);

        // 将合同附件插入到附件表
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
     * 更新项目合同
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void updateProjContract(PageData pd) {
        String serialNumber = pd.getString("serialNumber");
        if (0 == serialNumber.length()) {
            throw new MyException("合同编号错误");
        }

        List<PageData> projContractInfoList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryBySerialNumber", pd);
        if (CollectionUtils.isEmpty(projContractInfoList)) {
            throw new MyException("未找到所属合同");
        }

        baseDao.update("ProjContractSignMapper.update", pd);

        //编辑附件表
        //更新附件表，前端只传无id的，将这些数据入库
        List<PageData> fileList = new ArrayList<PageData>();
        String attachmentDetailStr = pd.getString("attachmentList");
        if (StringUtils.isNotBlank(attachmentDetailStr)) {
            List<PageData> listFile = JSONObject.parseArray(attachmentDetailStr, PageData.class);
            if (!CollectionUtils.isEmpty(listFile)) {
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

    /**
     * 删除项目合同
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public void deleteProjContract(PageData pd) {
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryById", idList);

        if (!CollectionUtils.isEmpty(projContractList)) {
            //删除签订的项目合同信息
            baseDao.batchDelete("ProjContractSignMapper.delete", projContractList);

            //删除项目合同关联的附件
            baseDao.batchDelete("ProjContractSignMapper.batchDeleteFile", projContractList);
        }
    }

    /**
     * 查询项目合同
     *
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public List<PageData> queryProjContractList(PageData pd) {
        List<String> contractStatusCodeList = JSONArray.parseArray(pd.getString("contractStatusCodeList"), String.class);
        List<String> secretsCodeList = JSONArray.parseArray(pd.getString("secretsCodeList"), String.class);
        pd.put("contractStatusCodeList", contractStatusCodeList);
        pd.put("secretsCodeList", secretsCodeList);

        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.selectAll", pd);

        if (!CollectionUtils.isEmpty(projContractList)) {
            return projContractList;
        }

        return projContractList;
    }

    /**
     * 根据process_id查询项目合同列表
     *
     * @return
     */
    public List<PageData> queryByBusinessId(PageData pd) {
        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryByBusinessId", pd);

        if (!CollectionUtils.isEmpty(projContractList)) {
            return projContractList;
        }

        return projContractList;
    }

    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "项目合同签订信息";
        String[] head = {"序号", "合同编号", "项目名称", "密级", "合同状态", "项目负责人",
                "岗位", "所属单位名称", "起始年度", "结束年度", "编制人"};

        String idStr = pageData.getString("businessIdList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);

        //根据idList查询主表
        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryByBusinessId", listId);

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

        // 后续行  内容
        if (!CollectionUtils.isEmpty(projContractList)) {
            for (int i = 0; i < projContractList.size(); i++) {
                PageData pd = projContractList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                //序号
                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                //合同编号
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("serialNumber"));
                cell.setCellStyle(styleCell);

                //项目名称
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectName"));
                cell.setCellStyle(styleCell);

                //密级
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("secretsName"));
                cell.setCellStyle(styleCell);

                //合同状态
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("contractStatusName"));
                cell.setCellStyle(styleCell);

                //项目负责人
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectLeaderName"));
                cell.setCellStyle(styleCell);

                //岗位
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("leaderPostName"));
                cell.setCellStyle(styleCell);

                //所属单位名称
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("unitName"));
                cell.setCellStyle(styleCell);

                //起始年度
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("startDate"));
                cell.setCellStyle(styleCell);

                //结束年度
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("endDate"));
                cell.setCellStyle(styleCell);

                //编制人
                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("creatorUserName"));
                cell.setCellStyle(styleCell);
            }
        }

        // 设置自适应宽度
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
    private File createFile() {
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
     * 导出合同pdf文档
     *
     * @param pageData
     * @param zos
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    public void exportContractZip(PageData pageData, ZipOutputStream zos,
                                  ByteArrayOutputStream bos, String serialNumber) throws Exception {
        //List<String> idList = JSONArray.parseArray(pageData.getString("idList"), String.class);
        List<String> idList = JSONArray.parseArray(pageData.getString("idList"), String.class);

        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryById", idList);

        if (CollectionUtils.isEmpty(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        XWPFDocument doc = null;
        InputStream is = null;
        File file = this.createFile();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date = df.format(new Date());

        for (int i = 0; i < projContractList.size(); i++) {

            // 1. 从数据库中获取导出合同中的待替换信息
            Map<String, Object> params = new HashMap<>();
            //fillContractParams(params, projContractList.get(i));

            is = this.getClass().getResourceAsStream("/template/研究与开发项目合同.docx");
            doc = new XWPFDocument(is);

            // 内容与表格填充
            //xwpfTUtil.replaceInPara(doc, params);
            fillPlaceHolders(params, projContractList.get(i));
            xwpfTUtil.replaceInPara(doc, params);
            // 2.2 表格填充
            fillInfo2Tables(doc, projContractList.get(i));

            // 2. 在默认文件夹下创建临时文件
            //prefix :临时文件名的前缀,   suffix :临时文件名的后缀
            String serialNum = SerialNumberUtil.generateSerialNo("YFHT");
            String fileName = "项目合同签订管理_" + projContractList.get(i).getString("projectName") + "_" +
                    new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + serialNum;
            File newWordTempFile = new File(file, fileName + ".docx");
            String newWordTempFilePath = newWordTempFile.getCanonicalPath();
            File pdfTempFile = new File(file, fileName + ".pdf");
            String pdfTempFilePath = pdfTempFile.getCanonicalPath();

            // 3. 下载到临时word文件中 
            FileOutputStream os = new FileOutputStream(newWordTempFilePath);
            doc.write(os);
            xwpfTUtil.close(is);
            xwpfTUtil.close(os);

            // 4. 将word转为pdf
            Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);  //包含字体设置

            // 5. 将pdf文件数据写入到zip流中
            DownloadUtil.zipFile(fileName + ".pdf", pdfTempFile, zos);

            newWordTempFile.delete();
            pdfTempFile.delete();
        }
    }


    /**
     * 在同一pdf格式导出多个合同文档
     *
     * @param pageData
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    @Override
    public void exportContractPdf(PageData pageData, ByteArrayOutputStream bos, String serialNumber) throws Exception {
        List<String> idList = JSONArray.parseArray(pageData.getString("idList"), String.class);
        if (CollectionUtils.isEmpty(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryById", idList);

        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        XWPFDocument doc = null;
        InputStream is = null;
        File file = this.createFile();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date = df.format(new Date());

        PdfCopy copy = null;
        Document document = null;

        try {
            for (int i = 0; i < projContractList.size(); i++) {

                // 1. 从数据库中获取导出合同中的待替换信息
                Map<String, Object> params = new HashMap<>();

                // 2. 读取合同范本，替换其中指定占位符，填充表格内容
                is = this.getClass().getResourceAsStream("/template/研究与开发项目合同.docx");
                doc = new XWPFDocument(is);

                // 2.1 占位符替换
                fillPlaceHolders(params, projContractList.get(i));
                xwpfTUtil.replaceInPara(doc, params);
                // 2.2 表格填充
                fillInfo2Tables(doc, projContractList.get(i));

                // 2.3 临时word和pdf空文件构建
                String fileName = String.valueOf(i);

                File newWordTempFile = new File(file, fileName + ".docx");
                String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                File pdfTempFile = new File(file, fileName + ".pdf");
                String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                // 2.4 替换后的word内容下载到临时word文件中 
                FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                doc.write(os);
                xwpfTUtil.close(is);
                xwpfTUtil.close(os);

                // 3. 将内容替换后临时word转为临时pdf
                Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);  //包含字体设置

                // 4. 将新生成的临时pdf文档依次合并至document中
                // 4.1 document不存在，创建之
                PdfReader reader = new PdfReader(pdfTempFilePath);
                if (i == 0) {
                    document = new Document(reader.getPageSize(1));
                    //PdfWriter.getInstance(document, bos);
                    copy = new PdfSmartCopy(document, bos);
                    document.open();
                }

                // 4.2 将临时pdf合并到document中去
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }

                // 5. 关闭reader, 删除临时word和pdf文档
                reader.close();
                newWordTempFile.delete();
                pdfTempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭copy和document
            if (copy != null) {
                copy.close();
            }
            if (document != null) {
                document.close();
            }
        }
    }

    @Override
    public ResponseEntity
    printContractPdf(PageData pd, String serialNumber, HttpServletResponse response) throws Exception {
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);
        if (CollectionUtils.isEmpty(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryById", idList);

        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        XWPFDocument doc = null;
        InputStream is = null;
        File file = this.createFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        PdfCopy copy = null;
        Document document = null;

        try {
            for (int i = 0; i < projContractList.size(); i++) {

                // 1. 从数据库中获取导出合同中的待替换信息
                Map<String, Object> params = new HashMap<>();

                //fillContractParams(params, projContractList.get(i));

                // 2. 读取合同范本，替换其中指定占位符
                is = this.getClass().getResourceAsStream("/template/研究与开发项目合同.docx");
                doc = new XWPFDocument(is);

                // 2.1 占位符替换
                //xwpfTUtil.replaceInPara(doc, params);
                // 2.1 占位符替换
                fillPlaceHolders(params, projContractList.get(i));
                xwpfTUtil.replaceInPara(doc, params);
                // 2.2 表格填充
                fillInfo2Tables(doc, projContractList.get(i));

                // 2.3 临时word和pdf空文件构建
                String fileName = String.valueOf(i);

                File newWordTempFile = new File(file, fileName + ".docx");
                String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                File pdfTempFile = new File(file, fileName + ".pdf");
                String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                // 2.4 替换后的word内容下载到临时word文件中 
                FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                doc.write(os);
                xwpfTUtil.close(is);
                xwpfTUtil.close(os);

                // 3. 将内容替换后临时word转为临时pdf
                Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);  //包含字体设置

                // 4. 将新生成的临时pdf文档依次合并至document中
                // 4.1 document不存在，创建之
                PdfReader reader = new PdfReader(pdfTempFilePath);
                if (i == 0) {
                    document = new Document(reader.getPageSize(1));
                    //PdfWriter.getInstance(document, bos);
                    copy = new PdfSmartCopy(document, bos);
                    document.open();
                }

                // 4.2 将临时pdf合并到document中去
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }

                // 5. 关闭reader, 删除临时word和pdf文档
                reader.close();
                newWordTempFile.delete();
                pdfTempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 6.1 关闭copy
            if (copy != null) {
                copy.close();
            }

            // 6.2 将bos存为临时文件, 放入response返回, 以便预览.
            if (document != null) {
                // 临时文件创建
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String date = df.format(new Date());
                String pdfFileName = "项目合同签订管理_" + date + "_" + serialNumber;

                File pdfFile = new File(file, pdfFileName + ".pdf");
                String pdfFilePath = pdfFile.getCanonicalPath();

                FileOutputStream os = new FileOutputStream(pdfFile);
                os.write(bos.toByteArray());
                os.close();

                // 临时文件放入response返回
                Word2pdfUtil.fileResponse(pdfFilePath, response);

                // 文档关闭, 输出流关闭, 临时文件删除.
                document.close();
                bos.close();

                pdfFile.delete();
            }
        }

        return ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
    }


    /**
     * 项目合同封面占位符映射信息预填充
     *
     * @param params
     * @param projContrPd
     */
    @Override
    public int fillContrCoverPagePlaceholders(Map<String, Object> params, PageData projContrPd) throws ParseException {
        if (projContrPd.size() > 0) {
            // 1. 合同号和密级信息填充
            params.put("(secretsName)", projContrPd.getString("secretsName"));
            params.put("(serialNumber)", projContrPd.getString("serialNumber"));

            // 2. 项目立项主题信息填充
            String projectId = projContrPd.getString("projectId");
            PageData projApplyMainPd =
                    (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", projectId);

            if (projApplyMainPd != null) {
                String startYear = projApplyMainPd.getString("startYear");
                startYear = startYear.substring(0, startYear.indexOf("-"));
                params.put("(year)", startYear);

                params.put("(projectName)", projApplyMainPd.getString("projectName"));
                params.put("(unitName)", projApplyMainPd.getString("unitName"));
                params.put("(projectLeader)", projApplyMainPd.getString("applyUserName"));

                String endYear = projApplyMainPd.getString("endYear");
                endYear = endYear.substring(0, endYear.indexOf("-"));
                String dateRangeStr = startYear + "-" + endYear;
                params.put("(dateRange)", dateRangeStr);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String date = df.format(new Date());
                params.put("(printedDate)", Date2CnDate.convertDate2CnDate(date));

                return 1;
            } else {
                logger.error("未找到绑定的研发项目, request=[{}]", projectId);
            }
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }

        return 0;
    }


    /**
     * 项目合同中立项调研信息占位符预填充
     *
     * @param params
     * @param projContrPd
     */
    @Override
    public int fillContrSurveyInfoPlaceholders(Map<String, Object> params, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            PageData padgeData = new PageData();
            padgeData.put("businessId", projectId);
            List<PageData> projApplySurveyList =
                    (List<PageData>) baseDao.findForList("ProjApplyMainMapper.querySurveyByBusinessId", padgeData);

            if (projApplySurveyList.size() > 0) {
                PageData pd = projApplySurveyList.get(0);

                params.put("(currentSituation)", pd.getString("currentSituation"));
                params.put("(contentMethod)", pd.getString("contentMethod"));
                params.put("(targetResults)", pd.getString("targetResults"));

                return 1;
            } else {
                logger.error("未找到项目的立项调研信息, request=[{}]", projectId);
            }
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }

        return 0;
    }

    /**
     * 课题的年度计划及年度目标
     *
     * @param params
     * @param projContrPd
     */
    @Override
    public int fillContrProgressPlanParams(Map<String, String> params, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            PageData padgeData = new PageData();
            padgeData.put("businessId", projectId);
            List<PageData> projApplyProgessPlanList =
                    (List<PageData>) baseDao.findForList("ProjApplyMainMapper.queryProgressPlan", padgeData);

            if (projApplyProgessPlanList.size() > 0) {
                for (int i = 0; i < projApplyProgessPlanList.size(); i++) {
                    PageData pd = projApplyProgessPlanList.get(i);

                    params.put("years" + i, pd.getString("years"));
                    params.put("planTarget" + i, pd.getString("planTarget"));
                }
            } else {
                logger.error("未找到课题年度计划及年度目标, request=[{}]", projContrPd);
            }

            return projApplyProgessPlanList.size();
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }

        return 0;
    }

    /**
     * 课题的参加单位及分工
     *
     * @param params
     * @param projContrPd
     */
    @Override
    public int fillContrAttendUnitParams(Map<String, String> params, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            List<PageData> projApplyAttendUnitList =
                    (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryAttendUnit", projectId);

            if (projApplyAttendUnitList.size() > 0) {
                for (int i = 0; i < projApplyAttendUnitList.size(); i++) {
                    PageData pd = projApplyAttendUnitList.get(i);

                    //TODO: 项目负责人的单位是否需要放在首位？

                    params.put("unitName" + i, pd.getString("unitName"));
                    params.put("taskDivision" + i, pd.getString("taskDivision"));
                }
                return projApplyAttendUnitList.size();
            } else {
                logger.error("未找到课题年度计划及年度目标, request=[{}]", projContrPd);
            }
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }

        return 0;
    }

    /**
     * 项目负责人信息获取
     *
     * @param params
     * @param projContrPd
     */
    @Override
    public int getProjLeaderInfoParams(Map<String, String> params, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            List<PageData> projApplyResearchUserList =
                    (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryResearchUser", projectId);

            String projLeaderName = "";
            PageData projApplyMainPd =
                    (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", projectId);
            if (projApplyMainPd != null) {
                projLeaderName = projApplyMainPd.getString("applyUserName");
            }

            int number = 0;
            for (PageData pd : projApplyResearchUserList) {
                if (projLeaderName.compareTo(pd.getString("userName")) == 0) {
                    params.put("userName", pd.getString("userName"));
                    params.put("gender", pd.getString("gender"));
                    params.put("age", pd.getString("age"));
                    params.put("belongPost", pd.getString("belongPost"));
                    params.put("taskDivision", pd.getString("taskDivision"));
                    params.put("belongUnit", pd.getString("belongUnit"));
                    params.put("workRate", pd.getString("workRate"));
                    /*String workRateStr = pd.getString("workRate");
                    float iWorkRate = Float.parseFloat(workRateStr);//Float.valueOf(workRateStr);
                    String strWorkRate = "20%";
                    if ( iWorkRate >= 0.875 ) {
                        strWorkRate = "100%";
                    } else if ( iWorkRate >= 0.625 ) {
                        strWorkRate = "75%";
                    } else if ( iWorkRate >= 0.35 ) {
                        strWorkRate = "50%";
                    }
                    params.put("workRate", strWorkRate);
*/
                    number++;
                }
            }

            if (number == 0) {
                logger.error("未找到课题研究人员信息, request=[{}]", projContrPd);
            } else {
                return number;
            }
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }
        return 0;
    }

    /**
     * 合同范本中项目负责人信息占位符替换
     *
     * @param params
     * @param projContrPd
     */
    @Override
    public int fillContrProjLeaderInfoPlaceholders(Map<String, Object> params, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            List<PageData> projApplyResearchUserList =
                    (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryResearchUser", projectId);

            String projLeaderName = "";
            PageData projApplyMainPd =
                    (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", projectId);
            if (projApplyMainPd != null) {
                projLeaderName = projApplyMainPd.getString("applyUserName");
            }

            if (projApplyResearchUserList.size() > 0) {
                for (int i = 0; i < projApplyResearchUserList.size(); i++) {
                    PageData pd = projApplyResearchUserList.get(i);

                    String researchName = pd.getString("userName");
                    if (projLeaderName.compareTo(researchName) == 0) {
                        params.put("(projLeaderName)", pd.getString("userName"));
                        params.put("(sex)", pd.getString("gender"));
                        params.put("(age)", pd.getString("age"));
                        params.put("(postName)", pd.getString("belongPost"));
                        params.put("(taskDivision)", pd.getString("taskDivision"));
                        params.put("(workRate)", pd.getString("workRate"));
                        params.put("(belongUnit)", pd.getString("belongUnit"));

                        return 1;
                    } /*else {
                        logger.error("未找到项目负责人信息, request=[{}]", projContrPd);
                    }*/
                }
            } else {
                logger.error("未找到课题研究人员信息, request=[{}]", projContrPd);
            }
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }

        return 0;
    }

    /**
     * 获取项目参与人信息
     *
     * @param params
     * @param projContrPd
     */
    @Override
    public int getParticipantsInfoParams(Map<String, String> params, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            List<PageData> projApplyResearchUserList =
                    (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryResearchUser", projectId);

            String projLeaderName = "";
            PageData projApplyMainPd =
                    (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", projectId);
            if (projApplyMainPd != null) {
                projLeaderName = projApplyMainPd.getString("applyUserName");
            }

            if (projApplyResearchUserList.size() > 0) {
                int participantsNum = 0;
                for (int i = 0; i < projApplyResearchUserList.size(); i++) {
                    PageData pd = projApplyResearchUserList.get(i);

                    String researchName = pd.getString("userName");

                    if (projLeaderName.compareTo(researchName) == 0) {
                        continue;
                    }

                    params.put("userName" + participantsNum, pd.getString("userName"));
                    params.put("gender" + participantsNum, pd.getString("gender"));
                    params.put("age" + participantsNum, pd.getString("age"));
                    params.put("postName" + participantsNum, pd.getString("belongPost"));
                    params.put("taskDivision" + participantsNum, pd.getString("taskDivision"));
                    params.put("workRate" + participantsNum, pd.getString("workRate"));
                    params.put("belongUnit" + participantsNum, pd.getString("belongUnit"));

                    participantsNum++;
                }

                return participantsNum;
            } else {
                logger.error("未找到课题研究人员信息, request=[{}]", projContrPd);
            }
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }

        return 0;
    }

    /**
     * 获取项目资金预算信息
     *
     * @param srcAccountMap
     * @param expAccountMap
     * @param projContrPd
     */
    @Override
    public int getFundsBudgetParams(Map<String, String> srcAccountMap,
                                    Map<String, String> expAccountMap, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            List<PageData> projApplyFundBudgetList =
                    (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryBudget", projectId);

            if (projApplyFundBudgetList.size() > 0) {
                for (int i = 0; i < projApplyFundBudgetList.size(); i++) {
                    PageData pd = projApplyFundBudgetList.get(i);

                    if (pd.getString("sourceAccount").length() > 0) {
                        srcAccountMap.put(pd.getString("sourceAccount"), pd.getString("sourceBudget"));
                    }

                    if (pd.getString("expenseAccount").length() > 0) {
                        expAccountMap.put(pd.getString("expenseAccount"), pd.getString("expenseBudget"));
                    }
                }

                //TODO: 需要按照大写数字和中文数字排序吗？

                return projApplyFundBudgetList.size();
            } else {
                logger.error("未找到课题预算信息表, request=[{}]", projContrPd);
            }
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }

        return 0;
    }

    /**
     * 获取项目各年度拨款计划信息
     *
     * @param appnPlanMap
     * @param projContrPd
     */
    @Override
    public int getAppnPlanParams(Map<Integer, String> appnPlanMap, PageData projContrPd) {
        if (projContrPd.size() > 0) {
            String projectId = projContrPd.getString("projectId");
            List<PageData> projAppropriationPlanList =
                    (List<PageData>) baseDao.findForList("ProjApplyMainMapper.queryAppropriationPlan", projectId);

            if (projAppropriationPlanList.size() > 0) {
                for (int i = 0; i < projAppropriationPlanList.size(); i++) {
                    PageData pd = projAppropriationPlanList.get(i);
                    appnPlanMap.put(Integer.valueOf(pd.getString("years")), pd.getString("planAmount"));
                }
            } else {
                logger.error("未找到课题拨款计划表, request=[{}]", projContrPd);
            }

            return projAppropriationPlanList.size();
        } else {
            logger.error("项目合同未找到, request=[{}]", projContrPd);
        }
        return 0;
    }

    /**
     * 项目研发合同范本中表格填充总入口
     *
     * @param doc
     * @param projContrPd
     */
    @Override
    public void fillInfo2Tables(XWPFDocument doc, PageData projContrPd) throws IOException, XmlException {
        if (null == doc) {
            logger.error("合同模板无效, request=[{}]", projContrPd);
        } else if (null == projContrPd ||
                projContrPd.getString("projectName").length() <= 0 ||
                projContrPd.getString("projectName") == "") {
            logger.error("项目合同信息错误, request=[{}]", projContrPd);
        } else {
            List<XWPFTable> xwpfTableList = doc.getTables();
            if (xwpfTableList != null) {
                // 1. 获取并填充第一张表格，即"四、课题的年度计划及年度目标"表
                XWPFTable xwpfProgPlanTable = xwpfTableList.get(0);
                fillPlanTarget2Table(xwpfProgPlanTable, projContrPd);

                // 2. 获取第二张表格，即"五、课题参加单位及主要研究人员"表, 填充参与单位信息
                XWPFTable xwpfAttendUnitTable = xwpfTableList.get(1);
                fillAttendUnit2Table(xwpfAttendUnitTable, projContrPd);

                // 3. 获取第三张表格，即"六、经费预算"表, 填充经费预算信息
                XWPFTable xwpfFundsBudgetTable = xwpfTableList.get(2);
                fillFundsBudget2Table(xwpfFundsBudgetTable, projContrPd);

                // 4. 获取第四张表格，即"七、拨款计划"表, 填充拨款计划信息
                XWPFTable xwpfAppnPlanTable = xwpfTableList.get(3);
                fillAppnPlan2Table(xwpfAppnPlanTable, projContrPd);

                XWPFTable xwpfContrConsigneeTable = xwpfTableList.get(4);
                fillConsigneeUnit2Table(xwpfContrConsigneeTable, projContrPd);

            }
        }
    }

    /**
     * 向新创建的XWPFTableCell表格添加文本text
     *
     * @param cell
     * @param text
     */
    @Override
    public void XWPFTableCellSetText(XWPFTableCell cell, String text) {
        if (cell != null) {
            for (XWPFParagraph p : cell.getParagraphs()) {
                List<XWPFRun> rList = p.getRuns();
                if (rList.size() <= 0) {
                    p.createRun().setText(text);
                } else {
                    for (XWPFRun r : rList) {
                        r.setText(text, 0);
                    }
                }
            }
        }
    }

    /**
     * 项目研究计划与目标表格填充
     *
     * @param xwpfTbl
     * @param projContrPd
     */
    @Override
    public void fillPlanTarget2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException {
        if (xwpfTbl != null) {
            // 1.1 根据项目年度计划数和表中空行个数，插入所需数量的空行。其中，空行格式集成预置的第一个空行
            int tblRowNum = xwpfTbl.getNumberOfRows();
            Map<String, String> params = new HashMap<>();
            int expectRowNum = fillContrProgressPlanParams(params, projContrPd);

            XWPFTableRow tableRow1 = xwpfTbl.getRow(1);
            if (tblRowNum - 2 < expectRowNum) {
                // 模板表格预置了表头和一个格式参考空行, 参考行后面插入新的空行
                for (int i = 0; i < expectRowNum - tblRowNum + 2; i++) {
                    CTRow ctrow = CTRow.Factory.parse(tableRow1.getCtRow().newInputStream()); //重点行
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, xwpfTbl);

                    // 1.1.1 为年度单元格添加文本
                    XWPFTableCell yearCell = newRow.getTableCells().get(0);
                    XWPFTableCellSetText(yearCell, params.get("years" + i));

                    // 1.1.2 为年度计划单元格添加文本
                    XWPFTableCell planTargetCell = newRow.getTableCells().get(1);
                    XWPFTableCellSetText(planTargetCell, params.get("planTarget" + i));

                    // 1.1.2 添加行至表格
                    xwpfTbl.addRow(newRow, i + 2);
                }
            }

            // 2. 删除格式参考行
            xwpfTbl.removeRow(1);
        }
    }

    /**
     * 项目参与单位表格填充
     *
     * @param xwpfTbl
     * @param projContrPd
     */
    @Override
    public void fillAttendUnit2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException {
        if (xwpfTbl != null) {

            // 1. 根据项目参与单位数和表中空行个数，插入所需数量的空行。其中，空行格式集成预置的第一个空行
            int tblRowNum = 2;
            Map<String, String> params = new HashMap<>();
            int expectRowNum = fillContrAttendUnitParams(params, projContrPd);

            XWPFTableRow tableRow1 = xwpfTbl.getRow(1);
            if (tblRowNum - 2 < expectRowNum) {
                // 模板表格预置了表头和一个格式参考空行, 参考行后面插入新的空行
                for (int i = 0; i < expectRowNum - tblRowNum + 2; i++) {
                    CTRow ctrow = CTRow.Factory.parse(tableRow1.getCtRow().newInputStream()); //重点行
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, xwpfTbl);

                    // 1.1.1 为参加单位单元格添加文本
                    XWPFTableCell unitNameCell = newRow.getTableCells().get(0);
                    XWPFTableCellSetText(unitNameCell, params.get("unitName" + i));

                    // 1.1.2 为单位任务单元格添加文本
                    XWPFTableCell taskDivisionCell = newRow.getTableCells().get(1);
                    XWPFTableCellSetText(taskDivisionCell, params.get("taskDivision" + i));

                    // 1.1.2 添加行至表格
                    xwpfTbl.addRow(newRow, i + 2);
                }
            }

            // 1.2 删除格式参考行
            xwpfTbl.addRow(tableRow1, expectRowNum + 2);
            xwpfTbl.removeRow(1);

            // 2. 项目负责人信息填写
            int index = expectRowNum + 2;
            params.clear();
            fillProjLeaderInfo2Table(xwpfTbl, projContrPd, index);

            // 3. 主要研究人员信息填写
            int LeaderNum = getProjLeaderInfoParams(params, projContrPd);
            index += LeaderNum + 2;
            params.clear();
            getParticipantsInfoParams(params, projContrPd);
            fillParticipantsInfo2Table(xwpfTbl, projContrPd, index);
        }
    }


    /**
     * 项目负责人表格填充
     *
     * @param xwpfTbl
     * @param projContrPd
     * @param index
     */
    @Override
    public void fillProjLeaderInfo2Table(XWPFTable xwpfTbl, PageData projContrPd, int index)
            throws IOException, XmlException {
        if (xwpfTbl != null) {
            // 1. 项目负责人信息填充
            int tblRowNum = 2;
            Map<String, String> params = new HashMap<>();
            int expectRowNum = getProjLeaderInfoParams(params, projContrPd);

            XWPFTableRow tableRowRef = xwpfTbl.getRow(index + 1);
            if (tblRowNum - 2 < expectRowNum) {
                for (int i = 0; i < expectRowNum - tblRowNum + 2; i++) {
                    CTRow ctrow = CTRow.Factory.parse(tableRowRef.getCtRow().newInputStream()); //重点行
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, xwpfTbl);

                    // 1.1 为项目负责人姓名单元格添加文本
                    XWPFTableCell userNameCell = newRow.getTableCells().get(0);
                    XWPFTableCellSetText(userNameCell, params.get("userName"));

                    // 1.2 为项目负责人性别单元格添加文本
                    XWPFTableCell genderCell = newRow.getTableCells().get(1);
                    XWPFTableCellSetText(genderCell, params.get("gender"));

                    // 1.3 为项目负责人年龄单元格添加文本
                    XWPFTableCell ageCell = newRow.getTableCells().get(2);
                    XWPFTableCellSetText(ageCell, params.get("age"));

                    // 1.4 为项目负责人职务单元格添加文本
                    XWPFTableCell belongPostCell = newRow.getTableCells().get(3);
                    XWPFTableCellSetText(belongPostCell, params.get("belongPost"));

                    // 1.5 为项目负责人任务与分工单元格添加文本
                    XWPFTableCell taskDivisionCell = newRow.getTableCells().get(4);
                    XWPFTableCellSetText(taskDivisionCell, params.get("taskDivision"));

                    // 1.6 为项目负责人全时率单元格添加文本
                    XWPFTableCell workRateCell = newRow.getTableCells().get(5);
                    XWPFTableCellSetText(workRateCell, params.get("workRate"));

                    // 1.7 为项目负责人所在单位单元格添加文本
                    XWPFTableCell belongUnitCell = newRow.getTableCells().get(6);
                    XWPFTableCellSetText(belongUnitCell, params.get("belongUnit"));

                    // 1.8 添加行至表格
                    xwpfTbl.addRow(newRow, index + 2);
                }
            }

            // 2. 删除格式参考行, 添加隔离行
            xwpfTbl.addRow(tableRowRef, index + 3);
            xwpfTbl.removeRow(index + 1);
        }
    }

    /**
     * 项目参与人员表格填充
     *
     * @param xwpfTbl
     * @param projContrPd
     * @param index
     */
    @Override
    public void fillParticipantsInfo2Table(XWPFTable xwpfTbl, PageData projContrPd, int index)
            throws IOException, XmlException {
        if (xwpfTbl != null) {
            // 1. 项目负责人信息填充
            int tblRowNum = 2;
            Map<String, String> params = new HashMap<>();
            int expectRowNum = getParticipantsInfoParams(params, projContrPd);

            XWPFTableRow tableRowRef = xwpfTbl.getRow(index + 1);
            if (tblRowNum - 2 < expectRowNum) {
                for (int i = 0; i < expectRowNum - tblRowNum + 2; i++) {
                    CTRow ctrow = CTRow.Factory.parse(tableRowRef.getCtRow().newInputStream()); //重点行
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, xwpfTbl);

                    // 1.1 为项目参与人姓名单元格添加文本
                    XWPFTableCell userNameCell = newRow.getTableCells().get(0);
                    XWPFTableCellSetText(userNameCell, params.get("userName" + i));

                    // 1.2 为项目参与人性别单元格添加文本
                    XWPFTableCell genderCell = newRow.getTableCells().get(1);
                    XWPFTableCellSetText(genderCell, params.get("gender" + i));

                    // 1.3 为项目参与人年龄单元格添加文本
                    XWPFTableCell ageCell = newRow.getTableCells().get(2);
                    XWPFTableCellSetText(ageCell, params.get("age" + i));

                    // 1.4 为项目参与人职务单元格添加文本
                    XWPFTableCell belongPostCell = newRow.getTableCells().get(3);
                    XWPFTableCellSetText(belongPostCell, params.get("belongPost" + i));

                    // 1.5 为项目参与人任务与分工单元格添加文本
                    XWPFTableCell taskDivisionCell = newRow.getTableCells().get(4);
                    XWPFTableCellSetText(taskDivisionCell, params.get("taskDivision" + i));

                    // 1.6 为项目参与人全时率单元格添加文本
                    XWPFTableCell workRateCell = newRow.getTableCells().get(5);
                    XWPFTableCellSetText(workRateCell, params.get("workRate" + i));

                    // 1.7 为项目参与人所在单位单元格添加文本
                    XWPFTableCell belongUnitCell = newRow.getTableCells().get(6);
                    XWPFTableCellSetText(belongUnitCell, params.get("belongUnit" + i));

                    // 1.8 添加行至表格
                    xwpfTbl.addRow(newRow, index + 2);
                }
            }

            // 2. 删除格式参考行, 添加隔离行
            //xwpfTbl.addRow(tableRowRef, index + 3);
            xwpfTbl.removeRow(index + 1);
        }
    }

    /**
     * 经费预算表格填充
     *
     * @param xwpfTbl
     * @param projContrPd
     * @param index
     */
    @Override
    public void fillFundsBudget2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException {
        if (xwpfTbl != null) {
            // 1. 项目经费收支信息填充
            int tblRowNum = 3;
            Map<String, String> srcAccParams = new HashMap<>();
            Map<String, String> expAccParams = new HashMap<>();

            int expectRowNum = getFundsBudgetParams(srcAccParams, expAccParams, projContrPd);

            // 2. 收支信息项目排序
            TreeMap<Integer, String> srcAccKeys = sortFundsBudget(srcAccParams);
            TreeMap<Integer, String> expAccKeys = sortFundsBudget(expAccParams);

            // 3. 收支信息项目及其金额插入表格
            Iterator srcAccIter = srcAccKeys.entrySet().iterator();
            Iterator expAccIter = expAccKeys.entrySet().iterator();

            XWPFTableRow tableRowRef = xwpfTbl.getRow(2);
            if (tblRowNum - 3 < expectRowNum) {
                for (int i = 0; i < expectRowNum - tblRowNum + 3; i++) {
                    CTRow ctrow = CTRow.Factory.parse(tableRowRef.getCtRow().newInputStream()); //重点行
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, xwpfTbl);

                    // 3.1 经费来源项目及其金额填充
                    if ( srcAccIter.hasNext() ) {
                        Map.Entry me = (Map.Entry) srcAccIter.next();
                        Integer key = (Integer)me.getKey();
                        String value = srcAccKeys.get(key);

                        // 3.1.1 为经费来源列支项添加文本
                        XWPFTableCell srcAccNameCell = newRow.getTableCells().get(0);
                        XWPFTableCellSetText(srcAccNameCell, value);

                        // 3.1.2 为经费来源列支项金额添加文本
                        XWPFTableCell srcAccValueCell = newRow.getTableCells().get(1);
                        XWPFTableCellSetText(srcAccValueCell, srcAccParams.get(value));
                    }

                    // 3.2 经费支出项目及其金额填充
                    if ( expAccIter.hasNext() ) {
                        Map.Entry me = (Map.Entry) expAccIter.next();
                        Integer key = (Integer)me.getKey();
                        String value = expAccKeys.get(key);

                        // 3.2.1 为经费支出列支项添加文本
                        XWPFTableCell expAccNameCell = newRow.getTableCells().get(2);
                        XWPFTableCellSetText(expAccNameCell, value);

                        // 3.2.2 为经费支出列支项金额添加文本
                        XWPFTableCell expAccValueCell = newRow.getTableCells().get(3);
                        XWPFTableCellSetText(expAccValueCell, expAccParams.get(value));
                    }

                    xwpfTbl.addRow(newRow, i + 3);
                }
            }
            // 4. 删除格式参考行, 添加隔离行
            //xwpfTbl.addRow(tableRowRef, index + 3);
            xwpfTbl.removeRow(2);
        }
    }

    /**
     * 拨款计划表格填充
     *
     * @param xwpfTbl
     * @param projContrPd
     * @param index
     */
    @Override
    public void fillAppnPlan2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException {
        if (xwpfTbl != null) {
            // 1. 项目经费收支信息填充
            int tblRowNum = 2;
            Map<Integer, String> params = new TreeMap<>();

            int expectRowNum = getAppnPlanParams(params, projContrPd);

            XWPFTableRow rowYearRef = xwpfTbl.getRow(0);
            XWPFTableRow rowBudgetRef = xwpfTbl.getRow(1);

            Iterator iter = params.entrySet().iterator();
            for (int i = 0; i < Math.ceil(expectRowNum/3.0); i++ ) {
                CTRow ctrowYear = CTRow.Factory.parse(rowYearRef.getCtRow().newInputStream()); //重点行
                XWPFTableRow newRowYear = new XWPFTableRow(ctrowYear, xwpfTbl);

                CTRow ctrowBudget = CTRow.Factory.parse(rowBudgetRef.getCtRow().newInputStream()); //重点行
                XWPFTableRow newRowBudget = new XWPFTableRow(ctrowBudget, xwpfTbl);

                for (int j = 0; j < 3; j++ ) {
                    // 3.1.1 为经费来源列支项添加文本
                    if ( iter.hasNext() ) {
                        Map.Entry me = (Map.Entry) iter.next();
                        Integer year = (Integer) me.getKey();
                        String value = params.get(year);

                        XWPFTableCell yearCell = newRowYear.getTableCells().get(j+1);
                        XWPFTableCellSetText(yearCell, year.toString());

                        XWPFTableCell planAmountCell = newRowBudget.getTableCells().get(j+1);
                        XWPFTableCellSetText(planAmountCell, value);
                    } else {
                        break;
                    }
                }

                xwpfTbl.addRow(newRowYear, i*2 + 2);
                xwpfTbl.addRow(newRowBudget, i*2 + 3);
            }
            // 4. 删除格式参考行, 添加隔离行
            xwpfTbl.removeRow(0);
            xwpfTbl.removeRow(0);
        }
    }

    /**
     * 项目合同签订双方（受委托）信息填写表格
     *
     * @param xwpfTbl
     * @param projContrPd
     */
    @Override
    public void fillConsigneeUnit2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException {
        if (xwpfTbl != null) {
            // 1. 项目签订双方信息填充
            //int tblRowNum = 2;
            Map<String, String> params = new HashMap<>();

            int expectRowNum = getProjLeaderInfoParams(params, projContrPd);

            XWPFTableRow consignUnitRowRef = xwpfTbl.getRow(12);

            CTRow ctrow = CTRow.Factory.parse(consignUnitRowRef.getCtRow().newInputStream()); //重点行
            XWPFTableRow newRow = new XWPFTableRow(ctrow, xwpfTbl);

            XWPFTableCell consignUnitCell = newRow.getTableCells().get(0);
            XWPFTableCellSetText(consignUnitCell, consignUnitRowRef.getCell(0).getText()+params.get("belongUnit"));

            xwpfTbl.addRow(newRow, 12);
            xwpfTbl.removeRow(13);
        }

    }


    /**
     * 项目合同占位符映射信息预填充
     *
     * @param params
     * @param projContractPd
     */
    @Override
    public void fillContractParams(Map<String, Object> params, PageData projContractPd) throws ParseException {
        params.put("(secretsName)", projContractPd.getString("secretsName"));
        params.put("(serialNumber)", projContractPd.getString("serialNumber"));

        // 1. 项目立项主题信息填充
        String projectId = projContractPd.getString("projectId");
        PageData projApplyMainPd =
                (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", projectId);
        if (projApplyMainPd == null) {

            params.put("(year)", "");
            params.put("(projectName)", "");
            params.put("(unitName)", "");
            params.put("(projectLeader)", "");
            params.put("(dateRange)", "");
            params.put("(printedDate)", "");
            params.put("(post)", "");
            params.put("(unit)", "");

            logger.error("1. 合同签订pdf导出-项目立项表缺少合同所属立项项目信息, request=[{}]", projectId);
        } else {
            String startYear = projApplyMainPd.getString("startYear");
            startYear = startYear.substring(0, startYear.indexOf("-"));
            params.put("(year)", startYear);

            params.put("(projectName)", projApplyMainPd.getString("projectName"));
            params.put("(unitName)", projApplyMainPd.getString("unitName"));
            params.put("(projectLeader)", projApplyMainPd.getString("applyUserName"));

            String endYear = projApplyMainPd.getString("endYear");
            endYear = endYear.substring(0, endYear.indexOf("-"));
            String dateRangeStr = startYear + "-" + endYear;
            params.put("(dateRange)", dateRangeStr);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            String date = df.format(new Date());
            params.put("(printedDate)", Date2CnDate.convertDate2CnDate(date));

            params.put("(post)", projApplyMainPd.getString("belongPost"));
            params.put("(unit)", projApplyMainPd.getString("belongUnit"));

            // 2 项目立项详情附表中信息填充
            PageData pd = new PageData();
            pd.put("businessId", projectId);
            PageData pdApplyDetail =
                    (PageData) baseDao.findForObject("ProjectApplyMapper.queryApplyDetail", pd);

            if (pdApplyDetail == null) {
                params.put("(currentSituation)", "");
                params.put("(contentMethod)", "");
                params.put("(targetResults)", "");

                logger.error("2. 合同签订pdf导出-项目立项详情附表中缺少立项合同所属详情信息, request=[{}]", pd);
            } else {
                params.put("(currentSituation)", pdApplyDetail.getString("currentSituation"));
                params.put("(contentMethod)", pdApplyDetail.getString("contentMethod"));
                params.put("(targetResults)", pdApplyDetail.getString("targetResults"));
            }

            // 3 项目立项研究人员附表信息填充
            List<PageData> pdApplyResearchUserList = (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryResearchUser", pd);
            if (pdApplyResearchUserList == null) {
                params.put("(name)", "");
                params.put("(sax)", "");
                params.put("(age)", "");
                params.put("(task)", "");
                params.put("(rate)", "");

                logger.error("2. 合同签订pdf导出-研究人员附表中缺少立项合同所属研究人员信息, request=[{}]", pd);
            } else {
                // 3.1 研究人员附表中第一个研究人员作为合同负责人放入合同
                PageData pdApplyResearchUser = pdApplyResearchUserList.get(0);

                params.put("(name)", pdApplyResearchUser.getString("userName"));
                params.put("(sax)", pdApplyResearchUser.getString("gender"));
                params.put("(age)", pdApplyResearchUser.getString("age"));
                params.put("(task)", pdApplyResearchUser.getString("taskDivision"));
                params.put("(rate)", pdApplyResearchUser.getString("workRate"));
            }
        }
    }

    /**
     * 文档中占位符替换
     *
     * @param params
     * @param projContractPd
     */
    @Override
    public void fillPlaceHolders(Map<String, Object> params, PageData projContractPd) throws ParseException {
        fillContrCoverPagePlaceholders(params, projContractPd);
        fillContrSurveyInfoPlaceholders(params, projContractPd);
    }

    /**
     * 经费预算列支项排序
     * 【备注】：目前仅支持"其他经费”列支项包含详细列支条目的情况
     *
     * @param params
     */
    @Override
    public TreeMap<Integer, String> sortFundsBudget(Map<String, String> params) {
        TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>();

        int num = 0;
        int baseNum = 100000;
        int secondNum = 0;

        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();

            num = 0;
            // 获取其他费用标号
            if ( true == key.contains("其他费用") ) {
                int sit = key.indexOf("、");

                String subStr = key.substring(0, sit);
                num = 1000 * NumberCN2Arab.numberCN2Arab(subStr);
                secondNum = num;

                treeMap.put(Integer.valueOf(baseNum + num), key);

                break;
            }
        }

        iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();

            if ( true == key.contains("其他费用") ) {
                continue;
            }

            num = 0;
            int sit = key.indexOf("、");
            if ( sit > 0 ) {
                String subStr = key.substring(0, sit);
                num = 1000 * NumberCN2Arab.numberCN2Arab(subStr);

                // 标号不是中文数字, 尝试获取阿拉伯标号
                if ( 0 == num ) {
                    int a = 10 * Integer.parseInt(subStr);
                    num = secondNum + a;
                }
            }

            treeMap.put(Integer.valueOf(baseNum + num), key);
        }

        return treeMap;
    }
}




