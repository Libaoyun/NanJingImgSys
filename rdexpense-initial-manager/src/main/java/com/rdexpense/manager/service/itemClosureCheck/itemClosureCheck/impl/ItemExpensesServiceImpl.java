package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.itemClosureCheck.ItemExpensesService;
import jodd.util.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
        List<String> firstSubjectCodeList = JSONArray.parseArray(pd.getString("firstSubjectCode"), String.class);
        pd.put("firstSubjectCode", firstSubjectCodeList);
        List<String> secondarySubjectCodeList = JSONArray.parseArray(pd.getString("secondarySubjectCode"), String.class);
        pd.put("secondarySubjectCode", secondarySubjectCodeList);
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);
        pd.put("processQuitStatus", ConstantValUtil.APPROVAL_STATUS[3]);
        pd.put("selectCreateUser", pd.getString("selectCreateUser"));
        List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryList", pd);

        return itemExpensesList;
    }

    /**
     * 查询预算总额、已支出累计
     * @param pd
     * @return
     */
    @Override
    public PageData queryBudgetAccumulated(PageData pd) {
        PageData request = new PageData();

        List<PageData> dicTypeList = null;
        List<String> dicTypeIdList = new ArrayList<>();
        if (pd.getString("bureauLevel").equals("0")){
            //不是局级模板项目
            //查询不是局级模板项目对应的经费支出预算字典类型的类型ID
            dicTypeList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicTypeNoBureauEnumList", null);
        } else {
            //是局级模板项目
            //查询是局级模板项目对应的经费支出预算字典类型的类型ID
            dicTypeList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicTypeBureauList", null);
        }

        if (!CollectionUtils.isEmpty(dicTypeList)){
            for (PageData pageData : dicTypeList) {
                dicTypeIdList.add(pageData.getString("dic_type_id"));
            }

            pd.put("dicTypeIdList",dicTypeIdList);
        }

        //根据二级科目，获取对应经费预算的支出费
        List<String> typeIdList = new ArrayList<>();
        if (pd.getString("secondarySubjectCode").equals("TRFY0005")){
            //二级科目为样品购置费时，对应的预算项目有两个，加工试验费和软件测试费
            List<PageData> dicPageDataList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicEnum", pd);
            if (!CollectionUtils.isEmpty(dicPageDataList)){
                for (PageData pageData : dicPageDataList) {
                    typeIdList.add(pageData.getString("dic_type_id"));
                }
                pd.put("dicEnumTypeIdList",dicPageDataList);
            }
            //获取预算总额--支出预算合计，取加工试验费和软件测试费的预算之和,预算为万元单位，换算为元单位
            PageData budgetPageData = (PageData) baseDao.findForObject("ItemExpensesMapper.queryBudgetBYDicEnumTypeIdList", pd);
            if (budgetPageData != null){
                if (!budgetPageData.getString("expenseBudgetChange").isEmpty()){
                    request.put("budgetAmount", new BigDecimal(budgetPageData.getString("expenseBudgetChange")).multiply(new BigDecimal("10000")));
                } else {
                    if (!budgetPageData.getString("expenseBudget").isEmpty()){
                        request.put("budgetAmount", new BigDecimal(budgetPageData.getString("expenseBudget")).multiply(new BigDecimal("10000")));
                    } else {
                        request.put("budgetAmount", 0);
                    }
                }
            } else {
                request.put("budgetAmount", 0);
            }
        } else {
            //二级科目非样品购置费时，对应的预算项目有一个
            PageData dicPageData = (PageData) baseDao.findForObject("ItemExpensesMapper.queryDicEnum", pd);
            if (dicPageData != null){
                typeIdList.add(dicPageData.getString("dic_type_id"));
                pd.put("dicTypeId",dicPageData.getString("dic_type_id"));
            }
            //获取预算总额--支出预算合计
            PageData budgetPageData = (PageData) baseDao.findForObject("ItemExpensesMapper.queryBudget", pd);
            if (budgetPageData != null){
                if (!budgetPageData.getString("expenseBudgetChange").isEmpty()){
                    request.put("budgetAmount", new BigDecimal(budgetPageData.getString("expenseBudgetChange")).multiply(new BigDecimal("10000")));
                } else {
                    if (!budgetPageData.getString("expenseBudget").isEmpty()){
                        request.put("budgetAmount", new BigDecimal(budgetPageData.getString("expenseBudget")).multiply(new BigDecimal("10000")));
                    } else {
                        request.put("budgetAmount", 0);
                    }
                }
            } else {
                request.put("budgetAmount", 0);
            }
        }

        //再根据项目ID、字段类型编码，找出对应经费支出预算的枚举值,即二级科目的字典编码
        List<String> secondarySubjectCodeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(typeIdList)){
            List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicEnumList", typeIdList);
            if (!CollectionUtils.isEmpty(dicEnumList)){
                for (PageData pageData : dicEnumList) {
                    String secondarySubjectCodeStr = pageData.getString("dic_enum_id").split("-")[0].toString();
                    secondarySubjectCodeList.add(secondarySubjectCodeStr);
                }
                pd.put("secondarySubjectCodeList",secondarySubjectCodeList);
            }


            //获取已支出累计，同一个项目同一个科目下已提交（包括审批没有完成的）的单据的支出金额累加
            List<String> processStatusList = new ArrayList<>();
            //已提交
            processStatusList.add(ConstantValUtil.APPROVAL_STATUS[5]);
            //审批中
            processStatusList.add(ConstantValUtil.APPROVAL_STATUS[1]);
            //已通过
            processStatusList.add(ConstantValUtil.APPROVAL_STATUS[4]);
            pd.put("processStatusList", processStatusList);

            PageData amountSumPageData = (PageData) baseDao.findForObject("ItemExpensesMapper.queryAmountSum", pd);
            if (amountSumPageData != null){
                request.put("accumulatedExpenditure", amountSumPageData.getString("accumulatedExpenditure"));
            } else {
                request.put("accumulatedExpenditure", 0);
            }
        } else {
            request.put("accumulatedExpenditure", 0);
        }

        return request;
    }

    /**
     *查询累计支出总额是否超出该类预算5%
     * @param pd
     * @return
     */
    @Override
    public PageData queryBudgetExcessTips(PageData pd) {
        PageData result = new PageData();
        String[] monthsArr = {"january","february","march","april","may","june","july","august","september","october","november","december"};
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");//设置日期格式
        DecimalFormat df = new DecimalFormat("0.00");
        //已累计支出
        BigDecimal accumulatedExpenditure = new BigDecimal(pd.getString("accumulatedExpenditure"));
        //本次金额
        BigDecimal amount = new BigDecimal(pd.getString("amount"));
        //截至当前单据所属月份的预算累计
        BigDecimal toThisMonthAllBudget = new BigDecimal("0.00");

        List<PageData> dicTypeList = null;
        List<String> dicTypeIdList = new ArrayList<>();
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

        if (!CollectionUtils.isEmpty(dicTypeList)){
            for (PageData pageData : dicTypeList) {
                dicTypeIdList.add(pageData.getString("dic_type_id"));
            }

            pd.put("dicTypeIdList",dicTypeIdList);
        }

        List<String> typeIdList = new ArrayList<>();
        if (pd.getString("secondarySubjectCode").equals("TRFY0005")){
            //二级科目为样品购置费时，对应的预算项目有两个，加工试验费和软件测试费
            List<PageData> dicPageDataList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicEnum", pd);
            if (!CollectionUtils.isEmpty(dicPageDataList)){
                for (PageData pageData : dicPageDataList) {
                    typeIdList.add(pageData.getString("dic_type_id"));
                }
                pd.put("dicEnumTypeIdList",typeIdList);
            }
        } else {
            //二级科目非样品购置费时，对应的预算项目有一个
            PageData dicPageData = (PageData) baseDao.findForObject("ItemExpensesMapper.queryDicEnum", pd);
            if (dicPageData != null){
                typeIdList.add(dicPageData.getString("dic_type_id"));
                pd.put("dicTypeId",dicPageData.getString("dic_type_id"));
            }
        }

        //获取截至当前单据所属月份的预算累计
        //根据当前项目、二级科目，获取截至当前单据所有月份的预算分摊值
        List<PageData> toThisMonthAllBudgetList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryBudgetAccumulated", pd);
        if (!CollectionUtils.isEmpty(toThisMonthAllBudgetList)){
            //遍历当前项目下，二级科目的所有月份的预算分摊值
            for (PageData pageData : toThisMonthAllBudgetList) {
                String monthsStr = "";
                //把数据库中的英文月份，转换为yyyy-MM格式的字符串
                for (int i = 0; i < 12; i++) {
                    int m = i+1;
                    if (pageData.getString("months").equals(monthsArr[i]) && i < 9){
                        monthsStr = pageData.getString("years") + "-0" + m;
                    } else if (pageData.getString("months").equals(monthsArr[i]) && i >= 9) {
                        monthsStr = pageData.getString("years") + "-" + m;
                    }
                }

                //计算所有小于所选月份的月份预算分摊值累计值,包含本月预算分摊值
                try {
                    if (StringUtil.isNotBlank(monthsStr) &&
                            !monthFormat.parse(monthsStr).after(monthFormat.parse(pd.getString("belongingMonth")))){

                        toThisMonthAllBudget = toThisMonthAllBudget.add(new BigDecimal(pageData.getString("budget_share")));
                    }
                } catch (Exception e) {
                    throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
                }
            }
        }

        //计算公式：该项目该类预算累计支付+本次金额-截至当前单据所属月份的预算累计）/截至当前单据所属月份的预算累计*100
        if (toThisMonthAllBudget.compareTo(BigDecimal.ZERO) != 0){
            BigDecimal amount1 = accumulatedExpenditure.add(amount).subtract(toThisMonthAllBudget.multiply(new BigDecimal("10000")))
                    .divide(toThisMonthAllBudget.multiply(new BigDecimal("10000")),3, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            if (amount1.compareTo(new BigDecimal(5)) > 0){
                result.put("tips","累计支出总额已经超出该类预算5%，是否继续提交");
            } else {
                result.put("tips","");
            }
        } else {
            result.put("tips","");
        }

        return result;
    }

    /**
     * 删除
     * @param pd
     */
    @Override
    @Transactional
    public void deleteItemExpenses(PageData pd) {
        List<String> businessIdList = JSONArray.parseArray(pd.getString("businessIdList"), String.class);

        List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDetailExportExcel", businessIdList);

        //先删除主表
        baseDao.batchDelete("ItemExpensesMapper.deleteItemExpenses", businessIdList);
        //先删除附件表
        fileService.deleteAttachment(pd);

        if (!CollectionUtils.isEmpty(itemExpensesList)){
            for (PageData pageData : itemExpensesList) {

                pageData.put("processStatus", ConstantValUtil.APPROVAL_STATUS[0]);

                if (pageData.getString("businessIdOther").contains("RDMC")){
                    //租赁计算模块
                    pageData.put("table","equipment_lease_settlement");

                } else if (pageData.getString("businessIdOther").contains("RDZJ")){
                    //设备折旧模块
                    pageData.put("table","equipment_depreciation_expenses");

                } else if (pageData.getString("businessIdOther").contains("YFZC")){
                    //领料单管理模块
                    pageData.put("table","project_material_equisition");

                } else if (pageData.getString("businessIdOther").contains("YFKQ")){
                    //研发人员考勤模块
                    pageData.put("table","researcher_manage_main");
                    List<PageData> list = (List<PageData>)  baseDao.findForList("ItemExpensesMapper.queryBusinessIdOtherSize", pageData);
                    if (list.size() < 2){
                        pd.put("table","researcher_manage_main");
                        //更改其它表的状态
                        baseDao.update("ItemExpensesMapper.updateOtherMainProcessStatus", pageData);
                    }
                } else {
                    //研发项目费用支出
                    pageData.put("table","item_expenses");
                }

                //更改其它表的状态
                if (!pageData.getString("businessIdOther").contains("YFKQ")){
                    baseDao.update("ItemExpensesMapper.deleteUpdateOtherMainProcessStatus", pageData);
                }
            }
        }

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
    @Transactional
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

        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[5]);

        //判断从其它模块跳转过来的单据，在费用支出模块中进行提交
        if (pd.getString("businessIdOther").contains("RDMC")){
            //租赁申请模块
            pd.put("table","equipment_lease_settlement");
            //更改其它表的状态
            baseDao.update("ItemExpensesMapper.updateOtherMainProcessStatus", pd);
        } else if (pd.getString("businessIdOther").contains("RDZJ")){
            //设备折旧模块
            pd.put("table","equipment_depreciation_expenses");
            //更改其它表的状态
            baseDao.update("ItemExpensesMapper.updateOtherMainProcessStatus", pd);
        } else if (pd.getString("businessIdOther").contains("YFZC")){
            //领料单管理模块
            pd.put("table","project_material_equisition");
            //更改其它表的状态
            baseDao.update("ItemExpensesMapper.updateOtherMainProcessStatus", pd);
        } else if (pd.getString("businessIdOther").contains("YFKQ")){
            //研发人员考勤模块

            List<PageData> list = (List<PageData>)  baseDao.findForList("ItemExpensesMapper.queryBusinessIdOtherSize", pd);
            if (list.size() == 2){
                pd.put("table","researcher_manage_main");
                //更改其它表的状态
                baseDao.update("ItemExpensesMapper.updateOtherMainProcessStatus", pd);
            }
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
            String serialNumber = SequenceUtil.generateSerialNo("FYZC");//生成流水号
            String businessId = "FYZC" + UUID.randomUUID().toString();//生成业务主键ID
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

        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[1]);
        //更改费用支出表的状态
        baseDao.update("ItemExpensesMapper.updateMainProcessStatus", pd);

    }

    /**
     * 审批
     * @param pd
     */
    @Override
    @Transactional
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

        //审批完成时
        if (pd.getString("processStatus").equals(ConstantValUtil.APPROVAL_STATUS[4]) &&
                !pd.getString("businessIdOther").isEmpty()){
            boolean flag = false;
            //判断从其它模块跳转过来的单据，在费用支出模块中进行提交
            if (pd.getString("businessIdOther").contains("RDMC")){
                //租赁计算模块
                pd.put("table","equipment_lease_settlement");

            } else if (pd.getString("businessIdOther").contains("RDZJ")){
                //设备折旧模块
                pd.put("table","equipment_depreciation_expenses");

            } else if (pd.getString("businessIdOther").contains("YFZC")){
                //领料单管理模块
                pd.put("table","project_material_equisition");

            } else if (pd.getString("businessIdOther").contains("YFKQ")){
                //研发人员考勤模块
                pd.put("table","researcher_manage_main");
                List<PageData> list = (List<PageData>)  baseDao.findForList("ItemExpensesMapper.queryBusinessIdOtherProcessStatus", pd);
                if (list.size() == 2){
                    baseDao.update("ItemExpensesMapper.updateOtherMainProcessStatus", pd);
                }
            }

            //更改其它表的状态
            if (!pd.getString("businessIdOther").contains("YFKQ")){
                baseDao.update("ItemExpensesMapper.updateOtherMainProcessStatus", pd);
            }
        }
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
                cell.setCellValue(pd.getString("secondarySubject"));
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
    @SneakyThrows
    @Override
    public void exportPDF(PageData data, Document document)  {
        //设置logo和标题
        PDFUtil.addTitle(document,"研究开发项目支出审批表");
        //设置基本信息
        int width1[] = {140, 400, 140, 300, 140, 160, 150, 250};//每栏的宽度
        String[] argArr1 = {
                "编制单位", data.getString("creatorOrgName"), "创建人", data.getString("createUser"),
                "创建时间", data.getString("createdDate"), "单据编号", data.getString("serialNumber"),
                "项目名称",data.getString("projectName"),"项目负责人",data.getString("applyUserName"),
                "所属月份",data.getString("belongingMonth"),"一级科目",data.getString("firstSubject"),
                "二级科目",data.getString("secondarySubject"), "预算总额",data.getString("budgetAmount"),
                "已累计支出",data.getString("accumulatedExpenditure"), "预算结余",data.getString("budgetBalance"),
                "本次金额(元)",data.getString("amount"), "结余金额(元)",data.getString("balanceAmount"),
                "","","","",
                "支出依据",data.getString("payNoted"),"申报意见",data.getString("remark")
                };
        HashMap<Integer,Integer> mergeMap=new HashMap<>();
        mergeMap.put(33,7);
        mergeMap.put(35, 7);
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
