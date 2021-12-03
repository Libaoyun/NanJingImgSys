package com.rdexpense.manager.service.itemClosureCheck;


import com.common.entity.PageData;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.util.List;

public interface ItemClosureCheckService {
    List<PageData> queryItemClosureCheckList(PageData pd);

    List<PageData> queryProjectApplyList(PageData pd);

    void deleteItemClosure(PageData pd);

    PageData getItemClosureCheckDetail(PageData pd);

    List<PageData> queryApplyUserList(PageData pd);

    void saveOrUpdate(PageData pd);

    void submit(PageData pd);

    void approveRecord(PageData pd);

    HSSFWorkbook exportExcel(PageData pd);

    void exportPDF(PageData data, Document document) throws DocumentException, IOException, Exception;
}
