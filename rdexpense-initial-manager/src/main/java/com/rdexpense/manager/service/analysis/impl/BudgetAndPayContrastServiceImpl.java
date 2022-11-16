package com.rdexpense.manager.service.analysis.impl;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.ConstantValUtil;
import com.common.util.ExcelUtil;
import com.rdexpense.manager.service.analysis.BudgetAndPayContrastService;
import com.rdexpense.manager.service.analysis.CostAddDeductionDetailSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.assertj.core.internal.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.ion.Decimal;

import javax.annotation.Resource;
import javax.swing.plaf.metal.MetalIconFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class BudgetAndPayContrastServiceImpl implements BudgetAndPayContrastService {
    private static final Logger logger = LoggerFactory.getLogger(BudgetAndPayContrastServiceImpl.class);

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    private  static final String[] arr = {"january","february","march","april","may","june","july","august","september","october","november","december"};

    /**
     * 查询列表
     *
     * @param
     * @return
     */
    @Override
    public List queryList(PageData pd) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        DecimalFormat df = new DecimalFormat("0.00");

        List<PageData> budgetList = new ArrayList<>() ;
        List<PageData> budgetAllList = new ArrayList<>() ;
        List<PageData> budgetNewList = new ArrayList<>();

        //获取对应经费支出预算的数据字典
        List<String> dicList = new ArrayList<>();
        dicList.add("1023");
        List<PageData> dicTypeList = (List<PageData>) baseDao.findForList("DictionaryTypeMapper.selectListByParentIds", dicList);

        String startYear = pd.getString("startMonth").substring(0,4);
        String endYear = pd.getString("endMonth").substring(0,4);
        Calendar startYearCalendar = Calendar.getInstance();
        Calendar endYearCalendar = Calendar.getInstance();
        try {
            startYearCalendar.setTime(sdf.parse(pd.getString("startMonth")));
            endYearCalendar.setTime(sdf.parse(pd.getString("endMonth")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int startYearInt = Integer.valueOf(startYear);
        int endYearInt = Integer.valueOf(endYear);
        int startMonthInt = startYearCalendar.get(Calendar.MONTH);
        int endMonthInt = endYearCalendar.get(Calendar.MONTH);
        pd.put("startYear",startYear);
        pd.put("endYear",endYear);

        pd.put("startYearInt",startYearInt);
        pd.put("endYearInt",endYearInt);

        pd.put("startMonthInt",startMonthInt);
        pd.put("endMonthInt",endMonthInt);


        //根据开始月份和结束月份，获取一个工程项目下的所有立项项目下的预算支出的立项项目信息
        if (pd.getString("flag").equals("1")) {
            List<PageData> projectInfoList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryProjectByYear", pd);

            //获取一个工程项目下的所有立项项目的来源预算
            List<PageData> projectBudgetList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(projectInfoList)){
                pd.put("projectInfoList",projectInfoList);
                projectBudgetList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryAllProjectBudget", pd);


                if (!CollectionUtils.isEmpty(projectBudgetList)){

                    //获取一个工程项目下的所有立项项目来源预算的信息
                    if (!CollectionUtils.isEmpty(dicTypeList)){
                        for (PageData pageData : dicTypeList) {
                            String dicTypeId = pageData.getString("dicTypeId");

                            BigDecimal sourceBudgetSum = new BigDecimal("0.00");

                            PageData budgetPageData = new PageData();
                            if (!CollectionUtils.isEmpty(projectBudgetList)){
                                for (PageData pageData1 : projectBudgetList) {
                                    if (dicTypeId.equals(pageData1.getString("expenseAccountCode"))){

                                        if (!StringUtils.isEmpty(pageData1.getString("sourceBudget"))){
                                            sourceBudgetSum = sourceBudgetSum.add(new BigDecimal(pageData1.getString("sourceBudget")));
                                        }

                                        budgetPageData.put("creatorOrgId",pd.getString("creatorOrgId"));
                                        budgetPageData.put("sourceAccount",pageData1.getString("sourceAccount"));
                                        budgetPageData.put("sourceBudget",sourceBudgetSum);
                                        budgetPageData.put("expenseAccount",pageData1.getString("expenseAccount"));
                                        budgetPageData.put("expenseBudget","");
                                        budgetPageData.put("expenseAccount",pageData.getString("dicTypeName"));
                                        budgetPageData.put("expenseAccountCode",pageData1.getString("expenseAccountCode"));
                                        budgetPageData.put("payBudgetRatio","");
                                    }
                                }
                            }

                            budgetList.add(budgetPageData);
                        }
                    }
                }

                //获取一个工程项目下的所有立项项目的支出预算
                List<PageData> projectExpenseBudgetList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryProjectExpenseBudget", pd);
                if (!CollectionUtils.isEmpty(budgetList)){

                    TreeMap<String, List<PageData>> map = null;
                    if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {
                        map = projectExpenseBudgetList.stream().collect(Collectors.groupingBy(o -> o.getString("years"), TreeMap::new, Collectors.toList()));
                    }

                    for (PageData pageData : budgetList) {
                        String expenseAccount = pageData.getString("expenseAccount");

                        if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {

                            BigDecimal expenseBudgetSum = new BigDecimal("0.00");

                            //开始年等于结束年
                            if (startYearInt == endYearInt){
                                for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    List<PageData> valueList = entry.getValue();
                                    for (PageData detailData : valueList) {
                                        if (detailData.getString("expenseAccount").equals(expenseAccount)) {
                                            for (int i = startMonthInt; i <= endMonthInt; i++) {
                                                expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                            }
                                        }
                                    }
                                }
                            } else {
                                //开始年不等于结束年
                                //开始年月
                                for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    List<PageData> valueList = entry.getValue();
                                    for (PageData detailData : valueList) {
                                        if (startYearInt == Integer.valueOf(key)  &&
                                                detailData.getString("expenseAccount").equals(expenseAccount)) {
                                            for (int i = startMonthInt; i < 12; i++) {
                                                expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                            }
                                        }
                                    }
                                }

                                //结束年月
                                for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    List<PageData> valueList = entry.getValue();
                                    for (PageData detailData : valueList) {
                                        if (endYearInt == Integer.valueOf(key) &&
                                                detailData.getString("expenseAccount").equals(expenseAccount)) {
                                            for (int i = 0; i <= endMonthInt; i++) {
                                                expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                            }
                                        }
                                    }
                                }

                                //中间年月
                                for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    List<PageData> valueList = entry.getValue();
                                    for (PageData detailData : valueList) {
                                        if (startYearInt != Integer.valueOf(key) && endYearInt != Integer.valueOf(key) &&
                                                detailData.getString("expenseAccount").equals(expenseAccount)) {
                                            for (int i = 0; i < 12; i++) {
                                                expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                            }
                                        }
                                    }
                                }

                            }
                            pageData.put("expenseBudget",expenseBudgetSum);
                        }
                    }
                }

                //获取工程项目下所有的费用支出信息
                List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryBudgetExpenses", pd);
                if (!CollectionUtils.isEmpty(budgetList)){
                    for (PageData pageData : budgetList) {
                        BigDecimal expensesBudgetSum = new BigDecimal("0.00");

                        //根据对应经费支出预算，查找枚举值，并获取枚举值字典编码
                        List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectDicEnumByParentIds", pageData);
                        if (!CollectionUtils.isEmpty(dicEnumList)){
                            for (PageData dicEnumPageData : dicEnumList) {
                                if (!CollectionUtils.isEmpty(itemExpensesList)){
                                    for (PageData expensesPageData : itemExpensesList) {
                                        String dic_enum_id = dicEnumPageData.getString("dic_enum_id").split("-")[0];
                                        if (expensesPageData.getString("secondarySubjectCode").equals(dic_enum_id)){
                                            if (!StringUtils.isEmpty(expensesPageData.getString("amount"))){
                                                expensesBudgetSum = expensesBudgetSum.add(new BigDecimal(expensesPageData.getString("amount")));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        pageData.put("actualPay",expensesBudgetSum.divide(new BigDecimal(10000),2,RoundingMode.HALF_UP));

                        budgetAllList.add(pageData);
                    }

                    //计算支出与预算比例
                    //支出预算合计
                    for (PageData pageData : budgetAllList) {
                        BigDecimal payBudgetRadio = new BigDecimal("0.00");
                        if (!pageData.getString("expenseBudget").equals("0.00") &&
                                !StringUtils.isEmpty(pageData.getString("expenseBudget"))){
                            payBudgetRadio = new BigDecimal(pageData.getString("actualPay")).divide(new BigDecimal(pageData.getString("expenseBudget")),4,RoundingMode.HALF_UP);
                        }

                        if (pageData.getString("sourceBudget").equals("0.00") &&
                                pageData.getString("sourceAccount").isEmpty() ){
                            pageData.put("sourceBudget","");
                        }

                        pageData.put("payBudgetRatio",df.format(payBudgetRadio.multiply(new BigDecimal(100))) + "%");
                    }

                    //计算九、其他费用的费用
                    for (PageData pageData : budgetAllList) {
                        if (pageData.getString("expenseAccount").equals("九、其他费用")){
                            BigDecimal expenseBudgetSum = new BigDecimal("0.00");
                            expenseBudgetSum = new BigDecimal(pageData.getString("expenseBudget"));
                            BigDecimal actualPaySum1 = new BigDecimal("0.00");
                            List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectDicEnumByParentIds", pageData);
                            if (!CollectionUtils.isEmpty(dicEnumList)){
                                for (PageData data : dicEnumList) {
                                    String dic_enum_id = data.getString("dic_enum_id").split("-")[1];
                                    for (PageData pageData1 : budgetAllList) {

                                        if (pageData1.getString("expenseAccountCode").equals(dic_enum_id)){
                                            actualPaySum1 = actualPaySum1.add(new BigDecimal(pageData1.getString("actualPay")));
                                        }
                                    }
                                }
                            }

                            pageData.put("actualPay",actualPaySum1);
                            if (expenseBudgetSum.compareTo(new BigDecimal(0)) != 0){
                                pageData.put("payBudgetRatio",df.format(actualPaySum1.divide(expenseBudgetSum,4,RoundingMode.HALF_UP).multiply(new BigDecimal(100))) + "%");
                            }
                        }
                    }

                    //计算支出预算合计的费用
                    for (PageData pageData : budgetAllList) {
                        if (pageData.getString("expenseAccount").equals("支出预算合计")){
                            BigDecimal expenseBudgetSum = new BigDecimal("0.00");
                            if (!StringUtils.isEmpty(pageData.getString("expenseBudget"))){
                                expenseBudgetSum = new BigDecimal(pageData.getString("expenseBudget"));
                            }

                            BigDecimal actualPaySum1 = new BigDecimal("0.00");
                            List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectDicEnumByParentIds", pageData);
                            if (!CollectionUtils.isEmpty(dicEnumList)){
                                for (PageData data : dicEnumList) {
                                    String dic_enum_id = data.getString("dic_enum_id").split("-")[1];
                                    for (PageData pageData1 : budgetAllList) {

                                        if (pageData1.getString("expenseAccountCode").equals(dic_enum_id)){
                                            actualPaySum1 = actualPaySum1.add(new BigDecimal(pageData1.getString("actualPay")));
                                        }
                                    }
                                }
                            }

                            pageData.put("actualPay",actualPaySum1);
                            if (expenseBudgetSum.compareTo(new BigDecimal(0)) != 0){
                                pageData.put("payBudgetRatio",df.format(actualPaySum1.divide(expenseBudgetSum,4,RoundingMode.HALF_UP).multiply(new BigDecimal(100))) + "%");
                            }

                            budgetNewList.add(pageData);
                        }
                    }


                    for (PageData pageData : budgetAllList) {
                        if (!pageData.getString("expenseAccount").equals("支出预算合计") &&
                                !pageData.getString("expenseAccount").equals("九、其他费用") &&
                                !pageData.getString("expenseAccount").equals("十一、委托研发费用") &&
                                !pageData.getString("expenseAccount").equals("十、新产品设计费") &&
                                !pageData.getString("expenseAccount").equals("1、国际合作交流费") &&
                                !pageData.getString("expenseAccount").equals("2、出版/文献/信息传播") &&
                                !pageData.getString("expenseAccount").equals("3、知识产权事务") &&
                                !pageData.getString("expenseAccount").equals("4、专家费") &&
                                !pageData.getString("expenseAccount").equals("5、其他")){

                            budgetNewList.add(pageData);
                        }
                    }

                    for (PageData pageData : budgetAllList) {
                        if (pageData.getString("expenseAccount").equals("九、其他费用")){
                            budgetNewList.add(pageData);
                        }
                    }

                    for (PageData pageData : budgetAllList) {
                        if (pageData.getString("expenseAccount").equals("1、国际合作交流费") ||
                                pageData.getString("expenseAccount").equals("2、出版/文献/信息传播") ||
                                pageData.getString("expenseAccount").equals("3、知识产权事务") ||
                                pageData.getString("expenseAccount").equals("4、专家费") ||
                                pageData.getString("expenseAccount").equals("5、其他")){
                            budgetNewList.add(pageData);
                        }
                    }

                    for (PageData pageData : budgetAllList) {
                        if (pageData.getString("expenseAccount").equals("十、新产品设计费")){
                            budgetNewList.add(pageData);
                        }
                    }

                    for (PageData pageData : budgetAllList) {
                        if (pageData.getString("expenseAccount").equals("十一、委托研发费用")){
                            budgetNewList.add(pageData);
                        }
                    }

                }
            }
        } else {
            //公司，需要获取公司下面的所有工程项目信息
            List<PageData> orgIdList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryOrgIdList", pd);
            if (!CollectionUtils.isEmpty(orgIdList)){
                pd.put("orgIdList",orgIdList);
            }

            queryCompanyActualPay(pd,dicTypeList,budgetNewList);
        }

        return budgetNewList;
    }



    /**
     * 取公司下的所有工程项目的实际支出金额
     * @param pd
     * @param dicTypeList
     * @param budgetNewList
     */
    private void queryCompanyActualPay(PageData pd,List<PageData> dicTypeList,List<PageData> budgetNewList){
        List<PageData> budgetList = new ArrayList<>() ;
        List<PageData> budgetAllList = new ArrayList<>() ;

        DecimalFormat df = new DecimalFormat("0.00");

        //获取所有公司下的所有工程项目下立项项目信息
        List<PageData> projectInfoList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryCompanyProjectByYear", pd);
        //获取一个工程项目下的所有立项项目的来源预算
        List<PageData> projectBudgetList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(projectInfoList)){
            pd.put("projectInfoList",projectInfoList);
            projectBudgetList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryAllProjectBudget", pd);


            if (!CollectionUtils.isEmpty(projectBudgetList)){

                //获取一个工程项目下的所有立项项目来源预算的信息
                if (!CollectionUtils.isEmpty(dicTypeList)){
                    for (PageData pageData : dicTypeList) {
                        String dicTypeId = pageData.getString("dicTypeId");

                        BigDecimal sourceBudgetSum = new BigDecimal("0.00");

                        PageData budgetPageData = new PageData();
                        if (!CollectionUtils.isEmpty(projectBudgetList)){
                            for (PageData pageData1 : projectBudgetList) {
                                if (dicTypeId.equals(pageData1.getString("expenseAccountCode"))){

                                    if (!StringUtils.isEmpty(pageData1.getString("sourceBudget"))){
                                        sourceBudgetSum = sourceBudgetSum.add(new BigDecimal(pageData1.getString("sourceBudget")));
                                    }

                                    budgetPageData.put("creatorOrgId",pd.getString("creatorOrgId"));
                                    budgetPageData.put("sourceAccount",pageData1.getString("sourceAccount"));
                                    budgetPageData.put("sourceBudget",sourceBudgetSum);
                                    budgetPageData.put("expenseAccount",pageData1.getString("expenseAccount"));
                                    budgetPageData.put("expenseBudget","");
                                    budgetPageData.put("expenseAccount",pageData.getString("dicTypeName"));
                                    budgetPageData.put("expenseAccountCode",pageData1.getString("expenseAccountCode"));
                                }
                            }
                        }

                        budgetList.add(budgetPageData);
                    }
                }
            }

            //获取一个工程项目下的所有立项项目的支出预算
            List<PageData> projectExpenseBudgetList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryProjectExpenseBudget", pd);
            if (!CollectionUtils.isEmpty(budgetList)){

                TreeMap<String, List<PageData>> map = null;
                if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {
                    map = projectExpenseBudgetList.stream().collect(Collectors.groupingBy(o -> o.getString("years"), TreeMap::new, Collectors.toList()));
                }

                for (PageData pageData : budgetList) {
                    String expenseAccount = pageData.getString("expenseAccount");

                    if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {

                        BigDecimal expenseBudgetSum = new BigDecimal("0.00");
                        //开始年等于结束年
                        if (pd.getString("startYearInt").equals(pd.getString("endYearInt"))){
                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                String key = entry.getKey();
                                List<PageData> valueList = entry.getValue();
                                for (PageData detailData : valueList) {
                                    if (detailData.getString("expenseAccount").equals(expenseAccount)) {
                                        int startMonthInt1 = Integer.parseInt(pd.getString("startMonthInt"));
                                        int endMonthInt1 = Integer.parseInt(pd.getString("endMonthInt"));
                                        for (int i = startMonthInt1; i <= endMonthInt1; i++) {
                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                        }
                                    }
                                }
                            }
                        } else {
                            //开始年不等于结束年
                            //开始年月
                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                String key = entry.getKey();
                                List<PageData> valueList = entry.getValue();
                                for (PageData detailData : valueList) {
                                    if (Integer.valueOf(pd.getString("startYearInt")) == Integer.valueOf(key)  &&
                                            detailData.getString("expenseAccount").equals(expenseAccount)) {
                                        for (int i = Integer.valueOf(pd.getString("startMonthInt")); i < 12; i++) {
                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                        }
                                    }
                                }
                            }

                            //结束年月
                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                String key = entry.getKey();
                                List<PageData> valueList = entry.getValue();
                                for (PageData detailData : valueList) {
                                    if (Integer.valueOf(pd.getString("endYearInt")) == Integer.valueOf(key) &&
                                            detailData.getString("expenseAccount").equals(expenseAccount)) {
                                        for (int i = 0; i <= Integer.valueOf(pd.getString("endMonthInt")); i++) {
                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                        }
                                    }
                                }
                            }

                            //中间年月
                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                String key = entry.getKey();
                                List<PageData> valueList = entry.getValue();
                                for (PageData detailData : valueList) {
                                    if (Integer.valueOf(pd.getString("startYearInt")) != Integer.valueOf(key) && Integer.valueOf(pd.getString("endYearInt")) != Integer.valueOf(key) &&
                                            detailData.getString("expenseAccount").equals(expenseAccount)) {
                                        for (int i = 0; i < 12; i++) {
                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                        }
                                    }
                                }
                            }

                        }
                        pageData.put("expenseBudget",expenseBudgetSum);
                    }
                }
            }

            //获取工程项目下所有的费用支出信息
            List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryCompanyBudgetExpenses", pd);
            if (!CollectionUtils.isEmpty(budgetList)){
                for (PageData pageData : budgetList) {
                    BigDecimal expensesBudgetSum = new BigDecimal("0.00");

                    //根据对应经费支出预算，查找枚举值，并获取枚举值字典编码
                    List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectDicEnumByParentIds", pageData);
                    if (!CollectionUtils.isEmpty(dicEnumList)){
                        for (PageData dicEnumPageData : dicEnumList) {
                            if (!CollectionUtils.isEmpty(itemExpensesList)){
                                for (PageData expensesPageData : itemExpensesList) {
                                    String dic_enum_id = dicEnumPageData.getString("dic_enum_id").split("-")[0];
                                    if (expensesPageData.getString("secondarySubjectCode").equals(dic_enum_id)){
                                        if (!StringUtils.isEmpty(expensesPageData.getString("amount"))){
                                            expensesBudgetSum = expensesBudgetSum.add(new BigDecimal(expensesPageData.getString("amount")));
                                        }
                                    }
                                }
                            }

                        }
                    }
                    pageData.put("actualPay",expensesBudgetSum.divide(new BigDecimal(10000),2,RoundingMode.HALF_UP));

                    budgetAllList.add(pageData);
                }

                //计算支出与预算比例
                //支出预算合计
                for (PageData pageData : budgetAllList) {
                    BigDecimal payBudgetRadio = new BigDecimal("0.00");
                    if (!pageData.getString("expenseBudget").equals("0.00") ){
                        payBudgetRadio = new BigDecimal(pageData.getString("actualPay")).divide(new BigDecimal(pageData.getString("expenseBudget")),4,RoundingMode.HALF_UP);
                    }

                    if (pageData.getString("sourceBudget").equals("0.00") &&
                            pageData.getString("sourceAccount").isEmpty() ){
                        pageData.put("sourceBudget","");
                    }

                    pageData.put("payBudgetRatio",df.format(payBudgetRadio.multiply(new BigDecimal(100))) + "%");
                }


                //计算九、其他费用的费用
                for (PageData pageData : budgetAllList) {
                    if (pageData.getString("expenseAccount").equals("九、其他费用")){
                        BigDecimal expenseBudgetSum = new BigDecimal("0.00");
                        expenseBudgetSum = new BigDecimal(pageData.getString("expenseBudget"));
                        BigDecimal actualPaySum1 = new BigDecimal("0.00");
                        List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectDicEnumByParentIds", pageData);
                        if (!CollectionUtils.isEmpty(dicEnumList)){
                            for (PageData data : dicEnumList) {
                                String dic_enum_id = data.getString("dic_enum_id").split("-")[1];
                                for (PageData pageData1 : budgetAllList) {

                                    if (pageData1.getString("expenseAccountCode").equals(dic_enum_id)){
                                        actualPaySum1 = actualPaySum1.add(new BigDecimal(pageData1.getString("actualPay")));
                                    }
                                }
                            }
                        }

                        pageData.put("actualPay",actualPaySum1);
                        if (expenseBudgetSum.compareTo(new BigDecimal(0)) != 0){
                            BigDecimal payBudgetRadio = new BigDecimal("0.00");
                            payBudgetRadio = actualPaySum1.divide(expenseBudgetSum,4,RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                            pageData.put("payBudgetRatio",df.format(payBudgetRadio) + "%");
                        }
                    }
                }

                //计算支出预算合计的费用
                for (PageData pageData : budgetAllList) {
                    if (pageData.getString("expenseAccount").equals("支出预算合计")){
                        BigDecimal expenseBudgetSum = new BigDecimal("0.00");
                        expenseBudgetSum = new BigDecimal(pageData.getString("expenseBudget"));

                        BigDecimal actualPaySum1 = new BigDecimal("0.00");
                        List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectDicEnumByParentIds", pageData);
                        if (!CollectionUtils.isEmpty(dicEnumList)){
                            for (PageData data : dicEnumList) {
                                String dic_enum_id = data.getString("dic_enum_id").split("-")[1];
                                for (PageData pageData1 : budgetAllList) {

                                    if (pageData1.getString("expenseAccountCode").equals(dic_enum_id)){
                                        actualPaySum1 = actualPaySum1.add(new BigDecimal(pageData1.getString("actualPay")));
                                    }
                                }
                            }
                        }

                        pageData.put("actualPay",actualPaySum1);
                        if (expenseBudgetSum.compareTo(new BigDecimal(0)) != 0){
                            pageData.put("payBudgetRatio",df.format(actualPaySum1.divide(expenseBudgetSum,4,RoundingMode.HALF_UP).multiply(new BigDecimal(100))) + "%");
                        }
                        budgetNewList.add(pageData);
                    }
                }

                for (PageData pageData : budgetAllList) {
                    if (!pageData.getString("expenseAccount").equals("支出预算合计") &&
                            !pageData.getString("expenseAccount").equals("九、其他费用") &&
                            !pageData.getString("expenseAccount").equals("十一、委托研发费用") &&
                            !pageData.getString("expenseAccount").equals("十、新产品设计费") &&
                            !pageData.getString("expenseAccount").equals("1、国际合作交流费") &&
                            !pageData.getString("expenseAccount").equals("2、出版/文献/信息传播") &&
                            !pageData.getString("expenseAccount").equals("3、知识产权事务") &&
                            !pageData.getString("expenseAccount").equals("4、专家费") &&
                            !pageData.getString("expenseAccount").equals("5、其他")){

                        budgetNewList.add(pageData);
                    }
                }

                for (PageData pageData : budgetAllList) {
                    if (pageData.getString("expenseAccount").equals("九、其他费用")){
                        budgetNewList.add(pageData);
                    }
                }
                for (PageData pageData : budgetAllList) {
                    if (pageData.getString("expenseAccount").equals("1、国际合作交流费") ||
                            pageData.getString("expenseAccount").equals("2、出版/文献/信息传播") ||
                            pageData.getString("expenseAccount").equals("3、知识产权事务") ||
                            pageData.getString("expenseAccount").equals("4、专家费") ||
                            pageData.getString("expenseAccount").equals("5、其他")){
                        budgetNewList.add(pageData);
                    }
                }

                for (PageData pageData : budgetAllList) {
                    if (pageData.getString("expenseAccount").equals("十、新产品设计费")){
                        budgetNewList.add(pageData);
                    }
                }

                for (PageData pageData : budgetAllList) {
                    if (pageData.getString("expenseAccount").equals("十一、委托研发费用")){
                        budgetNewList.add(pageData);
                    }
                }

            }
        }

    }


    /**
     * 查询立项项目预算与支出列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryProjectBudgetList(PageData pd) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        String startYear = pd.getString("startMonth").substring(0,4);
        String endYear = pd.getString("endMonth").substring(0,4);
        Calendar startYearCalendar = Calendar.getInstance();
        Calendar endYearCalendar = Calendar.getInstance();
        try {
            startYearCalendar.setTime(sdf.parse(pd.getString("startMonth")));
            endYearCalendar.setTime(sdf.parse(pd.getString("endMonth")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int startYearInt = Integer.valueOf(startYear);
        int endYearInt = Integer.valueOf(endYear);
        int startMonthInt = startYearCalendar.get(Calendar.MONTH);
        int endMonthInt = endYearCalendar.get(Calendar.MONTH);
        pd.put("startYear",startYear);
        pd.put("endYear",endYear);

        pd.put("startYearInt",startYearInt);
        pd.put("endYearInt",endYearInt);

        pd.put("startMonthInt",startMonthInt);
        pd.put("endMonthInt",endMonthInt);

        //获取对应经费支出预算的数据字典
        List<String> dicList = new ArrayList<>();
        dicList.add("1023");
        List<PageData> dicTypeList = (List<PageData>) baseDao.findForList("DictionaryTypeMapper.selectListByParentIds", dicList);

        List<PageData> projectInfoList = new ArrayList<>();

        List<PageData> newList = new ArrayList<>();
        //获取一个工程项目下的所有项目的信息
        if (pd.getString("flag").equals("1")){
            projectInfoList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryProjectByYear", pd);

            if (!CollectionUtils.isEmpty(projectInfoList)){

                for (PageData projectInfoPageData : projectInfoList) {

                    BigDecimal expenseBudgetSum = new BigDecimal("0.00");
                    BigDecimal actualPaySum = new BigDecimal("0.00");

                    pd.put("business_id",projectInfoPageData.getString("business_id"));

                    //获取一个立项项目的支出预算
                    List<PageData> projectExpenseBudgetList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryOneProjectExpenseBudget", pd);
                    if (!CollectionUtils.isEmpty(projectExpenseBudgetList)){

                        TreeMap<String, List<PageData>> map = null;
                        if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {
                            map = projectExpenseBudgetList.stream().collect(Collectors.groupingBy(o -> o.getString("years"), TreeMap::new, Collectors.toList()));
                        }


                        for (PageData pageData : dicTypeList) {
                            String dicTypeName = pageData.getString("dicTypeName");

                            if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {
                                //开始年等于结束年
                                if (startYearInt == endYearInt){
                                    for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        List<PageData> valueList = entry.getValue();
                                        for (PageData detailData : valueList) {
                                            if (detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                    dicTypeName.equals("支出预算合计")) {
                                                for (int i = startMonthInt; i <= endMonthInt; i++) {
                                                    expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //开始年不等于结束年
                                    //开始年月
                                    for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        List<PageData> valueList = entry.getValue();
                                        for (PageData detailData : valueList) {
                                            if (startYearInt == Integer.valueOf(key)  &&
                                                    detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                    dicTypeName.equals("支出预算合计")) {
                                                for (int i = startMonthInt; i < 12; i++) {
                                                    expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                }
                                            }
                                        }
                                    }

                                    //结束年月
                                    for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        List<PageData> valueList = entry.getValue();
                                        for (PageData detailData : valueList) {
                                            if (endYearInt == Integer.valueOf(key) &&
                                                    detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                    dicTypeName.equals("支出预算合计")) {
                                                for (int i = 0; i <= endMonthInt; i++) {
                                                    expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                }
                                            }
                                        }
                                    }

                                    //中间年月
                                    for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                        String key = entry.getKey();
                                        List<PageData> valueList = entry.getValue();
                                        for (PageData detailData : valueList) {
                                            if (startYearInt != Integer.valueOf(key) && endYearInt != Integer.valueOf(key) &&
                                                    detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                    dicTypeName.equals("支出预算合计")) {
                                                for (int i = 0; i < 12; i++) {
                                                    expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //获取工程项目下所有的费用支出信息
                    List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryOneBudgetExpenses", pd);
                    if (!CollectionUtils.isEmpty(dicTypeList)) {
                        for (PageData pageData : dicTypeList) {

                            //根据对应经费支出预算，查找枚举值，并获取枚举值字典编码
                            List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectOneDicEnumByParentIds", pageData);
                            if (!CollectionUtils.isEmpty(dicEnumList)) {
                                for (PageData dicEnumPageData : dicEnumList) {
                                    if (!CollectionUtils.isEmpty(itemExpensesList)) {
                                        for (PageData expensesPageData : itemExpensesList) {
                                            String dic_enum_id = dicEnumPageData.getString("dic_enum_id").split("-")[0];
                                            if (expensesPageData.getString("secondarySubjectCode").equals(dic_enum_id)) {
                                                if (!StringUtils.isEmpty(expensesPageData.getString("amount"))) {
                                                    actualPaySum = actualPaySum.add(new BigDecimal(expensesPageData.getString("amount")));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    projectInfoPageData.put("expenseBudget",expenseBudgetSum);
                    projectInfoPageData.put("actualPay",actualPaySum.divide(new BigDecimal(10000)));
                }
            }

        } else {
            List<PageData> orgIdList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryOrgIdList", pd);
            if (!CollectionUtils.isEmpty(orgIdList)){
                pd.put("orgIdList",orgIdList);
                projectInfoList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryCompanyProjectInfo", pd);
            }

            if (!CollectionUtils.isEmpty(projectInfoList)){
                for (PageData orgPageData : orgIdList) {


                    for (PageData projectInfoPageData : projectInfoList) {


                        if (orgPageData.getString("org_number").equals(projectInfoPageData.getString("creatorOrgId"))){

                            PageData newPage = new PageData();

                            BigDecimal actualPaySum = new BigDecimal(0.00);
                            BigDecimal expenseBudgetSum = new BigDecimal(0.00);

                            //获取项目的预算金额

                            pd.put("business_id",projectInfoPageData.getString("business_id"));

                            //获取所有立项项目的支出预算
                            List<PageData> projectExpenseBudgetList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryOneProjectExpenseBudget", pd);
                            if (!CollectionUtils.isEmpty(projectExpenseBudgetList)){

                                TreeMap<String, List<PageData>> map = null;
                                if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {
                                    map = projectExpenseBudgetList.stream().collect(Collectors.groupingBy(o -> o.getString("years"), TreeMap::new, Collectors.toList()));
                                }

                                for (PageData pageData : dicTypeList) {
                                    String dicTypeName = pageData.getString("dicTypeName");

                                    if (!CollectionUtils.isEmpty(projectExpenseBudgetList)) {
                                        //开始年等于结束年
                                        if (startYearInt == endYearInt){
                                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                                String key = entry.getKey();
                                                List<PageData> valueList = entry.getValue();
                                                for (PageData detailData : valueList) {
                                                    if (detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                            dicTypeName.equals("支出预算合计")) {
                                                        for (int i = startMonthInt; i <= endMonthInt; i++) {
                                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            //开始年不等于结束年
                                            //开始年月
                                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                                String key = entry.getKey();
                                                List<PageData> valueList = entry.getValue();
                                                for (PageData detailData : valueList) {
                                                    if (startYearInt == Integer.valueOf(key)  &&
                                                            detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                            dicTypeName.equals("支出预算合计")) {
                                                        for (int i = startMonthInt; i < 12; i++) {
                                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                        }
                                                    }
                                                }
                                            }

                                            //结束年月
                                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                                String key = entry.getKey();
                                                List<PageData> valueList = entry.getValue();
                                                for (PageData detailData : valueList) {
                                                    if (endYearInt == Integer.valueOf(key) &&
                                                            detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                            dicTypeName.equals("支出预算合计")) {
                                                        for (int i = 0; i <= endMonthInt; i++) {
                                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                        }
                                                    }
                                                }
                                            }

                                            //中间年月
                                            for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                                                String key = entry.getKey();
                                                List<PageData> valueList = entry.getValue();
                                                for (PageData detailData : valueList) {
                                                    if (startYearInt != Integer.valueOf(key) && endYearInt != Integer.valueOf(key) &&
                                                            detailData.getString("expenseAccount").equals(dicTypeName) &&
                                                            dicTypeName.equals("支出预算合计")) {
                                                        for (int i = 0; i < 12; i++) {
                                                            expenseBudgetSum = expenseBudgetSum.add(new BigDecimal(detailData.getString(arr[i])));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //获取工程项目下所有的费用支出信息
                            pd.put("creatorOrgId",projectInfoPageData.getString("creatorOrgId"));
                            List<PageData> itemExpensesList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.queryOneBudgetExpenses", pd);
                            if (!CollectionUtils.isEmpty(dicTypeList)) {
                                for (PageData pageData : dicTypeList) {

                                    //根据对应经费支出预算，查找枚举值，并获取枚举值字典编码
                                    List<PageData> dicEnumList = (List<PageData>) baseDao.findForList("BudgetAndPayContrastMapper.selectOneDicEnumByParentIds", pageData);

                                    if (!CollectionUtils.isEmpty(dicEnumList)) {
                                        for (PageData dicEnumPageData : dicEnumList) {

                                            if (!CollectionUtils.isEmpty(itemExpensesList)) {
                                                for (PageData expensesPageData : itemExpensesList) {
                                                    String dic_enum_id = dicEnumPageData.getString("dic_enum_id").split("-")[0];
                                                    if (expensesPageData.getString("secondarySubjectCode").equals(dic_enum_id)) {
                                                        if (!StringUtils.isEmpty(expensesPageData.getString("amount"))) {
                                                            actualPaySum = actualPaySum.add(new BigDecimal(expensesPageData.getString("amount")));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            newPage.put("actualPay",actualPaySum.divide(BigDecimal.valueOf(10000)));
                            newPage.put("expenseBudget",expenseBudgetSum);
                            newPage.put("serialNumber",projectInfoPageData.getString("serialNumber"));
                            newPage.put("projectName",projectInfoPageData.getString("projectName"));
                            newPage.put("startYear",projectInfoPageData.getString("startYear"));
                            newPage.put("endYear",projectInfoPageData.getString("endYear"));
                            newPage.put("creatorOrgId",projectInfoPageData.getString("creatorOrgId"));

                            newList.add(newPage);
                        }
                    }
                }
            }
            projectInfoList.clear();
            projectInfoList.addAll(newList);
        }

        return projectInfoList;
    }



    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {

        String title = "预算与支出对比表(" + pageData.getString("belongingMonthStr") + ")";
        String[] head = {"科  目", "预算金额(万元)", "科  目", "预算金额(万元)", "实际支出金额(万元)", "支出/预算比例"};

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(title);
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setWrapCell(wb, sheet);// 单元格
        HSSFCellStyle setCellLeft = ExcelUtil.setCellLeft(wb, sheet);// 单元格
        HSSFCellStyle setCellLeftIndentionOne = ExcelUtil.setCellLeftIndentionOne(wb, sheet);// 单元格
        HSSFCellStyle setCellLeftIndentionTwo = ExcelUtil.setCellLeftIndentionTwo(wb, sheet);// 单元格
        // 创建标题
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, 5);

        // 创建第一行
        rows = sheet.createRow(1);
        cells = rows.createCell(3);
        cells.setCellValue("经费来源预算");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 1, 1, 0, 2);

        cells = rows.createCell(0);
        cells.setCellValue("经费支出预算/实际支出");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 1, 1, 3, 5);

        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(2);
        HSSFCell hc;
        for (int j = 0; j < head.length; j++) {
            hc = rowTitle.createCell(j);
            hc.setCellValue(head[j]);
            hc.setCellStyle(styleCell);
        }


        List<PageData> pageDataList = queryList(pageData);
        if (!CollectionUtils.isEmpty(pageDataList)){
            for (int i = 0; i < pageDataList.size(); i++) {
                PageData data = pageDataList.get(i);
                int rowNumber = i + 3;
                rows = sheet.createRow(rowNumber);

                cells = rows.createCell(0);
                cells.setCellValue(data.getString("sourceAccount"));
                cells.setCellStyle(styleCell);

                cells = rows.createCell(1);
                cells.setCellValue(data.getString("sourceBudget"));
                cells.setCellStyle(styleCell);

                cells = rows.createCell(2);
                cells.setCellValue(data.getString("expenseAccount"));
                cells.setCellStyle(styleCell);

                cells = rows.createCell(3);
                cells.setCellValue(data.getString("expenseBudget"));
                cells.setCellStyle(styleCell);

                cells = rows.createCell(4);
                cells.setCellValue(data.getString("actualPay"));
                cells.setCellStyle(styleCell);

                cells = rows.createCell(5);
                cells.setCellValue(data.getString("payBudgetRatio"));
                cells.setCellStyle(styleCell);
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
}
