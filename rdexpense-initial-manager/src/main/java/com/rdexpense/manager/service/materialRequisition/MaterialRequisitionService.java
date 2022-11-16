package com.rdexpense.manager.service.materialRequisition;


import com.common.entity.PageData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.text.Document;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface MaterialRequisitionService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryList(PageData pd);

    /**
     * 新增项目领料单
     * @param pd
     * @return
     */
    void addMaterialRequisition(PageData pd);

    /**
     * 更新项目领料单
     * @param pd
     * @return
     */
    void updateMaterialRequisition(PageData pd);


    /**
     * 删除项目领料单
     * @param pd
     * @return
     */
    void deleteMaterialRequisition(PageData pd);

    /**
     * 查询项目领料单
     * @param pd
     * @return
     */
   PageData queryDetail(PageData pd);



    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd) throws Exception;

    /**
     * 导出excel压缩包
     * @param businessIdList
     * @param zos
     * @param filePrefix
     * @throws Exception
     */
    void exportZip(List<String> businessIdList, ZipOutputStream zos, String filePrefix) throws Exception;


    /**
     * 导入明细
     * @param file
     * @param pd
     * @return
     * @throws Exception
     */
    PageData upload(MultipartFile file, PageData pd)throws Exception;


    /**
     * 生成支出申请单
     * @param pd
     * @return
     * @throws Exception
     */
    PageData generateApply(PageData pd);


    void exportPDF(PageData pd, Document document) throws Exception;

}
