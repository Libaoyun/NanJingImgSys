package com.rdexpense.manager.service.projectApply;


import com.common.entity.PageData;
import com.itextpdf.text.Document;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface ProjectApplyService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryList(PageData pd);

    /**
     * 新增立项申请
     * @param pd
     * @return
     */
    void addApply(PageData pd);

    /**
     * 更新立项申请
     * @param pd
     * @return
     */
    void updateApply(PageData pd);


    /**
     * 删除立项申请
     * @param pd
     * @return
     */
    void deleteApply(PageData pd);

    /**
     * 查询立项申请详情
     * @param pd
     * @return
     */
   PageData getApplyDetail(PageData pd);


    /**
     * 审批流程
     * @return
     */
    void approveRecord(PageData pd);

    /**
     * 列表提交
     * @param pd
     */
    void submitRecord(PageData pd);

    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd);



}
