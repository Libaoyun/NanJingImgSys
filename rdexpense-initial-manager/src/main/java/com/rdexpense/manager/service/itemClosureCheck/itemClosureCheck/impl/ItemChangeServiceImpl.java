package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.config.ThreadPoolConfig;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.controller.itemClosureCheck.ItemExpensesController;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.itemClosureCheck.ItemChangeService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class ItemChangeServiceImpl implements ItemChangeService {
    private static final Logger logger = LoggerFactory.getLogger(ItemChangeServiceImpl.class);

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    private FileService fileService;

    @Resource(name = "asyncServiceExecutor")
    private Executor Executor;

    @Autowired
    @Resource(name = "FlowService")
    private FlowService flowService;

    /**
     * 查询列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryItemChangeList(PageData pd) {
        List<String> pageDataList = JSONArray.parseArray(pd.getString("statusList"), String.class);
        pd.put("processStatusList", pageDataList);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        pd.put("processQuitStatus", ConstantValUtil.APPROVAL_STATUS[3]);
        pd.put("selectCreateUser", pd.getString("selectCreateUser"));

        //处理日期搜索条件
        String createTime = pd.getString("createTime");
        if(org.apache.commons.lang3.StringUtils.isNotBlank(createTime)){
            JSONArray createTimeArr = JSONArray.parseArray(createTime);
            pd.put("beginCreateTime",createTimeArr.get(0) + " 00:00:00");
            pd.put("lastCreateTime",createTimeArr.get(1) + " 23:59:59");
        }

        String updateTime = pd.getString("updateTime");
        if(StringUtils.isNotBlank(updateTime)){
            JSONArray updateTimeArr = JSONArray.parseArray(updateTime);
            pd.put("beginUpdateTime",updateTimeArr.get(0) + " 00:00:00");
            pd.put("lastUpdateTime",updateTimeArr.get(1) + " 23:59:59");
        }

        String startDate = pd.getString("startYear");
        if(StringUtils.isNotBlank(startDate)){
            JSONArray startDateArr = JSONArray.parseArray(startDate);
            pd.put("beginStartDate",startDateArr.get(0));
            pd.put("lastStartDate",startDateArr.get(1));
        }

        String endDate = pd.getString("endYear");
        if(StringUtils.isNotBlank(endDate)){
            JSONArray endDateArr = JSONArray.parseArray(endDate);
            pd.put("beginEndDate",endDateArr.get(0));
            pd.put("lastEndDate",endDateArr.get(1));
        }

        String checkDate = pd.getString("checkDate");
        if(StringUtils.isNotBlank(checkDate)){
            JSONArray checkDateArr = JSONArray.parseArray(checkDate);
            pd.put("beginCheckDate",checkDateArr.get(0));
            pd.put("lastCheckDate",checkDateArr.get(1));
        }

        List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("ItemChangeMapper.queryList", pd);

        return itemExpensesList;
    }

    /**
     * 查询经费预算信息
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryBudget(PageData pd) {

        pd.put("businessId",pd.getString("projectApplyMainId"));
        //查询经费预算信息
        List<PageData>   pageDataList = (List<PageData>) baseDao.findForList("ProjectApplyMapper.queryBudget", pd);

        return pageDataList;
    }

    /**
     * 查询明细
     * @param pd
     * @return
     */
    @Override
    public List<PageData> getItemChangeDetailByBusinessId(PageData pd) {
        List<PageData>   pageDataList = null;
        //changeTypeCode周期变更：1,预算变更：2,人员变更：3
        if (pd.getString("changeTypeCode").equals("1")){
            pageDataList = (List<PageData>) baseDao.findForList("ItemChangeMapper.queryCycleDetailByBusinessId", pd);
        } else if (pd.getString("changeTypeCode").equals("2")){
            pageDataList = (List<PageData>) baseDao.findForList("ItemChangeMapper.queryFundsList", pd);
        } else {
            pageDataList = (List<PageData>) baseDao.findForList("ItemChangeMapper.queryItemChangeUserList", pd);
        }
        return pageDataList;
    }

    /**
     * 获取项目立项申请的开始结束年份
     * @param pageData
     * @return
     */
    @Override
    public PageData getProjectApplyDetail(PageData pageData) {
        PageData request = (PageData) baseDao.findForObject("ItemChangeMapper.queryProjectApplyDetail", pageData);
        return request;
    }

    /**
     * 周期变更时，查询最大的费用支出月份值
     * @param pd
     * @return
     */
    @Override
    public PageData querySecondarySubjectCodeMaxMonths(PageData pd) {
        PageData request = (PageData) baseDao.findForObject("ItemChangeMapper.querySecondarySubjectCodeMaxMonths", pd);
        return request;
    }

    /**
     * 删除
     * @param pd
     */
    @Override
    @Transactional
    public void deleteItemChange(PageData pd) {
        List<String> idList = JSONArray.parseArray(pd.getString("businessIdList"), String.class);
        //删除主表
        baseDao.batchDelete("ItemChangeMapper.deleteItemChange", idList);

        //删除周期变更表
        baseDao.batchDelete("ItemChangeMapper.deleteItemChangeCycle", idList);

        //删除经费支出表
        baseDao.batchDelete("ItemChangeMapper.deleteItemChangeFundsBudget", idList);

        //删除变更人员表
        baseDao.batchDelete("ItemChangeMapper.deleteItemChangeResearchUser", idList);

        //删除附件表
        fileService.deleteAttachment(pd);
    }

    /**
     * 查询详情
     * @param pd
     * @return
     */
    @Override
    public PageData getItemChangeDetail(PageData pd) {
        //1、查询主表
        PageData request = (PageData) baseDao.findForObject("ItemChangeMapper.queryItemChangeDetail", pd);

        //查询变更信息
        List<PageData> cycleList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryCycleList", pd);
        request.put("cycleList",cycleList);

        //查询经费支出信息
        List<PageData> budgetDetailList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryFundsList", pd);
        request.put("budgetDetailList",budgetDetailList);

        //查询人员信息
        List<PageData> userInfoList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryItemChangeUserList", pd);
        request.put("userInfoList",userInfoList);

        // 查询附件表
        fileService.queryFileByBusinessId(request);

        return request;
    }

    /**
     * 保存或编辑
     * @param pd
     */
    @Override
    @Transactional
    public void saveOrUpdate(PageData pd) {
        //如果ID为空，则为保存
        if (pd.getString("id").isEmpty()){
            //判断是直接保存，还是提交时保存
            //直接保存
            if (pd.getString("flag").isEmpty()){
                pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
                String serialNumber = SequenceUtil.generateSerialNo("XMBG");//生成流水号
                String businessId = "XMBG" + UUID.randomUUID().toString();//生成业务主键ID
                pd.put("serialNumber", serialNumber);
                pd.put("businessId", businessId);
            }

            //1、插入主表
            baseDao.insert("ItemChangeMapper.insertMain", pd);

            //changeTypeCode周期变更：1,预算变更：2,人员变更：3
            if (pd.getString("changeTypeCode").equals("2")){
                List<PageData> fundsList = JSONArray.parseArray(pd.getString("budgetDetailList"), PageData.class);
                for (PageData data : fundsList) {
                    data.put("businessId", pd.getString("businessId"));
                }

                //插入预算表
                baseDao.batchInsert("ItemChangeMapper.insertBudget", fundsList);

            } else if (pd.getString("changeTypeCode").equals("3")){
                List<PageData> userInfoList = JSONArray.parseArray(pd.getString("userInfoList"), PageData.class);
                for (PageData data : userInfoList) {
                    data.put("businessId", pd.getString("businessId"));
                }
                //插入人员表
                baseDao.batchInsert("ItemChangeMapper.insertUserInfo", userInfoList);

            } else {

                List<PageData> cycleList = JSONArray.parseArray(pd.getString("cycleList"), PageData.class);
                for (PageData data : cycleList) {
                    data.put("businessId", pd.getString("businessId"));
                }

                //插入周期变更表
                baseDao.batchInsert("ItemChangeMapper.insertCycle", cycleList);
            }

            // 插入到附件表
            fileService.insert(pd);

        } else {
            //编辑
            //1、更新主表
            baseDao.update("ItemChangeMapper.updateMain", pd);

            //changeTypeCode周期变更：1,预算变更：2,人员变更：3
            if (pd.getString("changeTypeCode").equals("2")){
                //先删除预算表
                baseDao.delete("ItemChangeMapper.deleteItemChangeFundsBudgetByBusinessId", pd);

                List<PageData> fundsList = JSONArray.parseArray(pd.getString("budgetDetailList"), PageData.class);
                for (PageData data : fundsList) {
                    data.put("businessId", pd.getString("businessId"));
                }
                //插入预算表
                baseDao.batchInsert("ItemChangeMapper.insertBudget", fundsList);
            } else if (pd.getString("changeTypeCode").equals("3")){
                //先删除人员表
                baseDao.delete("ItemChangeMapper.deleteItemChangeUserInfoByBusinessId", pd);

                List<PageData> userInfoList = JSONArray.parseArray(pd.getString("userInfoList"), PageData.class);
                for (PageData data : userInfoList) {
                    data.put("businessId", pd.getString("businessId"));
                }
                //插入人员表
                baseDao.batchInsert("ItemChangeMapper.insertUserInfo", userInfoList);
            } else {
                //先删除变更周期表
                baseDao.delete("ItemChangeMapper.deleteItemChangeCycleByBusinessId", pd);

                List<PageData> cycleList = JSONArray.parseArray(pd.getString("cycleList"), PageData.class);
                for (PageData data : cycleList) {
                    data.put("businessId", pd.getString("businessId"));
                }
                //插入周期变更表
                baseDao.batchInsert("ItemChangeMapper.insertCycle", cycleList);
            }

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
    @Transactional
    public void submit(PageData pd) {

        //1：列表提交，2：保存提交，3：编辑提交
        if (pd.getString("flag").equals("2")){
            String serialNumber = SequenceUtil.generateSerialNo("XMBG");//生成流水号
            String businessId = "XMBG" + UUID.randomUUID().toString();//生成业务主键ID
            pd.put("serialNumber", serialNumber);
            pd.put("businessId", businessId);
            //保存提交，先生成单据编号，再启动工作流，然后再保存数据
            flowService.startFlow(pd);

            saveOrUpdate(pd);
        } else if (pd.getString("flag").equals("3")){
            //编辑提交，先启动工作流，然后再保存数据
            flowService.startFlow(pd);

            saveOrUpdate(pd);
        } else {

            //列表提交，直接启动工作流
            flowService.startFlow(pd);
        }

        //更改主表状态
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[1]);
        baseDao.update("ItemChangeMapper.updateMainProcessStatus", pd);

    }

    /**
     * 审批
     * @param pd
     */
    @Override
    @Transactional
    public void approveRecord(PageData pd) {
        //插入附件表，并返回主键id的拼接字符串
        PageData data = fileService.insertApproveFile(pd);
        pd.put("fileId", data.getString("fileId"));
        pd.put("fileName", data.getString("fileName"));

        //审批类型 1:同意 2:回退上一个节点 3：回退到发起人
        String approveType = pd.getString("approveType");
        if (approveType.equals("1")) {
            flowService.approveFlow(pd);

        } else if (approveType.equals("2")) {
            flowService.backPreviousNode(pd);

        } else if (approveType.equals("3")) {
            flowService.backOriginalNode(pd);
        }

        //审批完成时
        if (pd.getString("processStatus").equals(ConstantValUtil.APPROVAL_STATUS[4])){
            //更改周期、经费预算
            if (pd.getString("changeTypeCode").equals("1")){
                PageData cyclePageData = (PageData) baseDao.findForObject("ItemChangeMapper.queryCycleListByDataStatus", pd);
                cyclePageData.put("projectApplyMainId",pd.getString("projectApplyMainId"));
                //周期变更，更改研发立项申请里的周期
                baseDao.update("ItemChangeMapper.updateProjectApplyCycle", cyclePageData);

                //周期并更后，需把周期变长或变短，并计算经费预算的剩余预算值分摊到剩余月份的每个月份
                updateCycleAndSurplusMonthsBudget(cyclePageData);

            } else if (pd.getString("changeTypeCode").equals("2")){
                List<PageData> budgetDetailList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryFundsList", pd);
                for (PageData pageData : budgetDetailList) {
                    pageData.put("projectApplyMainId",pd.getString("projectApplyMainId"));
                }
                //预算变更，更改研发立项申请里的经费预算
                baseDao.update("ItemChangeMapper.updateProjectApplyBudget", budgetDetailList);
                //预算变更，更改研发立项申请里的经费预算（每月预算）
                baseDao.update("ItemChangeMapper.updateProjectApplyBudgetMonth", budgetDetailList);

                //预算并更后，需把经费预算的剩余预算值分摊到剩余月份的每个月份
                updateSurplusMonthsBudget(pd);
//                Executor.execute(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        updateCycleAndSurplusMonthsBudget(budgetDetailList,projectApplyMainId);
//                    }
//                });
            } else {
                //人员变更
                baseDao.update("ItemChangeMapper.updateUserInfoOriginal", pd);
            }
        }
        //编辑主表的审批状态、审批人等信息
        baseDao.update("ItemChangeMapper.updateMainApproveProcessStatus", pd);
    }



    /**
     * 导出excel
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        String title = "研发项目变更";
        String[] head = {"序号", "单据编号", "项目名称", "项目负责人", "联系电话", "变更类型", "要求变更的原项目相关部分内容",
                "要求变更的内容或建议", "变更理由", "项目实施情况", "经费使用情况",
                "编制人", "创建日期", "更新日期"};
        String idStr = pageData.getString("businessIdList");
        List<String> listId = JSONObject.parseArray(idStr, String.class);
        //根据idList查询主表
        List<PageData> checkInfoList = (List<PageData>) baseDao.findForList("ItemChangeMapper.queryItemChangeExportExcel", listId);

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
                cell.setCellValue(pd.getString("applyUserName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("telephone"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("changeType"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("partContent"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("changeAdvise"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("changeReason"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("implementation"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("fundsUse"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("createUser"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                String createTime = pd.getString("createTime");
                cell.setCellValue(createTime.substring(0, createTime.lastIndexOf(".")));
                cell.setCellStyle(styleCell);


                cell = row.createCell(j++);
                String time1 = pd.getString("updateTime");
                cell.setCellValue(time1.substring(0, time1.lastIndexOf(".")));
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
    public void exportPDF(PageData data, Document document) throws Exception {
        //设置logo和标题
        PDFUtil.addTitle(document,"研发项目变更");

        PageData filePd = fileService.queryFileByBusinessId(data);
        String fileName = "";
        if (filePd != null){
            List<PageData> fileList = JSONArray.parseArray(filePd.getString("attachmentList"),PageData.class);
            StringBuilder stringBuilder = new StringBuilder();
            if (fileList.size() > 0){
                for (PageData pageData : fileList) {
                    stringBuilder.append(pageData.getString("fileName")).append(",");
                }
            }

            if (stringBuilder.length() > 0){
                fileName = stringBuilder.substring(0,stringBuilder.toString().length()-1);
            }
        }

        //设置基本信息
        int width1[] = {140, 400, 140, 300, 140, 160};//每栏的宽度
        String[] argArr1 = {
                "项目名称",data.getString("projectName"),
                "合同编号",data.getString("projectName"),"项目负责人",data.getString("applyUserName"),"联系电话",data.getString("telephone"),
                "主持单位", data.getString("creatorOrgName"),
                "附件清单", fileName,
                "要求变更的原项目相关部分内容", data.getString("partContent"),
                "要求变更的内容或建议", data.getString("changeAdvise"),
                "变更理由",data.getString("changeReason"),
                "项目实施情况",data.getString("implementation"),
                "经费使用情况",data.getString("fundsUse")};

        HashMap<Integer,Integer> mergeMap=new HashMap<>();
        mergeMap.put(1,5);
        mergeMap.put(9,5);
        mergeMap.put(11,5);
        mergeMap.put(13,5);
        mergeMap.put(15,5);
        mergeMap.put(17,5);
        mergeMap.put(19,5);
        mergeMap.put(21,5);
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


    /**
     * 周期变更，变更研发项目的月份明细，并计算分摊值
     * @param pd
     */
    private  void updateCycleAndSurplusMonthsBudget(PageData pd){

        logger.info("周期变更，变更研发项目的月份明细，并计算分摊值,参数=[{}]", pd);
        CheckParameter.stringEmpty(pd.getString("startYear"),"开始年度");
        CheckParameter.stringEmpty(pd.getString("endYear"),"结束年度");

        //周期变更的开始年度，如果有已提交的支出费用后，则不能进行变更开始年度，只能变更结束年度，而结束年度不能小于二级科目支出费用的最大月份
        //计算周期变更的年度数量值
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        int yearsNumber = 0;
        int startYear = 0;

        //1、获取现有每月预算详情信息的年度值，根据周期变更的周期，按年度进行对比，
        //查询研发项目立项申请-经费预算-每月预算
        List<PageData> monthsList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthList", pd);
        //查询研发项目立项申请-经费预算-每月明细预算
        List<PageData> monthsDetailList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthDetailList", pd);
        //对每月明细预算，按年度进行分组
        List<PageData> yearsList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthDetailGroupByYearsList", pd);

        List<PageData> newMonthsDetailList = new ArrayList<>();
        try {
            startYear = Integer.valueOf(sdf.format(sdf.parse(pd.getString("startYear"))));

            startCalendar.setTime(sdf.parse(pd.getString("startYear")));
            endCalendar.setTime(sdf.parse(pd.getString("endYear")));

            yearsNumber = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR) + 1;

            if (!CollectionUtils.isEmpty(yearsList)){
                if (yearsNumber > yearsList.size()){
                    //如果yearsNumber>yearsList.size(),说明周期变更比原来的周期变长了，需要新增年度的月份明细项
                    for (int i = 0; i < yearsNumber; i++) {
                        boolean flag = false;
                        for (PageData pageData : yearsList) {
                            Integer years = Integer.valueOf(pageData.getString("years"));

                            if (startYear == years){
                                flag = true;
                            }
                        }

                        if (!flag){
                            //说明周期变更的周期变长了，需要新增年度的月度明细值
                            if (!CollectionUtils.isEmpty(monthsList)){
                                //遍历每月预算的科目，生成科目的每月预算详情
                                for (PageData monthsPageData : monthsList) {
                                    PageData addNewPageData = new PageData();
                                    addNewPageData.put("businessId",monthsPageData.getString("businessId"));
                                    addNewPageData.put("years",startYear);
                                    addNewPageData.put("expenseAccount",monthsPageData.getString("expenseAccount"));
                                    addNewPageData.put("expenseAccountCode",monthsPageData.getString("expenseAccountCode"));
                                    monthsDetailList.add(addNewPageData);
                                }
                            }
                        }

                        startYear ++;
                    }

                    newMonthsDetailList.addAll(monthsDetailList);

                } else {
                    //如果yearsNumber<=yearsList.size(),说明周期变更比原来的周期变短了，需要删除年度的月份明细项
                    for (int j = 0; j < yearsList.size(); j++) {
                        PageData pageData = yearsList.get(j);
                        Integer years = Integer.valueOf(pageData.getString("years"));

                        boolean flag = false;

                        int sYear = Integer.valueOf(sdf.format(sdf.parse(pd.getString("startYear"))));
                        for (int i = 0; i < yearsNumber; i++) {
                            if (sYear == years){
                                flag = true;
                            }
                            sYear ++;
                        }

                        if (flag){
                            //说明周期变更的周期变短了，需要删除年度的月份明细项
                            if (!CollectionUtils.isEmpty(monthsDetailList)){
                                for (int m = 0; m < monthsDetailList.size(); m++) {
                                    PageData deletePageData = monthsDetailList.get(m);
                                    int deleteYears = Integer.valueOf(deletePageData.getString("years"));
                                    if (deleteYears == years){
                                        newMonthsDetailList.add(deletePageData);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            throw new MyException("所有月份的月份的数量allMonthNumber值计算异常", e);
        }

        // 对现有每月预算详情信息进行增减，重新生成每月预算详情信息
        String projectApplyMainId = pd.getString("projectApplyMainId");
        baseDao.delete("ItemChangeMapper.deleteBudgetMonthDetail", projectApplyMainId);
        logger.info("周期变更===先删除每月预算的详情,projectApplyMainId=[{}]", projectApplyMainId);
        baseDao.batchInsert("ItemChangeMapper.batchInsertBudgetMonthDetail", newMonthsDetailList);
        logger.info("周期变更===插入每月预算的详情成功");

        //2、引用预算变更的计算方法进行计算
        updateSurplusMonthsBudget(pd);

    }

    /**
     * 预算变更，变更研发项目的月份明细，并计算分摊值
     * @param pd
     */
    public void updateSurplusMonthsBudget(PageData pd){

        String projectApplyMainId = pd.getString("projectApplyMainId");

        String[] monthsArr = {"january","february","march","april","may","june","july","august","september","october","november","december"};
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");//设置日期格式
        DecimalFormat df = new DecimalFormat("0.00");

        //根据projectApplyMainId，查询研发立项项目是否为局级模块
        List<PageData> dicTypeList = new ArrayList<>();
        PageData  blPageData= (PageData) baseDao.findForObject("ItemChangeMapper.queryProjectApplyDetail", pd);
        if (blPageData != null){
            //区分研发立项项目是否为局级模块
            if (blPageData.getString("bureauLevel").equals("0")){
                //不是局级模板项目
                //查询不是局级模板项目对应的经费支出预算字典类型的类型ID
                dicTypeList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicTypeNoBureauEnumList", null);
            } else if (blPageData.getString("bureauLevel").equals("1")){
                //是局级模板项目
                //查询是局级模板项目对应的经费支出预算字典类型的类型ID
                dicTypeList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicTypeBureauList", null);
            } else {
                throw new MyException("系统异常，研发立项项目的模块类型不能为空值！");
            }
        }
        logger.info("根据projectApplyMainId，查询研发立项项目是否为局级模块,dicTypeList=[{}]", dicTypeList);

        //根据projectApplyMainId,找出费用支出已审批完成的各个二级科目的最大月份值，及对应的二级科目编码,二级科目的支出总金额
        List<PageData> secondarySubjectCodeList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.querySecondarySubjectCodeMonthsList", projectApplyMainId);

        //再根据二级科目编码，找出对应的经费预算项的字典类型ID,及所对应的最大月份值
        List<PageData> maxMonthsList = new ArrayList<>();
        List<PageData> dicTypeIdList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.querySubToDicTypeList", dicTypeList);
        if (!CollectionUtils.isEmpty(dicTypeIdList)){
            //找出二级科目编码对应的经费预算项的字典类型ID,及所对应的最大月份值
            for (PageData pageData : dicTypeIdList) {
                PageData maxMonthsPD = new PageData();
                String dic_enum_id = pageData.getString("dic_enum_id").split("-")[0];

                if (!CollectionUtils.isEmpty(secondarySubjectCodeList)){
                    for (PageData data : secondarySubjectCodeList) {
                        if (dic_enum_id.equals(data.getString("secondary_subject_code"))){
                            maxMonthsPD.put("dic_type_id",pageData.getString("dic_type_id"));
                            maxMonthsPD.put("belonging_month",data.getString("belongingMonth"));
                            maxMonthsPD.put("sumAmount",data.getString("sumAmount"));
                            maxMonthsList.add(maxMonthsPD);
                        }
                    }
                } else {
                    //没有费用支出项
                    maxMonthsPD.put("dic_type_id",pageData.getString("dic_type_id"));
                    try {
                        maxMonthsPD.put("belonging_month",monthFormat.format(monthFormat.parse(blPageData.getString("startYear"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
                    }
                    maxMonthsPD.put("sumAmount",0);
                    maxMonthsList.add(maxMonthsPD);
                }
            }

            //遍历dicTypeList，找出字典类别ID在字典表中没有对应的字典信息的字典类型ID
            // (如：八、课题管理费，局级的八、项目管理费),并添加到maxMonthsList集合中
            for (PageData data : dicTypeList) {
                PageData dicTypePD = new PageData();
                boolean flag = false;
                for (PageData pageData : dicTypeIdList) {
                    if (data.getString("dic_type_id").equals(pageData.getString("dic_type_id"))){
                        flag = true;
                        break;
                    }
                }

                if (!flag){
                    dicTypePD.put("dic_type_id",data.getString("dic_type_id"));
                    try {
                        dicTypePD.put("belonging_month",monthFormat.format(monthFormat.parse(blPageData.getString("startYear"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
                    }
                    dicTypePD.put("sumAmount",0);
                    maxMonthsList.add(dicTypePD);
                }
            }
            logger.info("找出二级科目编码对应的经费预算项的字典类型ID,及所对应的最大月份值,maxMonthsList=[{}]", maxMonthsList);

            //对同一字典类型ID的二级科目支出总金额进行汇总
            //再对字典类型ID去重
            if (!CollectionUtils.isEmpty(maxMonthsList)){
                for (int i = 0; i < maxMonthsList.size(); i++) {
                    PageData pageData = maxMonthsList.get(i);
                    for (int j = 0; j < maxMonthsList.size(); j++) {
                        PageData data = maxMonthsList.get(j);
                        try {
                            if (pageData.getString("dic_type_id").equals(data.getString("dic_type_id")) &&
                                    monthFormat.parse(pageData.getString("belonging_month")).before(monthFormat.parse(data.getString("belonging_month")))){
                                //根据集合的索引值，去重，不能是对象pageData，maxMonthsList.remove(pageData)
                                maxMonthsList.remove(i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
                        }
                    }
                }
            }
            logger.info("找出二级科目编码对应的经费预算项的字典类型ID,及所对应的最大月份值,对字典类型ID去重,maxMonthsList=[{}]", maxMonthsList);
        }


        //可根据项目的开始年度、结束年度，计算剩余月份及所有月份，遍历
        //所有月份的月份的数量allMonthNumber，结束年度月份 - 开始年度月份
        //周期变更时，使用传入参数：结束年度月份、开始年度月份
        int allMonthNumber = 0 ;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        try {
            startCalendar.setTime(sdf.parse(blPageData.getString("startYear")));
            endCalendar.setTime(sdf.parse(blPageData.getString("endYear")));

            int differYears = (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12;
            int differMonth = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            allMonthNumber = differYears + differMonth;

        } catch (Exception e){
            throw new MyException("所有月份的月份的数量allMonthNumber值计算异常", e);
        }


        //截至当前单据所属月份的预算累计
        BigDecimal toThisMonthAllBudget = new BigDecimal("0.00");
        //预算值
        BigDecimal expenseBudget = new BigDecimal("0.00");
        //每月的分摊值
        BigDecimal monthShare = new BigDecimal("0.00");
        //遍历当前项目下，二级科目的所有月份的预算分摊值
        if (!CollectionUtils.isEmpty(maxMonthsList)) {
            for (PageData data1 : maxMonthsList) {
                //所有小于等于所选月份的月份的数量toThisMonthNumber，结束年度月份 - 最大月份
                //周期变更时，使用传入参数：结束年度月份
                int toThisMonthNumber = 0 ;
                String belongingMonth = data1.getString("belonging_month") + "-01";

                Calendar maxMonthCalendar = Calendar.getInstance();
                try {
                    maxMonthCalendar.setTime(sdf.parse(belongingMonth));
                    int differMaxMonthYears = (endCalendar.get(Calendar.YEAR) - maxMonthCalendar.get(Calendar.YEAR)) * 12;
                    int differMaxMonth = endCalendar.get(Calendar.MONTH) - maxMonthCalendar.get(Calendar.MONTH);

                    toThisMonthNumber = differMaxMonthYears + differMaxMonth;
                } catch (Exception e){
                    throw new MyException("所有小于等于所选月份的月份的数量toThisMonthNumber值计算异常", e);
                }

                data1.put("toThisMonthAllBudget",data1.getString("sumAmount"));
                data1.put("toThisMonthNumber",toThisMonthNumber);
                data1.put("allMonthNumber",allMonthNumber);
            }
        }
        logger.info("计算截止到费用支出已审批完成的各个二级科目的最大月份值的累计预算值,maxMonthsList=[{}]", maxMonthsList);

        //根据projectApplyMainId、对应的经费预算项的字典类型ID，获取每月的预算值,并计算每月的月分摊值
        List<PageData> monthsBudgetList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthList", pd);
        if (!CollectionUtils.isEmpty(monthsBudgetList)){
            for (PageData data : monthsBudgetList) {
                for (PageData data1 : maxMonthsList) {
                    if (data.getString("expenseAccountCode").equals(data1.getString("dic_type_id"))){
                        expenseBudget = new BigDecimal(data.getString("expenseBudget"));
                        BigDecimal surplusMonthNumber = new BigDecimal(data1.getString("allMonthNumber")).subtract(new BigDecimal(data1.getString("toThisMonthNumber")));
                        //计算公式=剩余金额/剩余月份
                        //其中剩余金额是指变更后金额-已扣除金额，剩余月份是指扣除最后月份的下一个月到结束月份为多少个月
                        if (new BigDecimal(data1.getString("toThisMonthNumber")).compareTo(BigDecimal.ZERO) != 0 &&
                                surplusMonthNumber.compareTo(BigDecimal.ZERO) != 0){
                            monthShare = expenseBudget.subtract(new BigDecimal(data1.getString("toThisMonthAllBudget")))
                                    .divide(surplusMonthNumber,2, RoundingMode.HALF_UP);
                        } else {
                            //当surplusMonthNumber为零时，说明没有支出，以开始年度月份值开始计算分摊值
                            monthShare = expenseBudget.subtract(new BigDecimal(data1.getString("toThisMonthAllBudget")))
                                    .divide(new BigDecimal(data1.getString("allMonthNumber")),2, RoundingMode.HALF_UP);
                        }

                        data1.put("monthShare",monthShare);
                    }
                }
            }
        }
        logger.info("根据projectApplyMainId、对应的经费预算项的字典类型ID，获取每月的预算值,并计算每月的月分摊值,maxMonthsList=[{}]", maxMonthsList);

        //更新剩余月份的分摊值
        //获取每月预算的详情
        //如果是周期变更，则需要使用传入的参数：detailList，即每月预算的详情
        List<PageData> detailList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthDetailList", pd);
        if (!CollectionUtils.isEmpty(detailList)){
            for (Map<String, String> map : detailList) {
                String monthsStr = "";
                Iterator<String> it=  map.keySet().iterator();
                while(it.hasNext()){
                    String key = it.next();

                    for (int i = 0; i < 12; i++) {
                        int m = i + 1;
                        if (key.equals(monthsArr[i]) && i < 9) {
                            monthsStr = map.get("years") + "-0" + m;
                        } else if (key.equals(monthsArr[i]) && i >= 9) {
                            monthsStr = map.get("years") + "-" + m;
                        }
                    }

                    //必须先处理不需要合计的预算项的分摊值
                    if (!CollectionUtils.isEmpty(maxMonthsList) && !key.equals("years")
                            && !key.equals("expenseAccountCode") && !key.equals("expenseAccount") && !key.equals("businessId")){
                        for (PageData data : maxMonthsList) {
                            try {
                                //判断有没有费用支出项，如果有费用支出项，则按费用支出项的所属月份的后一个月开始分摊
                                if (!CollectionUtils.isEmpty(secondarySubjectCodeList)){
                                    //分摊值，是在最大费用支出月份之后，且在结束年度月份之前
                                    if (StringUtil.isNotBlank(monthsStr) &&
                                            map.get("expenseAccountCode").equals(data.getString("dic_type_id")) &&
                                            monthFormat.parse(monthsStr).after(monthFormat.parse(data.getString("belonging_month"))) &&
                                            !monthFormat.parse(monthsStr).after(monthFormat.parse(blPageData.getString("endYear")))) {
                                        map.put(key,data.getString("monthShare"));
                                    }
                                } else {
                                    //如果没有费用支出项，则按研发项目开始年度月份值开始分摊
                                    if (StringUtil.isNotBlank(monthsStr) &&
                                            map.get("expenseAccountCode").equals(data.getString("dic_type_id")) &&
                                            !monthFormat.parse(monthsStr).before(monthFormat.parse(data.getString("belonging_month"))) &&
                                            !monthFormat.parse(monthsStr).after(monthFormat.parse(blPageData.getString("endYear")))) {
                                        map.put(key,data.getString("monthShare"));
                                    }
                                }

                            } catch (Exception e) {
                                throw new MyException("更新剩余月份的分摊值失败", e);
                            }
                        }
                    }
                }
            }
            logger.info("获取每月预算的详情,更新剩余月份的分摊值,maxMonthsList=[{}]", detailList);

        }

        //再处理需要合计的预算项的分摊值
        //查询需要合计的预算项
        List<PageData> sumDicEnumList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.querySumDicEnumList", dicTypeIdList);
        logger.info("查询需要合计的预算项,sumDicEnumList=[{}]", sumDicEnumList);

        BigDecimal january = new BigDecimal("0.00");
        BigDecimal february = new BigDecimal("0.00");
        BigDecimal march = new BigDecimal("0.00");
        BigDecimal april = new BigDecimal("0.00");
        BigDecimal may = new BigDecimal("0.00");
        BigDecimal june = new BigDecimal("0.00");
        BigDecimal july = new BigDecimal("0.00");
        BigDecimal august = new BigDecimal("0.00");
        BigDecimal september = new BigDecimal("0.00");
        BigDecimal october = new BigDecimal("0.00");
        BigDecimal november = new BigDecimal("0.00");
        BigDecimal december = new BigDecimal("0.00");
        //计算需要合计的预算项的分摊合计值
        if (!CollectionUtils.isEmpty(sumDicEnumList)){
            for (PageData dicData : sumDicEnumList) {
                //需要合计的预算项，在数据字典中是以字典类型ID-字典枚举ID的格式进行配置的，这里取出字典枚举ID
                //而非合计的预算项，则是以二级科目字典枚举ID-字典类型ID的格式进行配置
                String dicEnumId = dicData.getString("dic_enum_id").split("-")[1];

                //取出需要合计的一条信息，会存在一年以上的多条数据，因多年的多条数据值都是一样，所以只需要取出一条信息即可
                for (PageData detailData : detailList) {
                    if (String.valueOf(dicEnumId).equals(String.valueOf(detailData.getString("expenseAccountCode")))){
                        //计算需要合计的预算项下的所有预算项之和
                        if (!detailData.getString("january").isEmpty()){
                            january = january.add(new BigDecimal(detailData.getString("january")));
                        }
                        if (!detailData.getString("february").isEmpty()){
                            february = february.add(new BigDecimal(detailData.getString("february")));
                        }
                        if (!detailData.getString("march").isEmpty()){
                            march = march.add(new BigDecimal(detailData.getString("march")));
                        }
                        if (!detailData.getString("april").isEmpty()){
                            april = april.add(new BigDecimal(detailData.getString("april")));
                        }
                        if (!detailData.getString("may").isEmpty()){
                            may = may.add(new BigDecimal(detailData.getString("may")));
                        }
                        if (!detailData.getString("june").isEmpty()){
                            june = june.add(new BigDecimal(detailData.getString("june")));
                        }
                        if (!detailData.getString("july").isEmpty()){
                            july = july.add(new BigDecimal(detailData.getString("july")));
                        }
                        if (!detailData.getString("august").isEmpty()){
                            august = august.add(new BigDecimal(detailData.getString("august")));
                        }
                        if (!detailData.getString("september").isEmpty()){
                            september = september.add(new BigDecimal(detailData.getString("september")));
                        }
                        if (!detailData.getString("october").isEmpty()){
                            october = october.add(new BigDecimal(detailData.getString("october")));
                        }
                        if (!detailData.getString("november").isEmpty()){
                            november = november.add(new BigDecimal(detailData.getString("november")));
                        }
                        if (!detailData.getString("december").isEmpty()){
                            december = december.add(new BigDecimal(detailData.getString("december")));
                        }
                        break;
                    }
                }

                //需要合计的预算项，在数据字典中是以字典类型ID-字典枚举ID的格式进行配置的，这里取出字典类型ID
                String dicTypeId = dicData.getString("dic_enum_id").split("-")[0];
                //更改需要合计的预算项的月份值，即是需要合计的预算项下的所有预算项之和，并赋值
                for (PageData detailData : detailList) {
                    if (dicTypeId.equals(detailData.getString("expenseAccountCode"))){
                        detailData.put("january",january);
                        detailData.put("february",february);
                        detailData.put("march",march);
                        detailData.put("april",april);
                        detailData.put("may",may);
                        detailData.put("june",june);
                        detailData.put("july",july);
                        detailData.put("august",august);
                        detailData.put("september",september);
                        detailData.put("october",october);
                        detailData.put("november",november);
                        detailData.put("december",december);
                    }
                }
            }
        }

        //更新剩余月份的分摊值
        //先删除，再插入
        baseDao.delete("ItemChangeMapper.deleteBudgetMonthDetail", projectApplyMainId);
        logger.info("预算变更===先删除每月预算的详情,projectApplyMainId=[{}]", projectApplyMainId);
        baseDao.batchInsert("ItemChangeMapper.batchInsertBudgetMonthDetail", detailList);
        logger.info("预算变更===插入每月预算的详情成功");

    }
}
