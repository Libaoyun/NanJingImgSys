package com.rdexpense.manager.controller.analysis;


import com.alibaba.fastjson.JSONArray;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PageHelperUtils;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.analysis.BudgetDetailDto;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.CostAddDeductionDetailListDto;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.CostAddDeductionDetailUpdateDto;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.CostAddDeductionExpensesProjectApplyListDto;
import com.rdexpense.manager.dto.analysis.CostAddDeductionDetail.ExpensesMaxMonthDto;
import com.rdexpense.manager.dto.analysis.ProjectBudgetDto;
import com.rdexpense.manager.dto.projectApply.ProjectApplyListDto;
import com.rdexpense.manager.service.analysis.BudgetAndPayContrastService;
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
@RequestMapping("/BudgetAndPayContrast")
@Api(value = "预算与支出对比表", tags = "预算与支出对比表")
public class BudgetAndPayContrastController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetAndPayContrastController.class);

    @Autowired
    private BudgetAndPayContrastService budgetAndPayContrastService;

    @Autowired
    private LogUtil logUtil;



    @PostMapping("/queryList")
    @ApiOperation(value = "查询预算与支出对比列表", notes = "查询预算与支出对比列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "右上角选择的是公司还是项目,公司：0，项目：1", required = true, dataType = "String"),
            @ApiImplicitParam(name = "startMonth", value = "选择开始月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "endMonth", value = "选择结束月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "creatorOrgId", value = "右上角项目ID", required = true, dataType = "String")
    })
    public ResponseEntity<List<BudgetDetailDto>> queryList() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("flag"), "选择类型",10);
        if (pd.getString("flag").equals("1")){
            CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",256);
        }
        if (pd.getString("startMonth").isEmpty()){
            throw new MyException("选择月份的开始月份、结束月份，不能同时为空");
        }
        if (pd.getString("endMonth").isEmpty()){
            throw new MyException("选择月份的开始月份、结束月份，不能同时为空");
        }
        try {
            List<PageData> list = budgetAndPayContrastService.queryList(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, BudgetDetailDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("查询预算与支出对比表失败,request=[{}]", pd);
            e.printStackTrace();
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryProjectBudgetList")
    @ApiOperation(value = "查询立项项目预算与支出列表", notes = "查询立项项目预算与支出列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "右上角选择的是公司还是项目,公司：0，项目：1", required = true, dataType = "String"),
            @ApiImplicitParam(name = "startMonth", value = "选择开始月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "endMonth", value = "选择结束月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "creatorOrgId", value = "右上角项目ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "startYear", value = "起始年度", required = true, dataType = "String"),
            @ApiImplicitParam(name = "endYear", value = "结束年度", required = true, dataType = "String"),
            @ApiImplicitParam(name = "serialNumber", value = "项目编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名称", required = true, dataType = "String")
    })
    public ResponseEntity<PageInfo<ProjectBudgetDto>> queryProjectBudgetList() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("flag"), "选择类型",10);
        if (pd.getString("flag").equals("1")){
            CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",256);
        }

        if (pd.getString("startMonth").isEmpty() && pd.getString("endMonth").isEmpty()){
            throw new MyException("选择月份的开始月份、结束月份，不能同时为空");
        }
        try {
//            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            PageData query = new PageData();
            query.put("pageNum",pd.getString("pageNum"));
            query.put("pageSize",pd.getString("pageSize"));
            pd.remove("pageNum");
            pd.remove("pageSize");
            List<PageData> list = budgetAndPayContrastService.queryProjectBudgetList(pd);
            PageInfo<PageData> pageInfo = PageHelperUtils.initPageInfo(query.getInt("pageNum"), query.getInt("pageSize"),list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ProjectBudgetDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("查询预算与支出对比表失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "右上角选择的是公司还是项目,公司：0，项目：1", required = true, dataType = "String"),
            @ApiImplicitParam(name = "startMonth", value = "选择开始月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "endMonth", value = "选择结束月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "creatorOrgId", value = "右上角项目ID", required = true, dataType = "String")
    })
    public ResponseEntity exportExcel() {
        PageData pd = this.getParams();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        CheckParameter.stringLengthAndEmpty(pd.getString("flag"), "选择类型",10);
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

            HSSFWorkbook wb = budgetAndPayContrastService.exportExcel(pd);
            String fileName = "预算与支出对比表(" + belongingMonthStr + ").xls";
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
            logUtil.saveLogData(result.getCode(), 6, "预算与支出对比表", pd);
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
