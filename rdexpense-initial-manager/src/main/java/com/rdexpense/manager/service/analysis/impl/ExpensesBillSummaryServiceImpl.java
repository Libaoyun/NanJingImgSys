package com.rdexpense.manager.service.analysis.impl;

import com.common.base.dao.mysql.BaseDao;
import com.common.entity.PageData;
import com.common.util.*;
import com.rdexpense.manager.service.analysis.ExpensesBillSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:11
 */
@Slf4j
@Service
public class ExpensesBillSummaryServiceImpl implements ExpensesBillSummaryService {


    @Autowired
    @Resource(name = "baseDao")
    private BaseDao dao;


    /**
     * 查询列表
     *
     * @param
     * @return
     */
    @Override
    public List queryList(PageData pd) {

        List<PageData> resultList = new LinkedList<>();

        //费用化小计
        BigDecimal userAmountFy = new BigDecimal("0.00");//人员人工费用
        BigDecimal inputAmountFy = new BigDecimal("0.00");//直接投入费用
        BigDecimal depreciationAmountFy = new BigDecimal("0.00");//折旧费用
        BigDecimal amortizationAmountFy = new BigDecimal("0.00");//无形资产摊销
        BigDecimal desginAmountFy = new BigDecimal("0.00");//新产品设计费等
        BigDecimal itemSumAmountFy = new BigDecimal("0.00");//前五项小计
        BigDecimal otherAmountFy = new BigDecimal("0.00");//其他相关费用合计
        BigDecimal territoryAmountFy = new BigDecimal("0.00");//境内活动费
        BigDecimal adjustTerritoryAmountFy = new BigDecimal("0.00");//允许加计后的境内活动费
        BigDecimal abroadAmountFy = new BigDecimal("0.00");//境外活动费


        //资本化小计
        BigDecimal userAmountZb = new BigDecimal("0.00");//人员人工费用
        BigDecimal inputAmountZb = new BigDecimal("0.00");//直接投入费用
        BigDecimal depreciationAmountZb = new BigDecimal("0.00");//折旧费用
        BigDecimal amortizationAmountZb = new BigDecimal("0.00");//无形资产摊销
        BigDecimal desginAmountZb = new BigDecimal("0.00");//新产品设计费等
        BigDecimal itemSumAmountZb = new BigDecimal("0.00");//前五项小计
        BigDecimal otherAmountZb = new BigDecimal("0.00");//其他相关费用合计
        BigDecimal territoryAmountZb = new BigDecimal("0.00");//境内活动费
        BigDecimal adjustTerritoryAmountZb = new BigDecimal("0.00");//允许加计后的境内活动费
        BigDecimal abroadAmountZb = new BigDecimal("0.00");//境外活动费




        //查询辅助账的主表信息
        List<PageData> list = (List<PageData>) dao.findForList("ExpensesBillSummaryMapper.queryProjectData", pd);

        //查询金额的合计
        if(!CollectionUtils.isEmpty(list)){
            List<PageData> amountList = (List<PageData>) dao.findForList("ExpensesBillSummaryMapper.queryAmountList",list);

            //遍历项目
            for(PageData project : list){

                String expensesTypeCode = project.getString("expensesTypeCode");
                String businessId = project.getString("businessId");

                PageData data = new PageData();
                data.put("projectCode",project.getString("projectCode"));//项目编码
                data.put("projectName",project.getString("projectName"));//项目名称
                data.put("completionStatus",project.getString("completionStatus"));//完成情况
                data.put("expensesType",project.getString("expensesType"));//支出类型
                data.put("expensesTypeCode",project.getString("expensesTypeCode"));//支出类型编码

                if(!CollectionUtils.isEmpty(amountList)){
                    for(PageData amount : amountList){
                        if(businessId.equals(amount.getString("businessId"))){
                            String userAmount = amount.getString("userAmount");//人员人工费用
                            String inputAmount = amount.getString("inputAmount");//直接投入费用
                            String depreciationAmount = amount.getString("depreciationAmount");//折旧费用
                            String amortizationAmount = amount.getString("amortizationAmount");//无形资产摊销
                            String desginAmount = amount.getString("desginAmount");//新产品设计费等

                            //前五项小计
                            BigDecimal itemSumAmount = new BigDecimal(userAmount).add(new BigDecimal(inputAmount))
                                    .add(new BigDecimal(depreciationAmount)).add(new BigDecimal(amortizationAmount)).add(new BigDecimal(desginAmount));

                            String otherAmount = amount.getString("otherAmount");//其他相关费用合计
                            String territoryAmount = amount.getString("territoryAmount");//境内活动费
                            String abroadAmount = amount.getString("abroadAmount");//境外活动费

                            //允许加计后的境内活动费 = 境内活动费 * 80%
                            BigDecimal adjustTerritoryAmount = new BigDecimal(territoryAmount).multiply(new BigDecimal(0.8)).setScale(2,BigDecimal.ROUND_HALF_UP);

                            if(expensesTypeCode.equals("DICT10280001")){//费用化
                                userAmountFy = userAmountFy.add(new BigDecimal(userAmount));
                                inputAmountFy = inputAmountFy.add(new BigDecimal(inputAmount));
                                depreciationAmountFy = depreciationAmountFy.add(new BigDecimal(depreciationAmount));
                                amortizationAmountFy = amortizationAmountFy.add(new BigDecimal(amortizationAmount));
                                desginAmountFy = desginAmountFy.add(new BigDecimal(desginAmount));
                                itemSumAmountFy = itemSumAmountFy.add(itemSumAmount);
                                otherAmountFy = otherAmountFy.add(new BigDecimal(otherAmount));
                                territoryAmountFy = territoryAmountFy.add(new BigDecimal(territoryAmount));
                                adjustTerritoryAmountFy = adjustTerritoryAmountFy.add(adjustTerritoryAmount);
                                abroadAmountFy = abroadAmountFy.add(new BigDecimal(abroadAmount));

                            }else if(expensesTypeCode.equals("DICT10280002")){//资本化
                                userAmountZb = userAmountZb.add(new BigDecimal(userAmount));
                                inputAmountZb = inputAmountZb.add(new BigDecimal(inputAmount));
                                depreciationAmountZb = depreciationAmountZb.add(new BigDecimal(depreciationAmount));
                                amortizationAmountZb = amortizationAmountZb.add(new BigDecimal(amortizationAmount));
                                desginAmountZb = desginAmountZb.add(new BigDecimal(desginAmount));
                                itemSumAmountZb = itemSumAmountZb.add(itemSumAmount);
                                otherAmountZb = otherAmountZb.add(new BigDecimal(otherAmount));
                                territoryAmountZb = territoryAmountZb.add(new BigDecimal(territoryAmount));
                                adjustTerritoryAmountZb = adjustTerritoryAmountZb.add(adjustTerritoryAmount);
                                abroadAmountZb = abroadAmountZb.add(new BigDecimal(abroadAmount));
                            }

                            data.put("userAmount",userAmount);
                            data.put("inputAmount",inputAmount);
                            data.put("depreciationAmount",depreciationAmount);
                            data.put("amortizationAmount",amortizationAmount);
                            data.put("desginAmount",desginAmount);
                            data.put("itemSumAmount",itemSumAmount);
                            data.put("otherAmount",otherAmount);
                            data.put("territoryAmount",territoryAmount);
                            data.put("adjustTerritoryAmount",adjustTerritoryAmount);
                            data.put("abroadAmount",abroadAmount);


                            resultList.add(data);
                        }
                    }
                }
            }

        }



        //合计金额=资本化合计+费用化合计
        BigDecimal userAmountHj = userAmountFy.add(userAmountZb);//人员人工费用
        BigDecimal inputAmountHj = inputAmountFy.add(inputAmountZb);//直接投入费用
        BigDecimal depreciationAmountHj = depreciationAmountFy.add(depreciationAmountZb);//折旧费用
        BigDecimal amortizationAmountHj = amortizationAmountFy.add(amortizationAmountZb);//无形资产摊销
        BigDecimal desginAmountHj = desginAmountFy.add(desginAmountZb);//新产品设计费等
        BigDecimal itemSumAmountHj = itemSumAmountFy.add(itemSumAmountZb);//前五项小计
        BigDecimal otherAmountHj = otherAmountFy.add(otherAmountZb);//其他相关费用合计
        BigDecimal territoryAmountHj = territoryAmountFy.add(territoryAmountZb);//境内活动费
        BigDecimal adjustTerritoryAmountHj = adjustTerritoryAmountFy.add(adjustTerritoryAmountZb);//允许加计后的境内活动费
        BigDecimal abroadAmountHj = abroadAmountFy.add(abroadAmountZb);//境外活动费


        //经限额调整后的其他相关费用合计金额 = 其他费用合计与(前五项合计)*10%/90% 谁小填谁
        BigDecimal adjustOtherAmountHj = new BigDecimal(0);
        BigDecimal calItemSumAmountHj = itemSumAmountHj.multiply(new BigDecimal(0.1)).divide(new BigDecimal(0.9),2,BigDecimal.ROUND_HALF_UP);
        if(calItemSumAmountHj.compareTo(otherAmountHj) == -1){
            adjustOtherAmountHj = calItemSumAmountHj;
        }else  {
            adjustOtherAmountHj = otherAmountHj;
        }

        //调整后的境外活动费 = (前五项合计+经限额调整后的其他相关费用合计金额+允许加计后的境内活动费)*2/3 与 境外活动费*80% 谁小填谁
        BigDecimal adjustAbroadAmountHj = new BigDecimal(0);
        BigDecimal cal1 = itemSumAmountHj.add(adjustOtherAmountHj).add(adjustTerritoryAmountHj).multiply(new BigDecimal(2)).divide(new BigDecimal(3),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal cal2 = abroadAmountHj.multiply(new BigDecimal(0.8)).setScale(2,BigDecimal.ROUND_HALF_UP);

        if(cal1.compareTo(cal2) == -1){
            adjustAbroadAmountHj = cal1;
        }else  {
            adjustAbroadAmountHj = cal2;
        }


        //经限额调整后的其他相关费用合计与其他相关费用合计的比例
        BigDecimal otherRate = new BigDecimal(0);
        if(otherAmountHj.compareTo(BigDecimal.ZERO) != 0){
            otherRate = adjustOtherAmountHj.divide(otherAmountHj,16,BigDecimal.ROUND_HALF_UP);
        }


        //境外活动费合计与调整后的境外活动费合计的比例
        BigDecimal abroadRate = new BigDecimal(0);
        if(abroadAmountHj.compareTo(BigDecimal.ZERO) != 0){
            abroadRate = adjustAbroadAmountHj.divide(abroadAmountHj,16,BigDecimal.ROUND_HALF_UP);
        }




        //返回去，计算每个项目的经限额调整后的其他相关费用和调整后的境外活动费

        BigDecimal totalAmountZb = new BigDecimal("0.00");//允许加计扣除金额合计资本化小计
        BigDecimal totalAmountFy = new BigDecimal("0.00");//允许加计扣除金额合计费用化小计

        if(!CollectionUtils.isEmpty(resultList)){
            for(PageData data : resultList) {
                String expensesTypeCode = data.getString("expensesTypeCode");
                BigDecimal otherAmount = new BigDecimal(data.getString("otherAmount"));
                BigDecimal itemSumAmount = new BigDecimal(data.getString("itemSumAmount"));
                BigDecimal abroadAmount = new BigDecimal(data.getString("abroadAmount"));
                BigDecimal adjustTerritoryAmount = new BigDecimal(data.getString("adjustTerritoryAmount"));

                //经限额调整后的其他相关费用
                BigDecimal adjustOtherAmount = new BigDecimal("0.00");
                if(expensesTypeCode.equals("DICT10280001")){//费用化
                    BigDecimal cal= itemSumAmount.multiply(new BigDecimal(0.1)).divide(new BigDecimal(0.9),2,BigDecimal.ROUND_HALF_UP);
                    if(calItemSumAmountHj.compareTo(otherAmount) == -1){
                        adjustOtherAmount = cal;
                    }else  {
                        adjustOtherAmount = otherAmount;
                    }

                }else if(expensesTypeCode.equals("DICT10280002")){//资本化
                    adjustOtherAmount = otherAmount.multiply(otherRate).setScale(2,BigDecimal.ROUND_HALF_UP);
                }

                data.put("adjustOtherAmount",adjustOtherAmount);

                //调整后的境外活动费
                BigDecimal adjustAbroadAmount = new BigDecimal("0.00");
                if(expensesTypeCode.equals("DICT10280001")){//费用化
                    BigDecimal cal3 = itemSumAmount.add(adjustOtherAmount).add(adjustTerritoryAmount).multiply(new BigDecimal(2)).divide(new BigDecimal(3),2,BigDecimal.ROUND_HALF_UP);
                    BigDecimal cal4 = abroadAmount.multiply(new BigDecimal(0.8)).setScale(2,BigDecimal.ROUND_HALF_UP);

                    if(cal3.compareTo(cal4) == -1){
                        adjustAbroadAmount = cal3;
                    }else  {
                        adjustAbroadAmount = cal4;
                    }

                }else if(expensesTypeCode.equals("DICT10280002")){//资本化
                    adjustAbroadAmount = abroadAmount.multiply(abroadRate).setScale(2,BigDecimal.ROUND_HALF_UP);
                }


                data.put("adjustAbroadAmount",adjustAbroadAmount);


                //计算允许加计扣除金额合计
                BigDecimal totalAmount = itemSumAmount.add(adjustOtherAmount).add(adjustTerritoryAmount).add(adjustAbroadAmount);
                data.put("totalAmount",totalAmount);

                if(expensesTypeCode.equals("DICT10280001")){//费用化
                    totalAmountFy = totalAmountFy.add(totalAmount);

                }else if(expensesTypeCode.equals("DICT10280002")){//资本化
                    totalAmountZb = totalAmountZb.add(totalAmount);
                }


            }
        }

        BigDecimal totalAmountHj = totalAmountFy.add(totalAmountZb);//允许加计扣除金额合计的金额合计

        //往结果里添加资本化小计
        PageData zbSum = new PageData();
        BigDecimal adjustOtherAmountZb = otherAmountZb.multiply(otherRate).setScale(2,BigDecimal.ROUND_HALF_UP);//经限额调整后的其他相关费用
        BigDecimal adjustAbroadAmountZb = abroadAmountZb.multiply(abroadRate).setScale(2,BigDecimal.ROUND_HALF_UP);//调整后的境外活动费
        zbSum.put("projectCode","资本化金额小计");
        zbSum.put("totalAmount",totalAmountZb);
        zbSum.put("userAmount",userAmountZb);
        zbSum.put("inputAmount",inputAmountZb);
        zbSum.put("depreciationAmount",depreciationAmountZb);
        zbSum.put("amortizationAmount",amortizationAmountZb);
        zbSum.put("desginAmount",desginAmountZb);
        zbSum.put("itemSumAmount",itemSumAmountZb);
        zbSum.put("otherAmount",otherAmountZb);
        zbSum.put("adjustOtherAmount",adjustOtherAmountZb);
        zbSum.put("territoryAmount",territoryAmountZb);
        zbSum.put("adjustTerritoryAmount",adjustTerritoryAmountZb);
        zbSum.put("abroadAmount",abroadAmountZb);
        zbSum.put("adjustAbroadAmount",adjustAbroadAmountZb);

        resultList.add(zbSum);

        //往结果里添加费用化小计
        PageData fySum = new PageData();
        BigDecimal adjustOtherAmountFy = otherAmountFy.multiply(otherRate).setScale(2,BigDecimal.ROUND_HALF_UP);//经限额调整后的其他相关费用
        BigDecimal adjustAbroadAmountFy = abroadAmountFy.multiply(abroadRate).setScale(2,BigDecimal.ROUND_HALF_UP);//调整后的境外活动费
        fySum.put("projectCode","费用化金额小计");
        fySum.put("totalAmount",totalAmountFy);
        fySum.put("userAmount",userAmountFy);
        fySum.put("inputAmount",inputAmountFy);
        fySum.put("depreciationAmount",depreciationAmountFy);
        fySum.put("amortizationAmount",amortizationAmountFy);
        fySum.put("desginAmount",desginAmountFy);
        fySum.put("itemSumAmount",itemSumAmountFy);
        fySum.put("otherAmount",otherAmountFy);
        fySum.put("adjustOtherAmount",adjustOtherAmountFy);
        fySum.put("territoryAmount",territoryAmountFy);
        fySum.put("adjustTerritoryAmount",adjustTerritoryAmountFy);
        fySum.put("abroadAmount",abroadAmountFy);
        fySum.put("adjustAbroadAmount",adjustAbroadAmountFy);

        resultList.add(fySum);


        //查询其中：其他事项
        PageData data = (PageData) dao.findForObject("ExpensesBillSummaryMapper.queryOtherData",pd);
        PageData otherData = new PageData();
        otherData.put("projectCode","其中：其他事项");
        if(data != null){
            otherData.put("id",data.getString("id"));
            otherData.put("totalAmount",data.getString("totalAmount"));
            otherData.put("userAmount",data.getString("userAmount"));
            otherData.put("inputAmount",data.getString("inputAmount"));
            otherData.put("depreciationAmount",data.getString("depreciationAmount"));
            otherData.put("amortizationAmount",data.getString("amortizationAmount"));
            otherData.put("desginAmount",data.getString("desginAmount"));
            otherData.put("itemSumAmount",data.getString("itemSumAmount"));
            otherData.put("otherAmount",data.getString("otherAmount"));
            otherData.put("adjustOtherAmount",data.getString("adjustOtherAmount"));
            otherData.put("territoryAmount",data.getString("territoryAmount"));
            otherData.put("adjustTerritoryAmount",data.getString("adjustTerritoryAmount"));
            otherData.put("abroadAmount",data.getString("abroadAmount"));
            otherData.put("adjustAbroadAmount",data.getString("adjustAbroadAmount"));
        }

        resultList.add(otherData);

        //添加金额合计
        PageData totalData = new PageData();
        totalData.put("projectCode","金额合计");
        totalData.put("totalAmount",totalAmountHj);
        totalData.put("userAmount",userAmountHj);
        totalData.put("inputAmount",inputAmountHj);
        totalData.put("depreciationAmount",depreciationAmountHj);
        totalData.put("amortizationAmount",amortizationAmountHj);
        totalData.put("desginAmount",desginAmountHj);
        totalData.put("itemSumAmount",itemSumAmountHj);
        totalData.put("otherAmount",otherAmountHj);
        totalData.put("adjustOtherAmount",adjustOtherAmountHj);
        totalData.put("territoryAmount",territoryAmountHj);
        totalData.put("adjustTerritoryAmount",adjustTerritoryAmountHj);
        totalData.put("abroadAmount",abroadAmountHj);
        totalData.put("adjustAbroadAmount",adjustAbroadAmountHj);

        resultList.add(totalData);



        return resultList;
    }



    /**
     * 编辑其他事项
     * @param pd
     */
    @Override
    public String update(PageData pd) {
        //判断是新增还是编辑
        String id = pd.getString("id");
        if(StringUtils.isNotBlank(id)){//编辑
            dao.update("ExpensesBillSummaryMapper.updateData",pd);
        }else {//新增
            dao.insert("ExpensesBillSummaryMapper.addData",pd);

            id = pd.getString("id");
        }

        return id;
    }



    /**
     * 导出Excel
     *
     * @param pageData
     * @return
     */
    @Override
    public HSSFWorkbook exportExcel(PageData pageData) {
        //查询单据的所有数据
        List<PageData> list = queryList(pageData);
        String years = pageData.getString("years")+"年";

        String title = years+"研发支出辅助账汇总表";

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("研发支出辅助账汇总表");
        HSSFCellStyle styleHeader = ExcelUtil.setHeader(wb, sheet);// 表头
        HSSFCellStyle styleHeaderLeft = ExcelUtil.setHeaderLeft(wb, sheet);// 表头
        HSSFCellStyle styleCell = ExcelUtil.setWrapCell(wb, sheet);// 单元格
        // 创建标题
        HSSFRow rows = sheet.createRow(0);
        rows.setHeightInPoints(20);// 行高
        HSSFCell cells = rows.createCell(0);
        cells.setCellValue(title);
        cells.setCellStyle(styleHeader);
        ExcelUtil.merge(wb, sheet, 0, 0, 0, 16);


        //创建第二行
        //1、税务识别码
        rows = sheet.createRow(1);
        cells = rows.createCell(0);
        cells.setCellValue("纳税人识别号（统一社会信用代码）：");
        cells.setCellStyle(styleHeaderLeft);
        ExcelUtil.merge(wb, sheet, 1, 1, 0, 2);

        cells = rows.createCell(3);
        cells.setCellValue("91420600179614");
        cells.setCellStyle(styleHeaderLeft);
        ExcelUtil.merge(wb, sheet, 1, 1, 3, 4);

        //2、纳税人名称
        cells = rows.createCell(6);
        cells.setCellValue("纳税人名称：");
        cells.setCellStyle(styleHeaderLeft);

        cells = rows.createCell(7);
        cells.setCellValue("第一工程有限公司");
        cells.setCellStyle(styleHeaderLeft);
        ExcelUtil.merge(wb, sheet, 1, 1, 7, 9);

        //3、属期
        cells = rows.createCell(11);
        cells.setCellValue("属期：");
        cells.setCellStyle(styleHeaderLeft);

        cells = rows.createCell(12);
        cells.setCellValue(years);
        cells.setCellStyle(styleHeaderLeft);

        //
        cells = rows.createCell(14);
        cells.setCellValue("金额单位：元");
        cells.setCellStyle(styleHeaderLeft);
        ExcelUtil.merge(wb, sheet, 1, 1, 14, 16);


        //创建第三行
        rows = sheet.createRow(2);
        cells = rows.createCell(0);
        cells.setCellValue("项目编号");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(1);
        cells.setCellValue("项目名称");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(2);
        cells.setCellValue("完成情况");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(3);
        cells.setCellValue("支出类型");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(4);
        cells.setCellValue("允许加计扣除金额合计");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(5);
        cells.setCellValue("人员人工费用");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(6);
        cells.setCellValue("直接投入费用");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(7);
        cells.setCellValue("折旧费用");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(8);
        cells.setCellValue("无形资产摊销");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(9);
        cells.setCellValue("新产品设计费等");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(10);
        cells.setCellValue("前五项小计");
        cells.setCellStyle(styleCell);


        cells = rows.createCell(11);
        cells.setCellValue("其他相关费用及限额");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 2, 2, 11, 12);

        cells = rows.createCell(13);
        cells.setCellValue("委托研发费用及限额");
        cells.setCellStyle(styleCell);
        ExcelUtil.merge(wb, sheet, 2, 2, 13, 16);


        //创建第四行
        rows = sheet.createRow(3);
        cells = rows.createCell(11);
        cells.setCellValue("其他相关费用合计");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(12);
        cells.setCellValue("经限额调整后的其他相关费用");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(13);
        cells.setCellValue("委托境内机构或个人进行研发活动所发生的费用");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(14);
        cells.setCellValue("允许加计扣除的委托境内机构或个人进行研发活动所发生的费用");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(15);
        cells.setCellValue("委托境外机构进行研发活动所发生的费用");
        cells.setCellStyle(styleCell);

        cells = rows.createCell(16);
        cells.setCellValue("经限额调整后的委托境外机构进行研发活动所发生的费用");
        cells.setCellStyle(styleCell);


        ExcelUtil.merge(wb, sheet, 2, 3, 0, 0);
        ExcelUtil.merge(wb, sheet, 2, 3, 1, 1);
        ExcelUtil.merge(wb, sheet, 2, 3, 2, 2);
        ExcelUtil.merge(wb, sheet, 2, 3, 3, 3);
        ExcelUtil.merge(wb, sheet, 2, 3, 4, 4);
        ExcelUtil.merge(wb, sheet, 2, 3, 5, 5);
        ExcelUtil.merge(wb, sheet, 2, 3, 6, 6);
        ExcelUtil.merge(wb, sheet, 2, 3, 7, 7);
        ExcelUtil.merge(wb, sheet, 2, 3, 8, 8);
        ExcelUtil.merge(wb, sheet, 2, 3, 9, 9);
        ExcelUtil.merge(wb, sheet, 2, 3, 10, 10);

        int len = 0;
        if(!CollectionUtils.isEmpty(list)){
            len = list.size();
            for(int i=0;i<list.size();i++){
                PageData pd = list.get(i);

                HSSFRow row = sheet.createRow(i + 4);
                int j = 0;

                HSSFCell cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectCode"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("projectName"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("completionStatus"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("expensesType"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("totalAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("userAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("inputAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("depreciationAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("amortizationAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("desginAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("itemSumAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("otherAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("adjustOtherAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("territoryAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("adjustTerritoryAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("abroadAmount"));
                cell.setCellStyle(styleCell);

                cell = row.createCell(j++);
                cell.setCellValue(pd.getString("adjustAbroadAmount"));
                cell.setCellStyle(styleCell);

            }
        }


        ExcelUtil.merge(wb, sheet, len, len, 0, 3);
        ExcelUtil.merge(wb, sheet, len+1, len+1, 0, 3);
        ExcelUtil.merge(wb, sheet, len+2, len+2, 0, 3);
        ExcelUtil.merge(wb, sheet, len+3, len+3, 0, 3);


        HSSFRow row = sheet.createRow(len + 4);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("法定代表人（签章）：");


        sheet.setAutobreaks(true);


        return wb;
    }



}
