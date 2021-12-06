package com.rdexpense.manager.service.projContract.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.rdexpense.manager.service.projContract.ProjContractSignService;
import com.rdexpense.manager.util.Date2CnDate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ProjContractSignServiceImpl implements ProjContractSignService {
    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;


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
        if (!CollectionUtils.isEmpty(projContractInfoList)) {
            throw new MyException("项目合同已存在");
        }

        String businessId = "YGGL" + UUID.randomUUID().toString(); //生成业务主键ID
        pd.put("businessId", businessId);

        String serialNumber = SequenceUtil.generateSerialNo();
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

        if ( !CollectionUtils.isEmpty(projContractList )) {
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
        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.selectAll", pd);

        if( !CollectionUtils.isEmpty(projContractList) ) {
            return projContractList;
        }

        return projContractList;
    }

    /**
     * 根据process_id查询项目合同列表
     * @return
     */
    public List<PageData> queryByBusinessId(PageData pd) {
        List<PageData> projContractList =
                (List<PageData>) baseDao.findForList("ProjContractSignMapper.queryByBusinessId", pd);

        if( !CollectionUtils.isEmpty(projContractList) ) {
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
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length-1));

        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for ( int j = 0; j < head.length; j++ ) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }

        // 后续行  内容
        if ( !CollectionUtils.isEmpty(projContractList) ) {
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
        for ( int j = 0; j < head.length; j++ ) {
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
     * @param pageData
     * @param zos
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    public void exportContractZip(PageData pageData, ZipOutputStream zos,
                                  ByteArrayOutputStream bos, String serialNumber) throws Exception {
        String idStr = pageData.getString("idList");
        List<PageData> listId = JSONObject.parseArray(idStr, PageData.class);
        if ( listId.isEmpty() ) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        XWPFDocument doc = null;
        InputStream is = null;
        File file = this.createFile();

        for ( int i = 0; i < listId.size(); i++ ) {
            PageData pd = listId.get(i);
            PageData projContractPd =
                    (PageData) baseDao.findForObject("ProjContractSignMapper.queryOneByBusinessId", pd.getString("businessId"));
            PageData projApplyMainPd =
                    (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", pd.getString("projectId"));

            is = this.getClass().getResourceAsStream("/template/研究与开发项目合同范本.docx");
            doc = new XWPFDocument(is);

            Map<String, Object> params = new HashMap<>();
            String startDate = projApplyMainPd.getString("startYear");
            params.put("(startYear)", startDate.substring(0, startDate.indexOf("-")));
            params.put("(projectLeaderName)", projApplyMainPd.getString("applyUserName"));
            params.put("(gender)", projApplyMainPd.getString("gender"));
            params.put("(age)", projApplyMainPd.getString("age"));
            params.put("(postName)", projApplyMainPd.getString("postName"));
            params.put("(unitName)", projApplyMainPd.getString("unitName"));
            params.put("(projUnitName)", projApplyMainPd.getString("unitName"));
            params.put("(createdDate)", Date2CnDate.convertDate2CnDate(projContractPd.getString("createdDate")));

            xwpfTUtil.replaceInPara(doc, params);

            //  在默认文件夹下创建临时文件
            //prefix :临时文件名的前缀,   suffix :临时文件名的后缀
            String serialNum = SerialNumberUtil.generateSerialNo("planarSectionalViews");
            String fileName = "项目合同签订管理_" + projApplyMainPd.getString("projectName") + "_" +
                    new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + serialNum;
            File newWordTempFile = new File(file, fileName + ".docx");
            String newWordTempFilePath = newWordTempFile.getCanonicalPath();
            File pdfTempFile = new File(file, fileName + ".pdf");
            String pdfTempFilePath = pdfTempFile.getCanonicalPath();

            //下载到临时word文件中 
            FileOutputStream os = new FileOutputStream(newWordTempFilePath);
            doc.write(os);
            xwpfTUtil.close(is);
            xwpfTUtil.close(os);

            //将word转为pdf
            Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);  //包含字体设置

            //将pdf文件数据写入到zip流中
            DownloadUtil.zipFile(fileName + ".pdf", pdfTempFile, zos);

            newWordTempFile.delete();
            pdfTempFile.delete();
        }
    }

    /**
     * 在同一pdf格式导出多个合同文档
     * @param pageData
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    @Override
    public void exportContractPdf(PageData pageData, ByteArrayOutputStream bos, String serialNumber) throws Exception {
        String idStr = pageData.getString("idList");
        List<PageData> listId = JSONObject.parseArray(idStr, PageData.class);
        if ( listId.isEmpty() ) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        XWPFDocument doc = null;
        InputStream is = null;
        File file = this.createFile();

        String pdfFileName = "项目合同签订管理_" + serialNumber;
        File pdfFile = new File(file, pdfFileName + ".pdf");
        String pdfFilePath = pdfFile.getCanonicalPath();

        PdfCopy copy = null;
        Document document = null;

        try {
            // 1. 待导出合同businessId信息获取
            for ( int i = 0; i < listId.size(); i++ ) {
                PageData pd = listId.get(i);
                PageData projContractPd =
                        (PageData) baseDao.findForObject("ProjContractSignMapper.queryOneByBusinessId", pd.getString("businessId"));
                PageData projApplyMainPd =
                        (PageData) baseDao.findForObject("ProjApplyMainMapper.queryOneByBusinessId", pd.getString("projectId"));

                is = this.getClass().getResourceAsStream("/template/研究与开发项目合同范本.docx");
                doc = new XWPFDocument(is);

                // 2. 合同模板内容替换
                Map<String, Object> params = new HashMap<>();
                String startDate = projApplyMainPd.getString("startYear");
                params.put("(startYear)", startDate.substring(0, startDate.indexOf("-")));
                params.put("(projectLeaderName)", projApplyMainPd.getString("applyUserName"));
                params.put("(gender)", projApplyMainPd.getString("gender"));
                params.put("(age)", projApplyMainPd.getString("age"));
                params.put("(postName)", projApplyMainPd.getString("postName"));
                params.put("(unitName)", projApplyMainPd.getString("unitName"));
                params.put("(projUnitName)", projApplyMainPd.getString("unitName"));
                params.put("(createdDate)", Date2CnDate.convertDate2CnDate(projContractPd.getString("createdDate")));

                xwpfTUtil.replaceInPara(doc, params);

                String serialNum = SerialNumberUtil.generateSerialNo("planarSectionalViews");
                String fileName = String.valueOf(i);
                File newWordTempFile = new File(file, fileName + ".docx");
                String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                File pdfTempFile = new File(file, fileName + ".pdf");
                String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                //下载到临时word文件中 
                FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                doc.write(os);
                xwpfTUtil.close(is);
                xwpfTUtil.close(os);

                // 3. 将内容替换后word转为pdf
                Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);  //包含字体设置

                // 4. 将新生成的pdf文档合并至前面的pdf中
                if ( i == 0 ) {
                    document = new Document(new PdfReader(pdfTempFilePath).getPageSize(1));
                    //PdfWriter.getInstance(document, bos);
                    copy = new PdfCopy(document, bos);
                    document.open();
                }

                PdfReader reader = new PdfReader(pdfTempFilePath);
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }

                newWordTempFile.delete();
                pdfTempFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
