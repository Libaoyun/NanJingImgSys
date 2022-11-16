package com.rdexpense.manager.controller.analysis;


import com.alibaba.fastjson.JSONArray;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.CostAddDeductionDetailListDto;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.CostAddDeductionDetailUpdateDto;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.CostAddDeductionExpensesProjectApplyListDto;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.ExpensesMaxMonthDto;
import com.rdexpense.manager.service.analysis.CostAddDeductionDetailSummaryService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.INFO_EXPORT_SUCCESS;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:27
 */
@RestController
@RequestMapping("/CostAddDeductionDetailSummary")
@Api(value = "研发费用加减扣除明细表", tags = "研发费用加减扣除明细表")
public class CostAddDeductionDetailSummaryController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CostAddDeductionDetailSummaryController.class);

    @Autowired
    private CostAddDeductionDetailSummaryService costAddDeductionDetailSummaryService;

    @Autowired
    private LogUtil logUtil;



    @PostMapping("/queryList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessId", value = "研发项目申请表业务主键businessId,全部项目：0", required = true, dataType = "String"),
            @ApiImplicitParam(name = "startMonth", value = "选择开始月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "endMonth", value = "选择结束月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "creatorOrgId", value = "右上角项目ID", required = true, dataType = "String")
    })
    public ResponseEntity<List<CostAddDeductionDetailListDto>> queryList() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "研发项目申请表主键ID",256);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",256);
        if (pd.getString("startMonth").isEmpty() && pd.getString("endMonth").isEmpty()){
            throw new MyException("选择月份的开始月份、结束月份，不能同时为空");
        }
        try {
            List<PageData> list = costAddDeductionDetailSummaryService.queryList(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, CostAddDeductionDetailListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("查询研发费用加减扣除明细表失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryExpensesProjectApplyList")
    @ApiOperation(value = "查询研发费用支出已审批完成的研发项目申请列表", notes = "查询研发费用支出已审批完成的研发项目申请列表")
    public ResponseEntity<List<CostAddDeductionExpensesProjectApplyListDto>> queryApplyList(CostAddDeductionExpensesProjectApplyListDto costAddDeductionExpensesProjectApplyListDto) {
        PageData pd = this.getParams();
        try {
            List<PageData> list = costAddDeductionDetailSummaryService.queryProjectApplyList(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, CostAddDeductionExpensesProjectApplyListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询已审批完成的研发项目申请列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryExpensesMaxMonths")
    @ApiOperation(value = "查询研发费用支出已审批完成的最大月份值", notes = "查询研发费用支出已审批完成的最大月份值")
    @ApiImplicitParam(name = "creatorOrgId", value = "右上角项目ID", required = true, dataType = "String")
    public ResponseEntity<ExpensesMaxMonthDto> queryExpensesMaxMonths() {

        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",256);
        try {
            PageData pageData = costAddDeductionDetailSummaryService.queryExpensesMaxMonths(pd);
            return PropertyUtil.pushData2(pageData, ExpensesMaxMonthDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询研发费用支出已审批完成的最大月份值,request=[{}]", "");
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "编辑")
    @PostMapping("/update")
    public ResponseEntity update(CostAddDeductionDetailUpdateDto costAddDeductionDetailUpdateDto) {
        PageData pd = this.getParams();

        checkData(pd);
        ResponseEntity result = null;
        try {

            costAddDeductionDetailSummaryService.update(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("编辑失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "研发费用加减扣除明细表", pd);
        }
    }



    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessId", value = "研发项目申请表业务主键businessId,全部项目：0", required = true, dataType = "String"),
            @ApiImplicitParam(name = "startMonth", value = "选择开始月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "endMonth", value = "选择结束月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "creatorOrgId", value = "右上角项目ID", required = true, dataType = "String")
    })
    public ResponseEntity exportExcel() {
        PageData pd = this.getParams();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "研发项目申请表主键ID",256);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",256);
        if (pd.getString("startMonth").isEmpty() && pd.getString("endMonth").isEmpty()){
            throw new MyException("开始月份、结束月份，不能同时为空");
        }

        String belongingMonthStr = "";
        if (pd.getString("startMonth").isEmpty() && !pd.getString("endMonth").isEmpty()){
            belongingMonthStr = "截止到" + pd.getString("endMonth");
        } else if (!pd.getString("startMonth").isEmpty() && pd.getString("endMonth").isEmpty()){
            belongingMonthStr = pd.getString("startMonth") +"~" + sdf.format(new Date());
        } else if (!pd.getString("startMonth").isEmpty() && !pd.getString("endMonth").isEmpty()){
            belongingMonthStr = pd.getString("startMonth") +"~" + pd.getString("endMonth");
        }

        pd.put("belongingMonthStr",belongingMonthStr);

        ResponseEntity result = null;
        HttpServletResponse response = this.getResponse();
        try {

            HSSFWorkbook wb = costAddDeductionDetailSummaryService.exportExcel(pd);
            String fileName = "研发费用加减扣除明细表(" + belongingMonthStr + ").xls";
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
            logUtil.saveLogData(result.getCode(), 6, "研发费用加减扣除明细表", pd);
        }
    }



    private void checkData(PageData pd){
//        List<PageData> costAddDeductionDetailList = (List<PageData>) pd.get("costAddDeductionDetailList");
//        //把List<PageData>集合里的map对象转为json对象
//        pd.put("costAddDeductionDetailList", JsonUtil.listPageDataToJsonObject(costAddDeductionDetailList));
        if (pd.getString("businessId").equals("0")){
            throw new MyException("选择项目为全部项目时，不能进行编辑");
        }
        CheckParameter.stringLengthAndEmpty(pd.getString("startMonth"), "开始月份",20);
        CheckParameter.stringLengthAndEmpty(pd.getString("endMonth"), "结束月份",20);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",256);
        if (!pd.getString("businessId").equals("0") && !pd.getString("startMonth").equals(pd.getString("endMonth"))){
            throw new MyException("选择项目为某个项目、开始月份不等于结束月份时，不能进行编辑");
        }
        List<PageData> pageDataList = JSONArray.parseArray(pd.getString("costAddDeductionDetailList"), PageData.class);
        if (!CollectionUtils.isEmpty(pageDataList)){
            for (PageData pageData : pageDataList) {
                CheckParameter.stringLengthAndEmpty(pageData.getString("expenseAccount"), "支出科目",255);
                if(!pageData.getString("expenseCost").isEmpty()){
                    CheckParameter.checkDecimal(pageData.getString("expenseCost"), pageData.getString("expenseAccount"), 18, 2);
                }
            }
        }
    }

}
