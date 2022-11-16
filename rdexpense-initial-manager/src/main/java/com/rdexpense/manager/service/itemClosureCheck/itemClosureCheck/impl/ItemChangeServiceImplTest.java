package com.rdexpense.manager.service.itemClosureCheck.itemClosureCheck.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.ConstantMsgUtil;
import com.rdexpense.RdexpenseApplication;
import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RdexpenseApplication.class)
@RunWith(SpringRunner.class)
class ItemChangeServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(ItemChangeServiceImpl.class);
    @Autowired
    @Resource(name = "baseDao")
    public BaseDao baseDao;

    @Test
    void updateCycleAndSurplusMonthsBudget() {
        PageData pd = new PageData();
        pd.put("projectApplyMainId","RDPIb0e04e63-3fb0-43d0-b64d-7ec200f4734b");
        pd.put("startYear","2020-01-01");
        pd.put("endYear","2023-01-01");
//        String projectApplyMainId = pd.getString("projectApplyMainId");
        //周期变更的开始年度，如果有已提交的支出费用后，则不能进行变更开始年度，只能变更结束年度，而结束年度不能小于二级科目支出费用的最大月份

        //计算周期变更的年度数量值
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        int yearsNumber = 0;
        int startYear = 0;
        try {
            startYear = Integer.valueOf(sdf.format(sdf.parse(pd.getString("startYear"))));

            startCalendar.setTime(sdf.parse(pd.getString("startYear")));
            endCalendar.setTime(sdf.parse(pd.getString("endYear")));

            System.out.println("startYear===="+pd.getString("startYear"));
            System.out.println("endYear===="+pd.getString("endYear"));
            yearsNumber = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR) + 1;

            System.out.println("yearsNumber===="+yearsNumber);
        } catch (Exception e){
            throw new MyException("所有月份的月份的数量allMonthNumber值计算异常", e);
        }

        //1、获取现有每月预算详情信息的年度值，根据周期变更的周期，按年度进行对比，
        //查询研发项目立项申请-经费预算-每月预算
        List<PageData> monthsList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthList", pd);
        //查询研发项目立项申请-经费预算-每月明细预算
        List<PageData> monthsDetailList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthDetailList", pd);
        //对每月明细预算，按年度进行分组
        List<PageData> yearsList = (List<PageData> ) baseDao.findForList("ItemChangeMapper.queryMonthDetailGroupByYearsList", pd);
        if (!CollectionUtils.isEmpty(yearsList)){
            if (yearsNumber > yearsList.size()){
                //如果yearsNumber>yearsList.size(),说明周期变更比原来的周期变长了，需要新增年度的月份明细项
                for (int i = 0; i < yearsNumber; i++) {
                    startYear ++;
                    boolean flag = false;
                    for (PageData pageData : yearsList) {
                        Integer years = Integer.valueOf(pageData.getString("years"));

                        if (startYear == years){
                            flag = true;
                        }
                    }

                    System.out.println("startYear============="+startYear);
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
                }
            } else {
                //如果yearsNumber<=yearsList.size(),说明周期变更比原来的周期变短了，需要删除年度的月份明细项
                for (int j = 0; j < yearsList.size(); j++) {
                    PageData pageData = yearsList.get(j);
                    Integer years = Integer.valueOf(pageData.getString("years"));
                    boolean flag = false;
                    for (int i = 0; i < yearsNumber; i++) {
                        startYear ++;
                        if (startYear == years){
                            flag = true;
                        }
                    }

                    if (!flag){
                        System.out.println("flag========"+flag);
                        //说明周期变更的周期变短了，需要删除年度的月份明细项
                        if (!CollectionUtils.isEmpty(monthsDetailList)){
                            for (int m = 0; m < monthsDetailList.size(); m++) {
                                PageData deletePageData = monthsDetailList.get(m);
                                Integer deleteYears = Integer.valueOf(deletePageData.getString("years"));
                                if (deleteYears.equals(years) ){
                                    monthsDetailList.remove(m);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 对现有每月预算详情信息进行增减，重新生成每月预算详情信息
//        String projectApplyMainId = pd.getString("projectApplyMainId");
//        baseDao.delete("ItemChangeMapper.deleteBudgetMonthDetail", projectApplyMainId);
//        logger.info("周期变更===先删除每月预算的详情,projectApplyMainId=[{}]", projectApplyMainId);
//        baseDao.batchInsert("ItemChangeMapper.batchInsertBudgetMonthDetail", monthsDetailList);
//        logger.info("周期变更===插入每月预算的详情成功");

//        updateSurplusMonthsBudget();
    }

    @Test
    void updateSurplusMonthsBudget() {

        PageData pd = new PageData();
        pd.put("projectApplyMainId","RDPIb0e04e63-3fb0-43d0-b64d-7ec200f4734b");
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
            } else {
                //是局级模板项目
                //查询是局级模板项目对应的经费支出预算字典类型的类型ID
                dicTypeList = (List<PageData>) baseDao.findForList("ItemExpensesMapper.queryDicTypeBureauList", null);
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

                for (PageData data : secondarySubjectCodeList) {
                    if (dic_enum_id.equals(data.getString("secondary_subject_code"))){
                        maxMonthsPD.put("dic_type_id",pageData.getString("dic_type_id"));
                        maxMonthsPD.put("belonging_month",data.getString("belongingMonth"));
                        maxMonthsPD.put("sumAmount",data.getString("sumAmount"));
                        maxMonthsList.add(maxMonthsPD);
                    }
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

            System.out.println("startYear===="+blPageData.getString("startYear"));
            System.out.println("endYear===="+blPageData.getString("endYear"));
            int differYears = (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12;
            System.out.println("differYears===="+differYears);
            int differMonth = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            System.out.println("differMonth===="+differMonth);
            allMonthNumber = differYears + differMonth;

            System.out.println("allMonthNumber===="+allMonthNumber);
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

                System.out.println("belongingMonth===="+belongingMonth);
                System.out.println("endYear===="+blPageData.getString("endYear"));
                Calendar maxMonthCalendar = Calendar.getInstance();
                try {
                    maxMonthCalendar.setTime(sdf.parse(belongingMonth));
                    int differMaxMonthYears = (endCalendar.get(Calendar.YEAR) - maxMonthCalendar.get(Calendar.YEAR)) * 12;
                    System.out.println("differMaxMonthYears===="+differMaxMonthYears);
                    int differMaxMonth = endCalendar.get(Calendar.MONTH) - maxMonthCalendar.get(Calendar.MONTH);
                    System.out.println("differMaxMonth===="+differMaxMonth);
                    toThisMonthNumber = differMaxMonthYears + differMaxMonth;
                    System.out.println("toThisMonthNumber===="+toThisMonthNumber);
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
                        if (new BigDecimal(data1.getString("toThisMonthNumber")).compareTo(BigDecimal.ZERO) != 0){
                            monthShare = expenseBudget.subtract(new BigDecimal(data1.getString("toThisMonthAllBudget")))
                                    .divide(surplusMonthNumber,2, RoundingMode.HALF_UP);;
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
                                if (StringUtil.isNotBlank(monthsStr) &&
                                        map.get("expenseAccountCode").equals(data.getString("dic_type_id")) &&
                                        monthFormat.parse(monthsStr).after(monthFormat.parse(data.getString("belonging_month")))) {
                                    map.put(key,data.getString("monthShare"));
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
                    if (dicEnumId.equals(detailData.getString("expenseAccountCode"))){
                        //计算需要合计的预算项下的所有预算项之和
                        january = january.add(new BigDecimal(detailData.getString("january")));
                        february = february.add(new BigDecimal(detailData.getString("february")));
                        march = march.add(new BigDecimal(detailData.getString("march")));
                        april = april.add(new BigDecimal(detailData.getString("april")));
                        may = may.add(new BigDecimal(detailData.getString("may")));
                        june = june.add(new BigDecimal(detailData.getString("june")));
                        july = july.add(new BigDecimal(detailData.getString("july")));
                        august = august.add(new BigDecimal(detailData.getString("august")));
                        september = september.add(new BigDecimal(detailData.getString("september")));
                        october = october.add(new BigDecimal(detailData.getString("october")));
                        november = november.add(new BigDecimal(detailData.getString("november")));
                        december = december.add(new BigDecimal(detailData.getString("december")));
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