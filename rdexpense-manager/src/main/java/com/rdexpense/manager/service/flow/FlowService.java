package com.rdexpense.manager.service.flow;

import com.common.entity.PageData;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:47
 * @description
 */
public interface FlowService {


    /**
     * 新增流程
     *
     * @param pd
     * @return
     */
    String addFlow(PageData pd);

    /**
     * 编码流程
     * @param pd
     */

    String updateFlow(PageData pd);

    /**
     * 删除流程
     * @param pd
     */
    void deleteFlow(PageData pd);


    /**
     * 查看流程
     * @param pd
     * @return
     */
    PageData getFlow(PageData pd);


    /**
     * 查询审批人
     * @param pd
     * @return
     */
    List<PageData> queryFlowUser(PageData pd);


    /**
     * 查看每个单据的流程
     * @param pd
     * @return
     */
    PageData getSerialFlow(PageData pd);


    /**
     * 查询待办
     * @param pd
     * @return
     */
    List<PageData> queryWaitDone(PageData pd);

    /**
     * 查询已办
     * @param pd
     * @return
     */
    List<PageData> queryIsDone(PageData pd);


    /**
     * 启动工作流
     * @param pd
     * @return 实例ID
     */
    PageData startFlow(PageData pd);

    /**
     * 审批工作流
     * @param pd
     * @return
     */
    PageData approveFlow(PageData pd);

    /**
     * 回退到上一个节点
     * @param pd
     * @return
     */
    PageData backPreviousNode(PageData pd);

    /**
     * 回退到发起人
     * @param pd
     * @return
     */
    PageData backOriginalNode(PageData pd);

    /**
     * 根据实例ID，查询审批记录
     * @param pd
     * @return
     */
    List<PageData> queryApprovalScheduleByProcessInstId(PageData pd);


    PdfPTable getApproveTable2(PageData pd, PdfPTable table) throws Exception;

}
