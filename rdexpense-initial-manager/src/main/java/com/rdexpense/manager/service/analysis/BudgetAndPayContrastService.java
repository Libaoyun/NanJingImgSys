
package com.rdexpense.manager.service.analysis;


import com.common.entity.PageData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface BudgetAndPayContrastService {

    /**
     * 查询列表
     *
     * @return
     */
    List<PageData> queryList(PageData pd);


    /**
     * 查询立项项目预算与支出列表
     *
     * @return
     */
    List<PageData> queryProjectBudgetList(PageData pd);


    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd) throws Exception;



}