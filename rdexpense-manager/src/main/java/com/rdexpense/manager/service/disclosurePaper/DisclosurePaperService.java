package com.rdexpense.manager.service.disclosurePaper;

import com.common.entity.PageData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.util.List;

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

    List<PageData> getDisclosurePaperDetailList(PageData pd);

    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pageData);

    File createFile();
}
