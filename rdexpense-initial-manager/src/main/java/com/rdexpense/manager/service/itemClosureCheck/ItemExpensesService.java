package com.rdexpense.manager.service.itemClosureCheck;


import com.common.entity.PageData;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.util.List;

public interface ItemExpensesService {

    void saveOrUpdate(PageData pd);

    void submit(PageData pd);

    void approveRecord(PageData pd);

    HSSFWorkbook exportExcel(PageData pd);

    void exportPDF(PageData data, Document document) ;

    PageData getItemExpensesDetail(PageData pd);

    List<PageData> queryItemExpensesList(PageData pd);

    void deleteItemExpenses(PageData pd);

    PageData queryBudgetAccumulated(PageData pd);

    PageData queryBudgetExcessTips(PageData pd);

}
