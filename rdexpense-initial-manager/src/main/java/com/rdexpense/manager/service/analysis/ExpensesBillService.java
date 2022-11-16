package com.rdexpense.manager.service.analysis;


import com.common.entity.PageData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface ExpensesBillService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryList(PageData pd);

    /**
     * 删除
     * @param pd
     * @return
     */
    void deleteApply(PageData pd);



    /**
     * 导入数据
     *
     * @param pd
     * @return
     */
    void uploadAll(MultipartFile file, PageData pd);



    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(String businessId) throws Exception;

    /**
     * 导出excel压缩包
     * @param businessIdList
     * @param zos
     * @param bos
     * @param filePrefix
     * @throws Exception
     */
    void exportZip(int flag, List<String> businessIdList, ZipOutputStream zos, ByteArrayOutputStream bos, String filePrefix) throws Exception;

    /**
     * 导出pdf
     * @param businessId
     */
    void exportPdf(String businessId, HttpServletResponse response, String filePrefix) throws Exception;


}
