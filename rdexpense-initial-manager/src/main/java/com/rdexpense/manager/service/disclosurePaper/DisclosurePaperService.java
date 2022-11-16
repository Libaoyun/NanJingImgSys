package com.rdexpense.manager.service.disclosurePaper;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.zip.ZipOutputStream;

public interface DisclosurePaperService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryDisclosurePaperList(PageData pd);

    /**
     * 添加交底书
     * @param pd
     * @return
     */
    void addDisclosurePaper(PageData pd);

    void deleteDisclosurePaper(PageData pd);

    /**
     * 编辑项目
     * @param pd
     * @return
     */
    void updateDisclosurePaper(PageData pd);


    /**
     * 查询详细
     */
    PageData getDisclosurePaperDetail(PageData pd);

    PageData getDisclosurePaperDetailBybusinessId(PageData pd);

    List<PageData> getDisclosurePaperDetailList(PageData pd);

    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pageData);

    File createFile();


    /**
     * 导入模版内容
     */
    PageData uploadSurvey(MultipartFile file, PageData pd) throws Exception;


    /**
     * 导出压缩包
     */
    void export(PageData pd, ZipOutputStream zos, ByteArrayOutputStream bos, String serialNumber) throws Exception;

    ResponseEntity export2(PageData pd, HttpServletResponse response) throws Exception;

}
