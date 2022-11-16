package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.ConstantMsgUtil;
import com.rdexpense.RdexpenseApplication;
import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
//需要注入DAO层时，需要这两个注释
//@SpringBootTest(classes = RdexpenseApplication.class)
//@RunWith(SpringRunner.class)
class ItemExpensesServiceImplTest {

    @Autowired
    @Resource(name = "baseDao")
    public BaseDao baseDao;

    @Test
    void queryBudgetExcessTips() {
        PageData result = new PageData();
        PageData pd = new PageData();
        pd.put("projectApplyMainId","RDPIfa7d1657-c5e7-4247-bca2-d149e406e513");
        pd.put("secondarySubjectCode","十一、委托研发费用");
        pd.put("belongingMonth","2022-01");
        pd.put("accumulatedExpenditure","200");
        pd.put("amount","100");


        String[] monthsArr = {"january","february","march","april","may","june","july","august","september","october","november","december"};
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");//设置日期格式
        DecimalFormat df = new DecimalFormat("0.00");
        //已累计支出
        BigDecimal accumulatedExpenditure = new BigDecimal(pd.getString("accumulatedExpenditure"));
        //本次金额
        BigDecimal amount = new BigDecimal(pd.getString("amount"));
        //截至当前单据所属月份的预算累计
        BigDecimal toThisMonthAllBudget = new BigDecimal("0.00");

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
                    if (pageData.getString("months").equals(monthsArr[i]) && i <= 9){
                        monthsStr = pageData.getString("years") + "-0" + m;
                    } else if (pageData.getString("months").equals(monthsArr[i]) && i > 9) {
                        monthsStr = pageData.getString("years") + "-" + m;
                    }
                    System.out.println("monthsStr======"+monthsStr);
                    break;
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
                System.out.println("toThisMonthAllBudget==111111111===="+toThisMonthAllBudget);
            }
        }
        System.out.println("toThisMonthAllBudget===22222222222==="+toThisMonthAllBudget);

        //计算公式：该项目该类预算累计支付+本次金额-截至当前单据所属月份的预算累计）/截至当前单据所属月份的预算累计*100
        if (toThisMonthAllBudget.compareTo(BigDecimal.ZERO) != 0){
            BigDecimal amount1 = accumulatedExpenditure.add(amount).subtract(toThisMonthAllBudget).divide(toThisMonthAllBudget).multiply(new BigDecimal("100"));
            System.out.println("amount1======"+amount1);
            if (amount1.compareTo(new BigDecimal(5)) > 0){
                System.out.println("tips======累计支出总额已经超出该类预算5%，是否继续提交");
                result.put("tips","累计支出总额已经超出该类预算5%，是否继续提交");
            } else {
                result.put("tips","");
            }
        } else {
            result.put("tips","");
        }
    }
}