package com.rdexpense.manager.service.progressreport;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author dengteng
 * @title: ProgressReportService
 * @projectName rdexpense-back
 * @description: TODO
 * @date 2021/11/24
 */
public interface ProgressReportService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryReportList(PageData pd);

    /**
     * 新增项目研发进展报告
     * @return
     */
    void addReport(PageData pd);

    /**
     * 查询项目进展报告单条记录详情
     * @param pd
     * @return
     */
    PageData getReportDetail(PageData pd);

    /**
     * 修改项目进展报告
     * @param pd
     */
    void updateReport(PageData pd);

    /**
     * 删除进展报告
     * @param pd
     */
    void deleteReport(PageData pd);

    /**
     * 提交项目进展报告
     * @param pd
     */
    void submitReport(PageData pd);

    void repealedReport(PageData pd);

    HSSFWorkbook exportExcel(PageData pageData);


    void export(PageData pd, ZipOutputStream zos, ByteArrayOutputStream bos, String serialNumber) throws Exception;

    ResponseEntity export2(PageData pd, HttpServletResponse response) throws Exception;

    File createFile();
    /**
     * 获取单条或多条项目进展报告记录
     * @param pd
     * @return
     */
    List<PageData> getReportDetailList(PageData pd);
}
