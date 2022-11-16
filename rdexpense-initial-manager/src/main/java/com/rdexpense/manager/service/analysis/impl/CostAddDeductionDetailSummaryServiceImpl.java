package com.rdexpense.manager.service.analysis.impl;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.ConstantValUtil;
import com.common.util.ExcelUtil;
import com.rdexpense.manager.service.analysis.CostAddDeductionDetailSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class CostAddDeductionDetailSummaryServiceImpl implements CostAddDeductionDetailSummaryService {
    private static final Logger logger = LoggerFactory.getLogger(CostAddDeductionDetailSummaryServiceImpl.class);

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;


    /**
     * 查询列表
     *
     * @param
     * @return
     */
    @Override
    public List queryList(PageData pd) {

        List<PageData> resultList = new LinkedList<>();
        List<PageData> CostAddDeductionDetailList = new ArrayList<>();
        if(pd.getString("businessId").equals("0")){
            CostAddDeductionDetailList = (List<PageData>) baseDao.findForList("CostAddDeductionDetailMapper.queryAllList", pd);
        } else {
            CostAddDeductionDetailList = (List<PageData>) baseDao.findForList("CostAddDeductionDetailMapper.queryList", pd);
        }

        try {
            queryCostAddDeduction(resultList,pd,CostAddDeductionDetailList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    /**
     * 查询研发项目费用增减扣除明细表
     * @param pd
     */
    public void queryCostAddDeduction(List<PageData> resultList, PageData pd,List<PageData> CostAddDeductionDetailList){
        pd.put("projectApplyMainId",pd.getString("businessId"));
        String projectApplyMainId = pd.getString("projectApplyMainId");

        DecimalFormat df = new DecimalFormat("0.00");

        //本年度的立项数，获取已审批完成研发立项项目
        PageData  projectApplySum= (PageData) baseDao.findForObject("CostAddDeductionDetailMapper.queryProjectApplySum", pd);
        projectApplySum.put("expenseAccountCode","1");
        projectApplySum.put("expenseAccount","本年可享受研发费用加计扣除项目数量");
        projectApplySum.put("expenseCost",projectApplySum.getString("projectSum"));
        resultList.add(projectApplySum);

        //一、自主研发、合作研发、集中研发
        PageData firstTheme = new PageData();
        firstTheme.put("expenseAccountCode","2");
        firstTheme.put("expenseAccount","一、自主研发、合作研发、集中研发");
        firstTheme.put("expenseCost","");
        resultList.add(firstTheme);

        //根据projectApplyMainId,找出费用支出已审批完成的各个二级科目的支出总金额
        List<PageData> secondarySubjectCodeList = new ArrayList<>();
        if (pd.getString("businessId").equals("0")){
            //查询全部
            secondarySubjectCodeList = (List<PageData> ) baseDao.findForList("CostAddDeductionDetailMapper.querySecondarySubjectCodeMonthsAllList", pd);
        } else {
            secondarySubjectCodeList = (List<PageData> ) baseDao.findForList("CostAddDeductionDetailMapper.querySecondarySubjectCodeMonthsList", pd);
        }

        //查找所有的一级科目
        List<PageData> dicTypeList = (List<PageData>) baseDao.findForList("CostAddDeductionDetailMapper.queryDicTypeList", null);
        //根据一级科目,查找所有的二级科目,
        List<PageData> dicTypeIdList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.querySubToDicTypeList", dicTypeList);
        if ( !CollectionUtils.isEmpty(dicTypeList)){
            //遍历对应的经费预算项的字典类型ID
            for (PageData pageData : dicTypeList) {
                PageData dicTypePD = new PageData();
                String dic_type_id = pageData.getString("dic_type_id");

                if (!dic_type_id.equals("102217")){
                    if (dic_type_id.equals("102211")){
                        dicTypePD.put("expenseAccount","  （一）人员人工费用");
                    } else if (dic_type_id.equals("102212")){
                        dicTypePD.put("expenseAccount","  （二）直接投入费用");
                    } else if (dic_type_id.equals("102213")){
                        dicTypePD.put("expenseAccount","  （三）折旧费用");
                    } else if (dic_type_id.equals("102214")){
                        dicTypePD.put("expenseAccount","  （四）无形资产摊销");
                    } else if (dic_type_id.equals("102215")){
                        dicTypePD.put("expenseAccount","  （五）新产品设计费等");
                    } else if (dic_type_id.equals("102216")){
                        dicTypePD.put("expenseAccount","  （六）其他相关费用");
                    }

                    dicTypePD.put("expenseAccountCode",pageData.getString("dic_type_id"));
                    dicTypePD.put("expenseCost",new BigDecimal("0.00"));
                    resultList.add(dicTypePD);

                    queryDicTypeExpenseCost(resultList,dicTypePD,dicTypeIdList,secondarySubjectCodeList,dic_type_id);

                    //在其它相关费用之后添加（七）经限额调整后的其他相关费用
                    if (dic_type_id.equals("102216")){
                        PageData  seventhPageData= new PageData();
                        seventhPageData.put("expenseAccountCode","3");
                        seventhPageData.put("expenseAccount","  （七）经限额调整后的其他相关费用");
                        seventhPageData.put("expenseCost",new BigDecimal("0.00"));
                        resultList.add(seventhPageData);
                    }
                } else {
                    //二、委托研发
                    PageData twoTheme = new PageData();
                    twoTheme.put("expenseAccountCode",pageData.getString("dic_type_id"));
                    twoTheme.put("expenseAccount","二、委托研发");
                    twoTheme.put("expenseCost",new BigDecimal("0.00"));
                    resultList.add(twoTheme);

                    queryDicTypeExpenseCost(resultList,twoTheme,dicTypeIdList,secondarySubjectCodeList,dic_type_id);
                }
            }

            //计算resultList集合里，需要合并的项
            List<PageData> tmpList = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                PageData pageData = resultList.get(i);
                //设备租赁费和仪器租赁费之和
                BigDecimal expenseCost1 = new BigDecimal("0.00");
                if (pageData.getString("expenseAccountCode").equals("TRFY0008")){
                    for (PageData data : resultList) {
                        if (!data.getString("expenseCost").isEmpty() && data.getString("expenseAccountCode").equals("TRFY0008") ||
                                data.getString("expenseAccountCode").equals("TRFY0009")){
                            expenseCost1 = expenseCost1.add(new BigDecimal(data.getString("expenseCost")));
                        }
                    }
                    pageData.put("expenseCost",expenseCost1);
                }
                //去掉重复的项
                if (pageData.getString("expenseAccountCode").equals("TRFY0009")){
                    tmpList.add(pageData);
                }

                //技术图书资料费、资料翻译费、专家咨询费、高新科技研发保险费之和
                BigDecimal expenseCost2 = new BigDecimal("0.00");
                if (pageData.getString("expenseAccountCode").equals("QTXGFY0001")){
                    for (PageData data : resultList) {
                        if (!data.getString("expenseCost").isEmpty() && data.getString("expenseAccountCode").equals("QTXGFY0001") ||
                                data.getString("expenseAccountCode").equals("QTXGFY0002") ||
                                data.getString("expenseAccountCode").equals("QTXGFY0003") ||
                                data.getString("expenseAccountCode").equals("QTXGFY0004")){
                            expenseCost2 = expenseCost2.add(new BigDecimal(data.getString("expenseCost")));
                        }
                    }
                    pageData.put("expenseCost",expenseCost2);
                }
                //添加到临时集合，去掉重复的项
                if (pageData.getString("expenseAccountCode").equals("QTXGFY0002") ||
                        pageData.getString("expenseAccountCode").equals("QTXGFY0003") ||
                        pageData.getString("expenseAccountCode").equals("QTXGFY0004")){
                    tmpList.add(pageData);
                }

                //知识产权申请费、知识产权注册费、知识产权代理费之和
                BigDecimal expenseCost3 = new BigDecimal("0.00");
                if (pageData.getString("expenseAccountCode").equals("QTXGFY0006")){
                    for (PageData data : resultList) {
                        if (!data.getString("expenseCost").isEmpty() && data.getString("expenseAccountCode").equals("QTXGFY0006") ||
                                data.getString("expenseAccountCode").equals("QTXGFY0007") ||
                                data.getString("expenseAccountCode").equals("QTXGFY0008")){
                            expenseCost3 = expenseCost3.add(new BigDecimal(data.getString("expenseCost")));
                        }
                    }
                    pageData.put("expenseCost",expenseCost3);
                }
                //添加到临时集合，去掉重复的项
                if (pageData.getString("expenseAccountCode").equals("QTXGFY0007") ||
                        pageData.getString("expenseAccountCode").equals("QTXGFY0008")){
                    tmpList.add(pageData);
                }

                //职工福利费、补充养老保险费、补充医疗保险费之和
                BigDecimal expenseCost4 = new BigDecimal("0.00");
                if (pageData.getString("expenseAccountCode").equals("QTXGFY0009")){
                    for (PageData data : resultList) {
                        if (!data.getString("expenseCost").isEmpty() && data.getString("expenseAccountCode").equals("QTXGFY0009") ||
                                data.getString("expenseAccountCode").equals("QTXGFY00010") ||
                                data.getString("expenseAccountCode").equals("QTXGFY00011")){
                            expenseCost4 = expenseCost4.add(new BigDecimal(data.getString("expenseCost")));
                        }
                    }
                    pageData.put("expenseCost",expenseCost4);
                }
                //添加到临时集合，去掉重复的项
                if (pageData.getString("expenseAccountCode").equals("QTXGFY00010") ||
                        pageData.getString("expenseAccountCode").equals("QTXGFY00011")){
                    tmpList.add(pageData);
                }

                //差旅费、会议费之和
                BigDecimal expenseCost5 = new BigDecimal("0.00");
                if (pageData.getString("expenseAccountCode").equals("QTXGFY00012")){
                    for (PageData data : resultList) {
                        if (!data.getString("expenseCost").isEmpty() && data.getString("expenseAccountCode").equals("QTXGFY00012") ||
                                data.getString("expenseAccountCode").equals("QTXGFY00013")){
                            expenseCost5 = expenseCost5.add(new BigDecimal(data.getString("expenseCost")));
                        }
                    }
                    pageData.put("expenseCost",expenseCost5);
                }
                //添加到临时集合，去掉重复的项
                if (pageData.getString("expenseAccountCode").equals("QTXGFY00013")){
                    tmpList.add(pageData);
                }
            }

            //去掉重复的项
            for (PageData pageData : tmpList) {
                for (int i = 0; i < resultList.size(); i++) {
                    PageData data = resultList.get(i);
                    if (pageData.getString("expenseAccountCode").equals(data.getString("expenseAccountCode"))){
                        resultList.remove(i);
                    }
                }
            }

            //计算（七）经限额调整后的其他相关费用
            //（六）合计数（28）与[（一~五（3+7+16+19+23））×10％]/(1-10％)孰小值填写,结果取2位效数，谁小就显示谁
            if(!CollectionUtils.isEmpty(resultList)){
                BigDecimal expenseCost = new BigDecimal("0.00");
                BigDecimal otherExpenseCost = new BigDecimal("0.00");
                for (PageData pageData : resultList) {
                    if (pageData.getString("expenseAccountCode").equals("102216")){
                        expenseCost = new BigDecimal(pageData.getString("expenseCost"));
                    }

                    if (pageData.getString("expenseAccountCode").equals("102211") ||
                            pageData.getString("expenseAccountCode").equals("102212") ||
                            pageData.getString("expenseAccountCode").equals("102213") ||
                            pageData.getString("expenseAccountCode").equals("102214") ||
                            pageData.getString("expenseAccountCode").equals("102215")){
                        otherExpenseCost = otherExpenseCost.add(new BigDecimal(pageData.getString("expenseCost")));
                    }
                }

                otherExpenseCost = otherExpenseCost.multiply(new BigDecimal("0.1")).divide(new BigDecimal("0.9"),2,RoundingMode.HALF_UP);

                if (expenseCost.compareTo(otherExpenseCost) > 0){
                    for (PageData pageData : resultList) {
                        if (pageData.getString("expenseAccountCode").equals("3")){
                            pageData.put("expenseCost",otherExpenseCost);
                        }
                    }
                }
            }

            //计算一、自主研发、合作研发、集中研发
            if(!CollectionUtils.isEmpty(resultList)) {
                BigDecimal expenseCost2 = new BigDecimal("0.00");
                for (PageData pageData : resultList) {
                    if (pageData.getString("expenseAccountCode").equals("102211") ||
                            pageData.getString("expenseAccountCode").equals("102212") ||
                            pageData.getString("expenseAccountCode").equals("102213") ||
                            pageData.getString("expenseAccountCode").equals("102214") ||
                            pageData.getString("expenseAccountCode").equals("102215") ||
                            pageData.getString("expenseAccountCode").equals("3")){
                        expenseCost2 = expenseCost2.add(new BigDecimal(pageData.getString("expenseCost")));
                    }
                }

                for (PageData pageData : resultList) {
                    if (pageData.getString("expenseAccountCode").equals("2")){
                        pageData.put("expenseCost",expenseCost2);
                    }
                }
            }

            //三、年度研发费用小计
            BigDecimal expenseCost4 = new BigDecimal("0.00");
            PageData  pageData1= new PageData();
            pageData1.put("expenseAccountCode","4");
            pageData1.put("expenseAccount","三、年度研发费用小计");
            if(!CollectionUtils.isEmpty(resultList)) {
                for (PageData pageData : resultList) {
                    if (pageData.getString("expenseAccountCode").equals("2") ||
                            pageData.getString("expenseAccountCode").equals("WTYFFY0002")){
                        expenseCost4 =  expenseCost4.add(new BigDecimal(pageData.getString("expenseCost")));
                    }

                    if (pageData.getString("expenseAccountCode").equals("WTYFFY0001")){
                        expenseCost4 =  expenseCost4.add(new BigDecimal(pageData.getString("expenseCost"))
                                .multiply(new BigDecimal("0.8")));
                    }
                }
            }
            pageData1.put("expenseCost",df.format(expenseCost4));
            resultList.add(pageData1);
            //（一）本年费用化金额
            PageData  pageData2= new PageData();
            pageData2.put("expenseAccountCode","5");
            pageData2.put("expenseAccount","  （一）本年费用化金额");
            pageData2.put("expenseCost",df.format(expenseCost4));
            resultList.add(pageData2);
            //（二）本年资本化金额
            PageData  pageData3= new PageData();
            pageData3.put("expenseAccountCode","6");
            pageData3.put("expenseAccount","  （二）本年资本化金额");
            pageData3.put("expenseCost",new BigDecimal("0.00"));
            resultList.add(pageData3);
            //四、本年形成无形资产摊销额
            PageData  pageData4= new PageData();
            pageData4.put("expenseAccountCode","7");
            pageData4.put("expenseAccount","四、本年形成无形资产摊销额");
            pageData4.put("expenseCost",new BigDecimal("0.00"));
            resultList.add(pageData4);
            //五、以前年度形成无形资产本年摊销额
            PageData  pageData5= new PageData();
            pageData5.put("expenseAccountCode","8");
            pageData5.put("expenseAccount","五、以前年度形成无形资产本年摊销额");
            pageData5.put("expenseCost",new BigDecimal("0.00"));
            resultList.add(pageData5);
            //六、允许扣除的研发费用合计
            BigDecimal expenseCost9 = new BigDecimal("0.00");
            PageData  pageData6= new PageData();
            pageData6.put("expenseAccountCode","9");
            pageData6.put("expenseAccount","六、允许扣除的研发费用合计");
            if(!CollectionUtils.isEmpty(resultList)) {
                for (PageData pageData : resultList) {
                    if (pageData.getString("expenseAccountCode").equals("5") ||
                            pageData.getString("expenseAccountCode").equals("7") ||
                            pageData.getString("expenseAccountCode").equals("8")){
                        expenseCost9 =  expenseCost9.add(new BigDecimal(pageData.getString("expenseCost")));
                    }
                }
            }
            pageData6.put("expenseCost",df.format(expenseCost9));
            resultList.add(pageData6);
            //减：特殊收入部分
            BigDecimal expenseCost10 = new BigDecimal("0.00");
            BigDecimal expenseCost12 = new BigDecimal("0.00");
            BigDecimal expenseCost13 = new BigDecimal("0.00");
            PageData  pageData7= new PageData();
            pageData7.put("expenseAccountCode","10");
            pageData7.put("expenseAccount","  减：特殊收入部分");
            //当选择条件包含已经填写信息的某个项目、某个月份，把expenseCost值相加
            if (!CollectionUtils.isEmpty(CostAddDeductionDetailList)){
                for (PageData pageData : CostAddDeductionDetailList) {
                    if (pageData.getString("expenseAccountCode").equals("10")){
                        expenseCost10 = expenseCost10.add(new BigDecimal(pageData.getString("expenseCost")));
                    }
                    if (pageData.getString("expenseAccountCode").equals("12")){
                        expenseCost12 = expenseCost12.add(new BigDecimal(pageData.getString("expenseCost")));
                    }
                    if (pageData.getString("expenseAccountCode").equals("13")){
                        expenseCost13 = expenseCost13.add(new BigDecimal(pageData.getString("expenseCost")));
                    }
                }
            }
            pageData7.put("expenseCost",df.format(expenseCost10));
            resultList.add(pageData7);
            //七、允许扣除的研发费用抵减特殊收入后的金额
            BigDecimal expenseCost11 = new BigDecimal("0.00");
            expenseCost11 = expenseCost9.subtract(expenseCost10);
            PageData  pageData8= new PageData();
            pageData8.put("expenseAccountCode","11");
            pageData8.put("expenseAccount","七、允许扣除的研发费用抵减特殊收入后的金额");
            pageData8.put("expenseCost",df.format(expenseCost11));
            resultList.add(pageData8);
            //减：当年销售研发活动直接形成产品（包括组成部分）对应的材料部分
            PageData  pageData9= new PageData();
            pageData9.put("expenseAccountCode","12");
            pageData9.put("expenseAccount","  减：当年销售研发活动直接形成产品（包括组成部分）对应的材料部分");
            pageData9.put("expenseCost",df.format(expenseCost12));
            resultList.add(pageData9);
            //减：以前年度销售研发活动直接形成产品（包括组成部分）对应材料部分结转金额
            PageData  pageData10= new PageData();
            pageData10.put("expenseAccountCode","13");
            pageData10.put("expenseAccount","  减：以前年度销售研发活动直接形成产品（包括组成部分）对应材料部分结转金额");
            pageData10.put("expenseCost",df.format(expenseCost13));
            resultList.add(pageData10);
            //八、加计扣除比例
            PageData  pageData11= new PageData();
            pageData11.put("expenseAccountCode","14");
            pageData11.put("expenseAccount","八、加计扣除比例");
            pageData11.put("expenseCost","75%");
            resultList.add(pageData11);
            //九、本年研发费用加计扣除总额
            BigDecimal expenseCost15 = new BigDecimal("0.00");
            expenseCost15 = expenseCost11.subtract(expenseCost12).subtract(expenseCost13);
            PageData  pageData12= new PageData();
            pageData12.put("expenseAccountCode","15");
            pageData12.put("expenseAccount","九、本年研发费用加计扣除总额");
            pageData12.put("expenseCost",df.format(expenseCost15.multiply(new BigDecimal("50"))));
            resultList.add(pageData12);
            //十、销售研发活动直接形成产品（包括组成部分）对应材料部分结转以后年度扣减金额
            //（当47-48-49≥0，本行＝0；当47-48-49＜0，本行＝47-48-49的绝对值)
            BigDecimal expenseCost16 = new BigDecimal("0.00");
            if (expenseCost15.compareTo(BigDecimal.ZERO) < 0){
                expenseCost16 = expenseCost15.abs();
            }
            PageData  pageData13= new PageData();
            pageData13.put("expenseAccountCode","16");
            pageData13.put("expenseAccount","十、销售研发活动直接形成产品（包括组成部分）对应材料部分结转以后年度扣减金额");
            pageData13.put("expenseCost",expenseCost16);
            resultList.add(pageData13);
        }
    }

    private void queryDicTypeExpenseCost(List<PageData> resultList,PageData dicTypePD, List<PageData> dicTypeIdList, List<PageData> secondarySubjectCodeList, String dic_type_id) {
        BigDecimal expenseCost = new BigDecimal("0.00");
        BigDecimal expenseCostSum = new BigDecimal("0.00");
        for (PageData pageData : dicTypeIdList) {
            String dic_enum_id = pageData.getString("dic_enum_id");

            if (dic_type_id.equals(pageData.getString("dic_type_id"))){
                PageData  dicEnumPageData= new PageData();
                dicEnumPageData.put("expenseAccountCode",dic_enum_id);
                dicEnumPageData.put("expenseAccount","     "+pageData.getString("remark"));
                dicEnumPageData.put("expenseCost",expenseCost);

                for (PageData data : secondarySubjectCodeList) {
                    if (dic_enum_id.equals(data.getString("secondary_subject_code"))){
                        if (!data.getString("sumAmount").isEmpty()){
                            expenseCostSum = expenseCostSum.add(new BigDecimal(data.getString("sumAmount")));
                            dicEnumPageData.put("expenseCost",data.getString("sumAmount"));
                        }
                    }
                }
                resultList.add(dicEnumPageData);

                if (dic_enum_id.equals("WTYFFY0002")){
                    PageData  tmpPageData= new PageData();
                    tmpPageData.put("expenseAccountCode","17");
                    tmpPageData.put("expenseAccount","   其中：允许加计扣除的委托境外机构进行研发活动发生的费用");
                    tmpPageData.put("expenseCost",dicEnumPageData.getString("expenseCost"));
                    resultList.add(tmpPageData);
                }
            }
        }
        dicTypePD.put("expenseCost",expenseCostSum);
    }


    /**
     * 查询研发费用支出已审批完成的研发项目申请列表
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryProjectApplyList(PageData pd) {
        pd.put("processStatus", ConstantValUtil.APPROVAL_STATUS[4]);
        List<String> projectTypeCodeList = JSONArray.parseArray(pd.getString("projectTypeCode"), String.class);
        pd.put("projectTypeCodeList", projectTypeCodeList);
        List<String> professionalCategoryCodeList = JSONArray.parseArray(pd.getString("professionalCategoryCode"), String.class);
        pd.put("professionalCategoryCodeList", professionalCategoryCodeList);
        List<PageData> pageDataList = (List<PageData>) baseDao.findForList("CostAddDeductionDetailMapper.queryExpensesProjectApplyList", pd);
        return pageDataList;
    }

    /**
     * 查询研发费用支出已审批完成的最大月份值
     * @return
     */
    @Override
    public PageData queryExpensesMaxMonths(PageData pd) {
        PageData pageData = (PageData) baseDao.findForObject("CostAddDeductionDetailMapper.queryExpensesMaxMonths",pd);
        return pageData;
    }

    /**
     * 编辑其他事项
     * @param pd
     */
    @Override
    @Transactional
    public void update(PageData pd) {
        List<PageData> pageDataList = JSONArray.parseArray(pd.getString("costAddDeductionDetailList"), PageData.class);
        pd.put("expenseAccountCode",pageDataList.get(0).getString("expenseAccountCode"));

        if (!CollectionUtils.isEmpty(pageDataList)){
            for (PageData pageData : pageDataList) {
                pageData.put("businessId",pd.getString("businessId"));
                pageData.put("projectName",pd.getString("projectName"));
                pageData.put("createUserId",pd.getString("createUserId"));
                pageData.put("createUser",pd.getString("createUser"));
                pageData.put("startMonth",pd.getString("startMonth"));
                pageData.put("endMonth",pd.getString("endMonth"));
                pageData.put("creatorOrgId",pd.getString("creatorOrgId"));
                pageData.put("creatorOrgName",pd.getString("creatorOrgName"));
            }
        }
        //判读是否存在已编辑的数据，如果存在，则更新，如果不存在则插入
        List<PageData> CostAddDeductionDetailList = (List<PageData>) baseDao.findForList("CostAddDeductionDetailMapper.queryOneList", pd);
        if (CollectionUtils.isEmpty(CostAddDeductionDetailList)){
            //插入
            baseDao.batchInsert("CostAddDeductionDetailMapper.batchInsertDetail", pageDataList);
        } else {
            //编辑
            baseDao.update("CostAddDeductionDetailMapper.updateDetail", pageDataList);
        }

    }



    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {

        String title = "研发费用加减扣除明细表(" + pageData.getString("belongingMonthStr") + ")";
        String[] head = {"序号", "项  目", "金额（数量）"};

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
        ExcelUtil.merge(wb, sheet, 0, 0, 0, 2);
        // 第一行  表头
        HSSFRow rowTitle = sheet.createRow(1);
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
                int rowNumber = i + 2;
                rows = sheet.createRow(rowNumber);

                cells = rows.createCell(0);
                cells.setCellValue(i+1);
                cells.setCellStyle(styleCell);

                //缩进1格
                if (data.getString("expenseAccountCode").equals("102211") ||
                        data.getString("expenseAccountCode").equals("102212") ||
                        data.getString("expenseAccountCode").equals("102213") ||
                        data.getString("expenseAccountCode").equals("102214") ||
                        data.getString("expenseAccountCode").equals("102215") ||
                        data.getString("expenseAccountCode").equals("102216") ||
                        data.getString("expenseAccountCode").equals("3") ||
                        data.getString("expenseAccountCode").equals("WTYFFY0001") ||
                        data.getString("expenseAccountCode").equals("WTYFFY0002") ||
                        data.getString("expenseAccountCode").equals("WTYFFY0003") ||
                        data.getString("expenseAccountCode").equals("5") ||
                        data.getString("expenseAccountCode").equals("6") ||
                        data.getString("expenseAccountCode").equals("10") ||
                        data.getString("expenseAccountCode").equals("12") ||
                        data.getString("expenseAccountCode").equals("13")){

                    cells = rows.createCell(1);
                    cells.setCellValue(data.getString("expenseAccount"));
                    cells.setCellStyle(setCellLeftIndentionOne);
                } else if (data.getString("expenseAccountCode").equals("1") ||
                        data.getString("expenseAccountCode").equals("2") ||
                        data.getString("expenseAccountCode").equals("102217") ||
                        data.getString("expenseAccountCode").equals("4") ||
                        data.getString("expenseAccountCode").equals("7") ||
                        data.getString("expenseAccountCode").equals("8") ||
                        data.getString("expenseAccountCode").equals("9") ||
                        data.getString("expenseAccountCode").equals("11") ||
                        data.getString("expenseAccountCode").equals("14") ||
                        data.getString("expenseAccountCode").equals("15") ||
                        data.getString("expenseAccountCode").equals("16")){
                    //不缩进
                    cells = rows.createCell(1);
                    cells.setCellValue(data.getString("expenseAccount"));
                    cells.setCellStyle(setCellLeft);
                } else {
                    //缩进2格
                    cells = rows.createCell(1);
                    cells.setCellValue(data.getString("expenseAccount"));
                    cells.setCellStyle(setCellLeftIndentionTwo);
                }


                cells = rows.createCell(2);
                cells.setCellValue(data.getString("expenseCost"));
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
