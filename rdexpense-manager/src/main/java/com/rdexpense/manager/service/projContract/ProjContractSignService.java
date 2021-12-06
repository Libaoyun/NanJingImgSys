package com.rdexpense.manager.service.projContract;

import com.common.entity.PageData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2021/11/26 12:56
 */
public interface ProjContractSignService {
    /**
     * 新增项目合同
     * @param pd
     * @return
     */
    void addProjContract(PageData pd);

    /**
     * 更新项目合同
     * @param pd
     * @return
     */
    void updateProjContract(PageData pd);

    /**
     * 删除项目合同
     * @param pd
     * @return
     */
    void deleteProjContract(PageData pd);

    /**
     * 查询项目合同列表
     * @return
     */
    List<PageData> queryProjContractList(PageData pd);

    /**
     * 根据process_id查询项目合同列表
     * @return
     */
    List<PageData> queryByBusinessId(PageData pd);

    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd);

    /**
     * 以zip格式导出合同pdf文档
     * @param pd
     * @param zos
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    void exportContractZip(PageData pd, ZipOutputStream zos,
                           ByteArrayOutputStream bos, String serialNumber) throws Exception;

    /**
     * 在同一pdf格式导出多个合同文档
     * @param pd
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    void exportContractPdf(PageData pd, ByteArrayOutputStream bos, String serialNumber) throws Exception;
}
