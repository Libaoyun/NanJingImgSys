package com.rdexpense.manager.service.system;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.itextpdf.text.Document;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:47
 * @description
 */
public interface OrganizationService {


    /**
     * 查询组织列表
     * @param pd
     * @return
     */
    List<PageData> queryOrganizationList(PageData pd);

    /**
     * 新增组织
     *
     * @param pd
     * @return
     */
    void addOrganization(PageData pd);

    /**
     * 编码组织
     * @param pd
     */

    void updateOrganization(PageData pd);

    /**
     * 删除组织
     * @param pd
     */
    void deleteOrganization(PageData pd);


    /**
     * 查询组织详情
     * @param pd
     * @return
     */
    PageData getOrganizationDetail(PageData pd);


    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd);


    /**
     * 导出pdf
     */
    void exportPDF(PageData pd, Document document) throws Exception;

}
