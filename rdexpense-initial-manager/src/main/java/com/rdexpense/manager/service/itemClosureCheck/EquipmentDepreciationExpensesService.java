package com.rdexpense.manager.service.itemClosureCheck;


import com.common.entity.PageData;
import com.itextpdf.text.Document;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EquipmentDepreciationExpensesService {

    void saveOrUpdate(PageData pd);

    HSSFWorkbook exportExcel(PageData pd);

    void exportPDF(PageData data, Document document) ;

    PageData getDepreciationExpensesDetail(PageData pd);

    List<PageData> queryList(PageData pd);

    void deleteEquipmentDepreciationExpenses(PageData pd);


    List<PageData> upload(MultipartFile file, PageData pd);
}
