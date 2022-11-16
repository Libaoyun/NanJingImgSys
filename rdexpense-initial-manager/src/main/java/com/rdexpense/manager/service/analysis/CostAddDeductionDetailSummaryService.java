
package com.rdexpense.manager.service.analysis;


import com.common.entity.PageData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface CostAddDeductionDetailSummaryService {

    /**
     * 查询列表
     *
     * @return
     */
    List<PageData> queryList(PageData pd);


    /**
     * 编辑其他事项
     * @param pd
     */
    void update(PageData pd);


    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd) throws Exception;


    List<PageData> queryProjectApplyList(PageData pd);

    PageData queryExpensesMaxMonths(PageData pd);
}
