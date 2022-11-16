package com.rdexpense.manager.controller.analysis;


import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.common.util.SerialNumberUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.analysis.ExpensesBillSummaryListDto;
import com.rdexpense.manager.dto.analysis.ExpensesBillSummarySearchDto;
import com.rdexpense.manager.dto.analysis.ExpensesBillSummaryUpdateDto;
import com.rdexpense.manager.service.analysis.ExpensesBillSummaryService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:27
 */
@RestController
@RequestMapping("/expensesBillSummary")
@Api(value = "项目支出辅助账汇总表", tags = "项目支出辅助账汇总表")
public class ExpensesBillSummaryController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpensesBillSummaryController.class);

    @Autowired
    private ExpensesBillSummaryService expensesBillSummaryService;

    @Autowired
    private LogUtil logUtil;



    @PostMapping("/queryList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<List<ExpensesBillSummaryListDto>> queryList(ExpensesBillSummarySearchDto expensesBillSummarySearchDto) {
        PageData pd = this.getParams();
        checkParams(pd);
        try {
            List<PageData> list = expensesBillSummaryService.queryList(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, ExpensesBillSummaryListDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("查询项目支出辅助账汇总表失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "编辑其他事项")
    @PostMapping("/update")
    public ResponseEntity update(ExpensesBillSummaryUpdateDto expensesBillSummaryUpdateDto) {
        PageData pd = this.getParams();
        checkParams(pd);

        checkData(pd);
        ResponseEntity result = null;
        try {

            String id = expensesBillSummaryService.update(pd);
            result = ResponseEntity.success(id, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("编辑其他事项失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "项目支出辅助账汇总表", pd);
        }
    }



    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(ExpensesBillSummarySearchDto expensesBillSummarySearchDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        checkParams(pd);

        HttpServletResponse response = this.getResponse();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String number = SerialNumberUtil.generateSerialNo("expensesBillSummaryExcel");

        try {

            HSSFWorkbook wb = expensesBillSummaryService.exportExcel(pd);
            String fileName = "项目支出辅助账汇总表_" + date + "_" + number + ".xls";
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();


            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目支出辅助账汇总表", pd);
        }
    }


    private void checkParams(PageData pd){
        CheckParameter.checkPositiveInt(pd.getString("menuCode"), "功能菜单编码");
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgName"), "右上角项目名称",256);
        CheckParameter.stringLengthAndEmpty(pd.getString("years"), "年份",256);
    }


    private void checkData(PageData pd){
        String totalAmount = pd.getString("totalAmount");
        if(StringUtils.isNotBlank(totalAmount)){
            CheckParameter.checkDecimal(totalAmount, "允许加计扣除金额合计",20,2);
        }else {
            pd.put("totalAmount",null);
        }

        String userAmount = pd.getString("userAmount");
        if(StringUtils.isNotBlank(userAmount)){
            CheckParameter.checkDecimal(userAmount, "人员人工费用",20,2);
        }else {
            pd.put("userAmount",null);
        }

        String inputAmount = pd.getString("inputAmount");
        if(StringUtils.isNotBlank(inputAmount)){
            CheckParameter.checkDecimal(inputAmount, "直接投入费用",20,2);
        }else {
            pd.put("inputAmount",null);
        }

        String depreciationAmount = pd.getString("depreciationAmount");
        if(StringUtils.isNotBlank(depreciationAmount)){
            CheckParameter.checkDecimal(depreciationAmount, "折旧费用",20,2);
        }else {
            pd.put("depreciationAmount",null);
        }

        String amortizationAmount = pd.getString("amortizationAmount");
        if(StringUtils.isNotBlank(amortizationAmount)){
            CheckParameter.checkDecimal(amortizationAmount, "无形资产摊销",20,2);
        }else {
            pd.put("amortizationAmount",null);
        }

        String desginAmount = pd.getString("desginAmount");
        if(StringUtils.isNotBlank(desginAmount)){
            CheckParameter.checkDecimal(desginAmount, "新产品设计费等",20,2);
        }else {
            pd.put("desginAmount",null);
        }

        String itemSumAmount = pd.getString("itemSumAmount");
        if(StringUtils.isNotBlank(itemSumAmount)){
            CheckParameter.checkDecimal(itemSumAmount, "前五项小计",20,2);
        }else {
            pd.put("itemSumAmount",null);
        }

        String otherAmount = pd.getString("otherAmount");
        if(StringUtils.isNotBlank(otherAmount)){
            CheckParameter.checkDecimal(otherAmount, "其他相关费用合计",20,2);
        }else {
            pd.put("otherAmount",null);
        }

        String adjustOtherAmount = pd.getString("adjustOtherAmount");
        if(StringUtils.isNotBlank(adjustOtherAmount)){
            CheckParameter.checkDecimal(adjustOtherAmount, "经限额调整后的其他相关费用",20,2);
        }else {
            pd.put("adjustOtherAmount",null);
        }

        String territoryAmount = pd.getString("territoryAmount");
        if(StringUtils.isNotBlank(territoryAmount)){
            CheckParameter.checkDecimal(territoryAmount, "境内活动费",20,2);
        }else {
            pd.put("territoryAmount",null);
        }

        String adjustTerritoryAmount = pd.getString("adjustTerritoryAmount");
        if(StringUtils.isNotBlank(adjustTerritoryAmount)){
            CheckParameter.checkDecimal(adjustTerritoryAmount, "允许加计后的境内活动费",20,2);
        }else {
            pd.put("adjustTerritoryAmount",null);
        }

        String abroadAmount = pd.getString("abroadAmount");
        if(StringUtils.isNotBlank(abroadAmount)){
            CheckParameter.checkDecimal(abroadAmount, "境外活动费",20,2);
        }else {
            pd.put("abroadAmount",null);
        }

        String adjustAbroadAmount = pd.getString("adjustAbroadAmount");
        if(StringUtils.isNotBlank(adjustAbroadAmount)){
            CheckParameter.checkDecimal(adjustAbroadAmount, "调整后的境外活动费",20,2);
        }else {
            pd.put("adjustAbroadAmount",null);
        }

    }

}
