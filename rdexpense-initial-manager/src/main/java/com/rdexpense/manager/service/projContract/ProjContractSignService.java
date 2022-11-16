package com.rdexpense.manager.service.projContract;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.xmlbeans.XmlException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2021/11/26 12:56
 */
public interface ProjContractSignService {
    /**
     * 新增项目合同
     * @param pd
     * @return
     */
    void addProjContract(PageData pd);

    /**
     * 更新项目合同
     * @param pd
     * @return
     */
    void updateProjContract(PageData pd);

    /**
     * 删除项目合同
     * @param pd
     * @return
     */
    void deleteProjContract(PageData pd);

    /**
     * 查询项目合同列表
     * @return
     */
    List<PageData> queryProjContractList(PageData pd);

    PageData getDetail(PageData pd);

    /**
     * 根据process_id查询项目合同列表
     * @return
     */
    List<PageData> queryByBusinessId(PageData pd);

    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd);

    /**
     * 以zip格式导出合同pdf文档
     * @param pd
     * @param zos
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    void exportContractZip(PageData pd, ZipOutputStream zos,
                           ByteArrayOutputStream bos, String serialNumber) throws Exception;

    /**
     * 导出excel压缩包
     * @param pd
     * @param zos
     * @param bos
     * @param filePrefix
     * @throws Exception
     */
    void exportZip(PageData pd, ZipOutputStream zos, ByteArrayOutputStream bos, String filePrefix,Map<String,PageData> map) throws Exception;

    /**
     * 在同一pdf格式导出多个合同文档
     * @param pd
     * @param bos
     * @param serialNumber
     * @throws Exception
     */
    void exportContractPdf(PageData pd, ByteArrayOutputStream bos, String serialNumber) throws Exception;

    /**
     * 打印pdf格式合同文档(包括一个或多个合同文档)
     * @param pd
     * @param response
     * @throws Exception
     */
    ResponseEntity printContractPdf(PageData pd, String serialNumber, HttpServletResponse response)  throws Exception;


    /**
     * 填充合同封面占位符
     * @param params
     * @param projContrPd
     */
    int fillContrCoverPagePlaceholders(Map<String, Object> params, PageData projContrPd) throws ParseException;

    /**
     * 填充合同封面占位符
     * @param params
     * @param projContrPd
     * @throws Exception
     */
    int fillContrSurveyInfoPlaceholders(Map<String, Object> params, PageData projContrPd);

    /**
     * 课题的年度计划及年度目标
     * @param params
     * @param projContrPd
     */
    int fillContrProgressPlanParams(Map<String, String> params, PageData projContrPd);

    /**
     * 课题的参加单位及分工
     * @param params
     * @param projContrPd
     */
    int fillContrAttendUnitParams(Map<String, String> params, PageData projContrPd);

    /**
     * 课题的负责人信息
     * @param params
     * @param projContrPd
     */
    int fillContrProjLeaderInfoPlaceholders(Map<String, Object> params, PageData projContrPd);

    int fillContrProjLeaderInfoParams(Map<String, String> params, PageData projContrPd);
    /**
     * 课题的负责人信息
     * @param params
     * @param projContrPd
     */
    int getParticipantsInfoParams(Map<String, String> params, PageData projContrPd);

    /**
     * 课题的经费预算信息
     * @param params
     * @param projContrPd
     */
    int getFundsBudgetParams(Map<String, String> srcAccountMap,
                                   Map<String, String> expAccountMap, PageData projContrPd);

    int getAppnPlanParams(Map<Integer, String> appnPlanMap, PageData projContrPd);

    void XWPFTableCellSetText(XWPFTableCell cell, String text);

    void fillPlanTarget2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException;

    void fillAttendUnit2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException;

    void fillProjLeaderInfo2Table(XWPFTable xwpfTbl, PageData projContrPd, int index) throws IOException, XmlException;

    void fillParticipantsInfo2Table(XWPFTable xwpfTbl, PageData projContrPd, int index) throws IOException, XmlException;

    void fillFundsBudget2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException;

    void fillAppnPlan2Table(XWPFTable xwpfTbl, PageData projContrPd) throws IOException, XmlException;

    void fillInfo2Tables(XWPFDocument doc, PageData contrPd) throws IOException, XmlException;

    void fillContractParams(Map<String, Object> params, PageData projContractPd) throws ParseException;
    void fillPlaceHolders(Map<String, Object> params, PageData projContractPd) throws ParseException;

    TreeMap<Integer, String> sortFundsBudget(Map<String, String> params);


    void exportPdf(PageData pd,HttpServletResponse response ) throws Exception;

}
