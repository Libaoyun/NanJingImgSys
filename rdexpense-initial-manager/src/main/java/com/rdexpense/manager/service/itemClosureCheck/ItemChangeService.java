package com.rdexpense.manager.service.itemClosureCheck;

import com.common.entity.PageData;
import com.itextpdf.text.Document;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

public interface ItemChangeService {
    List<PageData> queryItemChangeList(PageData pd);

    void deleteItemChange(PageData pd);

    PageData getItemChangeDetail(PageData pd);

    void saveOrUpdate(PageData pd);

    void submit(PageData pd);

    void approveRecord(PageData pd);

    HSSFWorkbook exportExcel(PageData pd);

    void exportPDF(PageData data, Document document) throws Exception;

    List<PageData> queryBudget(PageData pd);

    List<PageData> getItemChangeDetailByBusinessId(PageData pd);

    PageData getProjectApplyDetail(PageData pageData);

    PageData querySecondarySubjectCodeMaxMonths(PageData pd);
}
