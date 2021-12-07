package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.ConstantValUtil;
import com.common.util.ExcelUtil;
import com.common.util.PDFUtil;
import com.common.util.SequenceUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.itemClosureCheck.ItemExpensesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class ItemExpensesServiceImpl implements ItemExpensesService {

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    private FileService fileService;

    @Autowired
    @Resource(name = "FlowService")
    private FlowService flowService;

    /**
     * 查询列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryItemExpensesList(PageData pd) {
        List<String> pageDataList = JSONArray.parseArray(pd.getString("statusList"), String.class);
        pd.put("processStatusList", pageDataList);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        pd.put("selsctCreateUser", pd.getString("selsctCreateUser"));
        List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryList", pd);

        return itemExpensesList;
    }

    /**
     * 删除
     * @param pd
     */
    @Override
    public void deleteItemExpenses(PageData pd) {
        List<String> businessIdList = JSONArray.parseArray(pd.getString("businessIdList"), String.class);
        //删除主表
        baseDao.batchDelete("ItemExpensesMapper.deleteItemExpenses", businessIdList);
        //先删除附件表
        fileService.deleteAttachment(pd);
    }

    /**
     * 详情
     * @param pd
     * @return
     */
    @Override
    public PageData getItemExpensesDetail(PageData pd) {
        //1、查询主表
        PageData request = (PageData) baseDao.findForObject("ItemExpensesMapper.queryDetail", pd);

        // 查询附件表
        fileService.queryFileByBusinessId(request);

        return request;
    }

    /**
     * 保存或编辑
     * @param pd
     */
    @Override
    public void saveOrUpdate(PageData pd) {
        //如果ID为空，则为保存
        if (pd.getString("id").isEmpty()){
            //判断是直接保存，还是提交时保存
            //直接保存
            if (pd.getString("flag").isEmpty()){
                pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
                String serialNumber = SequenceUtil.generateSerialNo("FYZC");//生成流水号
                String businessId = "FYZC" + UUID.randomUUID().toString();//生成业务主键ID
                pd.put("serialNumber", serialNumber);
                pd.put("businessId", businessId);
            }

            //1、插入主表
            baseDao.insert("ItemExpensesMapper.insertMain", pd);

            // 插入到附件表
            fileService.insert(pd);

        } else {
            //编辑
            //1、更新主表
            baseDao.update("ItemExpensesMapper.updateMain", pd);

            //先删除附件表
            fileService.deleteAttachment(pd);
            //再插入附件表
            fileService.insert(pd);
        }
    }

    /**
     * 提交
     * @param pd
     */
    @Override
    public void submit(PageData pd) {
        //1：列表提交，2：保存提交，3：编辑提交
        if (!pd.getString("flag").equals("1")) {
            //先保存或编辑
            saveOrUpdate(pd);
        }

        flowService.startFlow(pd);

        //更改主表状态
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[1]);
        baseDao.update("ItemExpensesMapper.updateMainProcessStatus", pd);
    }

    /**
     * 审批
     * @param pd
     */
    @Override
    public void approveRecord(PageData pd) {
        //插入附件表，并返回主键id的拼接字符串
        PageData fileIdStr = fileService.insertApproveFile(pd);
        pd.put("fileId", fileIdStr);

        //审批类型 1:同意 2:回退上一个节点 3：回退到发起人
        String approveType = pd.getString("approveType");
        if (approveType.equals("1")) {
            flowService.approveFlow(pd);

        } else if (approveType.equals("2")) {
            flowService.backPreviousNode(pd);

        } else if (approveType.equals("3")) {
            flowService.backOriginalNode(pd);
        }

        //编辑主表的审批状态、审批人等信息
        baseDao.update("ItemExpensesMapper.updateMainProcessStatus", pd);
    }

    /**
     * 导出excel
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "研发项目费用支出";
        String[] head = {"序号", "单据编号", "项目名称", "项目负责人", "所属月份", "一级科目", "二级科目", "支出依据", "预算总额", "已累计支出", "预算结余",
                "本次金额(元)", "结余金额(元)", "申报意见", "编制人", "创建日期"};
        String idStr = pageData.getString("businessIdList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> checkInfoList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDetailExportExcel", listId);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title + 1);
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setCell(wb, sheet);// 单元格
        // 创建表头
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, (head.length - 1));
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }

        if (!CollectionUtils.isEmpty(checkInfoList)) {
            for (int i = 0; i < checkInfoList.size(); i++) {
                PageData pd = checkInfoList.get(i);

                HSSFRow row = sheet.createRow(i + 2);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("serialNumber"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("approveUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("belongingMonth"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("firstSubject"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("twoSubject"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("payNoted"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("budgetAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("accumulatedExpenditure"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("budgetBalance"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("amount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("balanceAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("remark"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String createTime = pd.getString("createTime");
                cell.setCellValue(createTime.substring(0, createTime.lastIndexOf(".")));
                cell.setCellStyle(styleCell);
            }
        }

        //设置自适应宽度
        for (int j = 0; j < head.length; j++) {
            sheet.autoSizeColumn(j);
            int colWidth = sheet.getColumnWidth(j) * 17 / 10;
            if (colWidth < 255 * 256) {
                sheet.setColumnWidth(j, colWidth < 3000 ? 3000 : colWidth);
            } else {
                sheet.setColumnWidth(j, 6000);
            }
        }
        sheet.setAutobreaks(true);
        return wb;
    }

    /**
     * 导出PDF
     * @param data
     * @param document
     */
    @Override
    public void exportPDF(PageData data, Document document) throws DocumentException, IOException, Exception {
        //设置logo和标题
        PDFUtil.addTitle(document,"研发项目结题验收");

        //设置基本信息
        int width1[] = {140, 400, 140, 300, 140, 160, 150, 250};//每栏的宽度
        String[] argArr1 = {
                "编制单位", data.getString("creatorOrg"), "创建人", data.getString("createUser"),
                "创建时间", data.getString("createdDate"), "单据编号", data.getString("serialNumber"),
                "项目名称",data.getString("projectName"),"项目负责人",data.getString("applyUserName"),
                "所属月份",data.getString("belongingMonth"),"一级科目",data.getString("firstSubject"),
                "二级科目",data.getString("twoSubject"), "预算总额",data.getString("budgetAmount"),
                "已累计支出",data.getString("accumulatedExpenditure"), "预算结余",data.getString("budgetBalance"),
                "本次金额(元)",data.getString("amount"), "结余金额(元)",data.getString("balanceAmount"),
                "","","","",
                "支出依据",data.getString("payNoted"),"申报意见",data.getString("remark")
                };
        HashMap<Integer,Integer> mergeMap=new HashMap<>();
        mergeMap.put(34,7);
        mergeMap.put(26, 7);
        PdfPTable baseTableHaveMerge = PDFUtil.createBaseTableHaveMerge(width1, argArr1, mergeMap);

        PdfPTable pdfPTable = new PdfPTable(5);
        int width3[] = {200, 200, 300, 200, 200};// 每栏的宽度
        pdfPTable.setWidths(width3);

        //设置审批记录
        flowService.getApproveTable2(data,pdfPTable);

        //将表2合到表1中
        PDFUtil.mergeTable(baseTableHaveMerge,pdfPTable);
        //添加表格
        document.add(baseTableHaveMerge);
    }




}
