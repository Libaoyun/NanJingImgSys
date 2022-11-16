package com.rdexpense.manager.controller.itemClosureCheck;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.text.Document;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfWriter;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.ItemChange.ItemChangeBudgetDetailDto;
import com.rdexpense.manager.dto.ItemChange.ItemChangeDetailDto;
import com.rdexpense.manager.dto.ItemChange.ItemChangeListDto;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesBudgetAccumulatedDto;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesDetailDto;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesListDto;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.base.FlowAbolishDto;
import com.rdexpense.manager.dto.base.FlowApproveDto;
import com.rdexpense.manager.dto.projectApply.ResearchUserDto;
import com.rdexpense.manager.service.itemClosureCheck.ItemChangeService;
import com.rdexpense.manager.service.itemClosureCheck.ItemExpensesService;
import com.rdexpense.manager.service.system.DictionaryService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

@RestController
@RequestMapping("/itemChange")
@Api(value = "研发项目变更管理", tags = "研发项目变更管理")
public class ItemChangeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ItemChangeController.class);

    @Autowired
    private ItemChangeService itemChangeService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private LogUtil logUtil;

    @PostMapping("/queryList")
    @ApiOperation(value = "查询研发项目变更列表", notes = "查询研发项目变更列表")
    public ResponseEntity<PageInfo<ItemChangeListDto>> queryList(ItemChangeListDto itemChangeListDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = itemChangeService.queryItemChangeList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ItemChangeListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询研发项目变更列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryBudget")
    @ApiOperation(value = "查询经费预算", notes = "查询经费预算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectApplyMainId", value = "研发项目申请表业务主键businessId", required = true, dataType = "String"),
    })
    public ResponseEntity<ItemChangeBudgetDetailDto> queryBudget() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("projectApplyMainId"), "研发项目申请表主键ID",256);
        ResponseEntity result = null;
        try {
            List<PageData> list = itemChangeService.queryBudget(pd);

            result = ResponseEntity.success(PropertyUtil.covertListModel(list, ItemChangeBudgetDetailDto.class));
            return result;
        } catch (MyException e) {
            logger.error("查询经费预算失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @PostMapping("/querySecondaryMaxMonths")
    @ApiOperation(value = "周期变更时，查询最大的费用支出月份值", notes = "周期变更时，查询最大的费用支出月份值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectApplyMainId", value = "研发项目申请表业务主键businessId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "secondarySubjectCode", value = "二级科目编码", required = true, dataType = "String")
    })
    public ResponseEntity querySecondaryMaxMonths() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("projectApplyMainId"), "研发项目申请表主键ID",256);
        ResponseEntity result = null;
        try {

            PageData pageData = itemChangeService.querySecondarySubjectCodeMaxMonths(pd);

            result = ResponseEntity.success(pageData, ConstantMsgUtil.ERR_QUERY_FAIL.desc());
            return result;
        } catch (MyException e) {
            logger.error("周期变更时，查询最大的费用支出月份值失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @ApiOperation(value = "删除研发项目变更申请")
    @PostMapping("/delete")
    public ResponseEntity deleteApply(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String businessIdList = pd.getString("businessIdList");
        if (StringUtils.isBlank(businessIdList)) {
            throw new MyException("业务主键businessId不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            itemChangeService.deleteItemChange(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除研发项目变更申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "研发项目变更申请", pd);
        }
    }

    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询研发项目变更申请详情", notes = "查询研发项目变更申请详情")
    @ApiImplicitParam(name = "businessId", value = "业务主键businessId", required = true, dataType = "String")
    public ResponseEntity<ItemChangeDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键businessId",64);
        try {

            PageData pageData = itemChangeService.getItemChangeDetail(pd);
            if (pageData != null){
                PageData projectPageData = itemChangeService.getProjectApplyDetail(pageData);
                pageData.put("startYear",projectPageData.getString("startYear"));
                pageData.put("endYear",projectPageData.getString("endYear"));
                pageData.put("bureauLevel",projectPageData.getString("bureauLevel"));
            }

            return PropertyUtil.pushData(pageData, ItemChangeDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询研发项目变更详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @ApiOperation(value = "新增或编辑")
    @PostMapping("/saveOrUpdate")
    public ResponseEntity addUser(ItemChangeDetailDto itemChangeDetailDto) {
        PageData pd = this.getParams();
        saveCheckParam(pd);
        ResponseEntity result = null;
        try {

            itemChangeService.saveOrUpdate(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增或编辑研发项目变更失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "研发项目变更", pd);
        }
    }

    @ApiOperation(value = "提交")
    @PostMapping("/submit")
    public ResponseEntity submit(ItemChangeDetailDto itemChangeDetailDto) {
        PageData pd = this.getParams();

        //1：列表提交，2：保存提交，3：编辑提交
        if (!pd.getString("flag").equals("1")) {
            //先保存或编辑校验
            submitCheckParam(pd);
        } else {
            PageData pageData = itemChangeService.getItemChangeDetail(pd);
            if (pageData != null){
                PageData projectPageData = itemChangeService.getProjectApplyDetail(pageData);
                pageData.put("startYear",projectPageData.getString("startYear"));
                pageData.put("endYear",projectPageData.getString("endYear"));

                //查询最大月份
                pd.put("projectApplyMainId",pageData.getString("projectApplyMainId"));
                PageData maxMonthsPageData = itemChangeService.querySecondarySubjectCodeMaxMonths(pd);
                if (maxMonthsPageData != null){
                    pageData.put("belongingMonth",maxMonthsPageData.getString("belongingMonth"));
                }
            }
            pageData.put("menuCode",pd.getString("menuCode"));
            pageData.put("flag",pd.getString("flag"));
            //启动工作流时会进行校验
            pd.put("serialNumber",pageData.getString("serialNumber"));

            //列表提交校验
            submitCheckParam(pageData);
            if (pd.getString("flag").equals("3")){
                CheckParameter.stringLengthAndEmpty(pageData.getString("businessId"), "业务主键businessId",64);
            }
        }

        ResponseEntity result = null;
        try {

            itemChangeService.submit(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("提交研发项目变更失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "研发项目变更", pd);
        }
    }

    @ApiOperation(value = "审批(同意/回退)")
    @PostMapping(value = "/approve")
    public ResponseEntity approve(FlowApproveDto flowApproveDto){
        PageData pd = this.getParams();
        checkApprove(pd);
        ResponseEntity result = null;
        try {
            itemChangeService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("研发项目变更审批失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 8, "研发项目变更", pd);
        }
    }

    @ApiOperation(value = "废除（未开发）")
    @PostMapping(value = "/abolish")
    public ResponseEntity abolishRecord(FlowAbolishDto flowAbolishDto){
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            itemChangeService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("研发项目变更废除失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 9, "研发项目变更支出", pd);
        }
    }

    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        //取出菜单id
        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("businessIdList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        try {
            HSSFWorkbook wb = itemChangeService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "研发项目变更_" + df.format(new Date()) + "_"+serialNumber +".xls";
            res.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = res.getOutputStream();
            wb.write(out);
            out.close();
            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "研发项目变更EXCEL", pd);
        }
    }

    @ApiOperation(value = "导出PDF", notes = "导出PDF")
    @PostMapping(value = "/exportPdf")
    public ResponseEntity exportPdf(BusinessIdListDto businessIdListDto) {
        PageData pageData = this.getParams();

        //取出菜单id
        CheckParameter.checkDefaultParams(pageData);
        //校验取出参数
        String idList = pageData.getString("businessIdList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        ResponseEntity result = null;
        try {
            HttpServletResponse response = this.getResponse();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            String fileName =  "研发项目变更_" + date + ".pdf";

            //输出流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String idStr = pageData.getString("businessIdList");
            List<PageData> listId = (List<PageData>) JSONArray.parse(idStr);
            // 打开文档，开始填写内容
            Document document = new Document(new RectangleReadOnly(842, 595));
            PdfWriter.getInstance(document, bos);
            document.open();
            if (listId.size() > 1) {
                // 文件名
                for (int i = 0; i < listId.size(); i++) {
                    if (i > 0) {//第一页往后，下一条记录新起一页
                        document.newPage();
                    }
                    PageData data = new PageData();
                    data.put("businessId", listId.get(i));
                    if (data.size() == 1) {//导出选择部分
                        data  = itemChangeService.getItemChangeDetail(data);
                    }
                    //生成单个pdf
                    itemChangeService.exportPDF(data, document);
                }
            } else {
                // 第一页
                if (listId.size() > 0) {
                    PageData data = new PageData();
                    data.put("businessId", listId.get(0));
                    if (data.size() == 1) {//导出选择部分
                        data  = itemChangeService.getItemChangeDetail(data);
                    }
                    itemChangeService.exportPDF(data, document);
                } else {
                    throw new MyException(ConstantMsgUtil.WAN_EXPORT.desc());
                }
            }
            document.close();

            //写入返回response
            response.reset();
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8"));
            response.setContentType("application/octet-stream; charset=utf-8");
            OutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(bos.toByteArray());
            out.flush();
            out.close();
            result = ResponseEntity.success(ConstantMsgUtil.INFO_EXPORT_SUCCESS.val(), ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "研发项目变更PDF", pageData);
        }
    }

    /**
     * 审批时校验
     * @param pd
     */
    private void checkApprove(PageData pd){
        CheckParameter.checkDefaultParams(pd);

        CheckParameter.stringLengthAndEmpty(pd.getString("waitId"), "待办列表waitId", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("approveComment"), "审批意见", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("approveType"), "审批类型", 256);

    }

    /**
     * 提交时校验
     * @param pd
     */
    private void submitCheckParam(PageData pd) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String startYear = pd.getString("startYear");
        String endYear = pd.getString("endYear");
        String belongingMonth = pd.getString("belongingMonth");

        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("telephone"), "电话", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("partContent"), "要求变更的原项目相关部分内容", 1024);
        CheckParameter.stringLengthAndEmpty(pd.getString("changeAdvise"), "要求变更的内容或建议", 1024);
        CheckParameter.stringLengthAndEmpty(pd.getString("changeReason"), "变更理由", 1024);
        CheckParameter.stringLengthAndEmpty(pd.getString("implementation"), "实施情况", 1024);
        CheckParameter.stringLengthAndEmpty(pd.getString("fundsUse"), "经费使用情况", 1024);

        String postArray = "";

        //查询岗位不兼容字典信息
        pd.put("dicTypeId","1021");
        List<PageData> dicEnumList = dictionaryService.findDictionariesList(pd);
        if (!CollectionUtils.isEmpty(dicEnumList)){
            for (PageData pageData : dicEnumList) {
                postArray += pageData.getString("dicEnumName") + ",";
            }
        }

        //changeTypeCode周期变更：1,预算变更：2,人员变更：3
        //1：列表提交，2：保存提交，3：编辑提交
        if (!pd.getString("flag").equals("1")){
            if (pd.getString("changeTypeCode").equals("1")){
                List<PageData> pageDataList = JSONArray.parseArray(pd.getString("cycleList"), PageData.class);
                if (pageDataList.size() > 0){
                    PageData pageData = pageDataList.get(1);
                    CheckParameter.stringEmpty(pageData.getString("startYear"),"开始年度");
                    CheckParameter.stringEmpty(pageData.getString("endYear"),"结束年度");
                    try {

                        if(!belongingMonth.isEmpty()){
                            belongingMonth = belongingMonth + "-01";;
                            if (!df.parse(pageData.getString("startYear")).before(df.parse(belongingMonth)) && !df.parse(pageData.getString("endYear")).after(df.parse(belongingMonth))){
                                throw new MyException("周期变更的研究开始年度和结束年度设置超出了费用支出最大月份值："+belongingMonth+"，不合法！");
                            }
                        }

                        if (!df.parse(pageData.getString("startYear")).before(df.parse(pageData.getString("endYear")))){
                            throw new MyException("周期变更的研究开始年度大于结束年度，不合法");
                        }
                    } catch (Exception e) {
                        throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
                    }
                }
            } else  if (pd.getString("changeTypeCode").equals("2")){
                List<PageData> pageDataList = JSONArray.parseArray(pd.getString("budgetDetailList"), PageData.class);
                if (pageDataList.size() > 0){
                    for (PageData pageData : pageDataList) {
                        if(!pageData.getString("sourceAccount").isEmpty()){
                            CheckParameter.checkDecimal(pageData.getString("sourceBudgetChange"), pageData.getString("sourceAccount"), 18, 2);
                        }
                        if(!pageData.getString("expenseAccount").isEmpty()){
                            CheckParameter.checkDecimal(pageData.getString("expenseBudgetChange"), pageData.getString("expenseAccount"), 18, 2);
                        }
                    }
                }
            } else {
                List<PageData> pageDataList = JSONArray.parseArray(pd.getString("userInfoList"), PageData.class);

                if (pageDataList != null && pageDataList.size() > 0){
                    for (int i = 0; i < pageDataList.size(); i++) {
                        Integer j = i+1;
                        PageData pageData = pageDataList.get(i);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("userName"), "第" + j +"行的姓名", 256);
                        String idCard = "";
                        if (!StringUtils.isNotBlank(pageData.getString("idCard"))){
                            idCard = pageData.getString("idCard").substring(0,pageData.getString("idCard").length()-1);
                        }
                        CheckParameter.idNumber(idCard,"第" + j +"行的身份证");
                        CheckParameter.intNumberLength(pageData.getString("age"),"第" + j +"行的年龄",3);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("gender"),"第" + j +"行的性别",2);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("education"),"第" + j +"行的学历",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("belongDepartment"),"第" + j +"行的所属部门",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("belongPost"),"第" + j +"行的所属职务",100);
                        if (!CollectionUtils.isEmpty(dicEnumList)){
                            for (int k = 0; k < dicEnumList.size(); k++) {
                                PageData dicPageData = dicEnumList.get(k);
                                if (pageData.getString("belongPost").contains(dicPageData.getString("dicEnumName"))){
                                    throw new MyException("第" + j +"行的所属职务不合法，不能包含以下职务名称：" + StringUtils.join(postArray, ","));
                                }
                            }
                        }

                        CheckParameter.stringLengthAndEmpty(pageData.getString("majorStudied"),"第" + j +"行的所学专业",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("majorWorked"),"第" + j +"行的从事专业",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("belongUnit"),"第" + j +"行的所在单位",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("taskDivision"),"第" + j +"行的研究任务及分工",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("workRate"),"第" + j +"行的全时率",10);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("telephone"),"第" + j +"行的联系电话",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("startDate"),"第" + j +"行的参与研究开始日期",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("endDate"),"第" + j +"行的参与研究结束日期",100);

                        try {
                            if (df.parse(pageData.getString("startDate")).before(df.parse(startYear)) || df.parse(pageData.getString("startDate")).after(df.parse(endYear))){
                                throw new MyException("第" + j +"行的参与研究开始日期不在研发项目的日期范围之内");
                            }

                            if (df.parse(pageData.getString("endDate")).before(df.parse(startYear)) || df.parse(pageData.getString("endDate")).after(df.parse(endYear))){
                                throw new MyException("第" + j +"行的参与研究结束日期不在研发项目的日期范围之内");
                            }
                        } catch (Exception e) {
                            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
                        }
                    }
                }
            }
        } else {
            if (pd.getString("changeTypeCode").equals("1")){
                List<PageData> cycleList = (List<PageData>) pd.get("cycleList");
                //把List<PageData>集合里的map对象转为json对象
                pd.put("cycleList", JsonUtil.listPageDataToJsonObject(cycleList));
                List<PageData> pageDataList = JSONArray.parseArray(pd.getString("cycleList"), PageData.class);
                if (pageDataList.size() > 0){
                    PageData pageData = pageDataList.get(1);
                    CheckParameter.stringEmpty(pageData.getString("startYear"),"开始年度");
                    CheckParameter.stringEmpty(pageData.getString("endYear"),"结束年度");
                    try {
                        if(!belongingMonth.isEmpty()){
                            belongingMonth = belongingMonth + "-01";
                            if (!df.parse(pageData.getString("startYear")).before(df.parse(belongingMonth)) && !df.parse(pageData.getString("endYear")).after(df.parse(belongingMonth))){
                                throw new MyException("周期变更的研究开始年度和结束年度设置超出了费用支出最大月份值："+belongingMonth+"，不合法！");
                            }
                        }

                        if (!df.parse(pageData.getString("startYear")).before(df.parse(pageData.getString("endYear")))){
                            throw new MyException("周期变更的研究开始年度大于结束年度，不合法");
                        }
                    } catch (Exception e) {
                        throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
                    }
                }
            } else  if (pd.getString("changeTypeCode").equals("2")){
                List<PageData> budgetDetailList = (List<PageData>) pd.get("budgetDetailList");
                //把List<PageData>集合里的map对象转为json对象
                pd.put("budgetDetailList", JSON.toJSONStringWithDateFormat(budgetDetailList, "yyyy-MM-dd", SerializerFeature.WriteDateUseDateFormat));
                List<PageData> pageDataList = JSONArray.parseArray(pd.getString("budgetDetailList"), PageData.class);
                if (pageDataList.size() > 0){
                    for (PageData pageData : pageDataList) {
                        if(!pageData.getString("sourceAccount").isEmpty()){
                            CheckParameter.checkDecimal(pageData.getString("sourceBudgetChange"), pageData.getString("sourceAccount"), 18, 2);
                        }
                        if(!pageData.getString("expenseAccount").isEmpty()){
                            CheckParameter.checkDecimal(pageData.getString("expenseBudgetChange"), pageData.getString("expenseAccount"), 18, 2);
                        }
                    }
                }
            } else {
                List<PageData> userInfoList = (List<PageData>) pd.get("userInfoList");
                pd.put("userInfoList", JSON.toJSONStringWithDateFormat(userInfoList, "yyyy-MM-dd", SerializerFeature.WriteDateUseDateFormat));
                List<PageData> pageDataList = JSONArray.parseArray(pd.getString("userInfoList"), PageData.class);

                if (pageDataList != null && pageDataList.size() > 0){
                    for (int i = 0; i < pageDataList.size(); i++) {
                        Integer j = i+1;
                        PageData pageData = pageDataList.get(i);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("userName"), "第" + j +"行的姓名", 256);
                        String idCard = "";
                        if (!StringUtils.isNotBlank(pageData.getString("idCard"))){
                            idCard = pageData.getString("idCard").substring(0,pageData.getString("idCard").length()-1);
                        }
                        CheckParameter.idNumber(idCard,"第" + j +"行的身份证");
                        CheckParameter.intNumberLength(pageData.getString("age"),"第" + j +"行的年龄",3);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("gender"),"第" + j +"行的性别",2);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("education"),"第" + j +"行的学历",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("belongDepartment"),"第" + j +"行的所属部门",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("belongPost"),"第" + j +"行的所属职务",100);

                        if (!CollectionUtils.isEmpty(dicEnumList)){
                            for (int k = 0; k < dicEnumList.size(); k++) {
                                PageData dicPageData = dicEnumList.get(k);
                                if (pageData.getString("belongPost").contains(dicPageData.getString("dicEnumName"))){
                                    throw new MyException("第" + j +"行的所属职务不合法，不能包含以下职务名称：" + StringUtils.join(postArray, ","));
                                }
                            }
                        }

                        CheckParameter.stringLengthAndEmpty(pageData.getString("majorStudied"),"第" + j +"行的所学专业",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("majorWorked"),"第" + j +"行的从事专业",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("belongUnit"),"第" + j +"行的所在单位",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("taskDivision"),"第" + j +"行的研究任务及分工",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("workRate"),"第" + j +"行的全时率",10);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("telephone"),"第" + j +"行的联系电话",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("startDate"),"第" + j +"行的参与研究开始日期",100);
                        CheckParameter.stringLengthAndEmpty(pageData.getString("endDate"),"第" + j +"行的参与研究结束日期",100);
                        try {
                            if (df.parse(pageData.getString("startDate")).before(df.parse(startYear)) || df.parse(pageData.getString("startDate")).after(df.parse(endYear))){
                                throw new MyException("第" + j +"行的参与研究开始日期不在研发项目的日期范围之内");
                            }

                            if (df.parse(pageData.getString("endDate")).before(df.parse(startYear)) || df.parse(pageData.getString("endDate")).after(df.parse(endYear))){
                                throw new MyException("第" + j +"行的参与研究结束日期不在研发项目的日期范围之内");
                            }
                        } catch (Exception e) {
                            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
                        }
                    }
                }
            }
        }
    }


    /**
     * 保存时校验
     * @param pd
     */
    private void saveCheckParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLength(pd.getString("projectName"), "项目名称", 256);
        CheckParameter.stringLength(pd.getString("telephone"), "电话", 256);
        CheckParameter.stringLength(pd.getString("partContent"), "要求变更的原项目相关部分内容", 1024);
        CheckParameter.stringLength(pd.getString("changeAdvise"), "要求变更的内容或建议", 1024);
        CheckParameter.stringLength(pd.getString("changeReason"), "变更理由", 1024);
        CheckParameter.stringLength(pd.getString("implementation"), "实施情况", 1024);
        CheckParameter.stringLength(pd.getString("fundsUse"), "经费使用情况", 1024);

        if (!pd.getString("budgetDetailList").isEmpty()){
            List<PageData> budgetDetailList = JSONArray.parseArray(pd.getString("budgetDetailList"),PageData.class);
            for (PageData pageData : budgetDetailList) {
                if (StringUtils.isNotBlank(pageData.getString("sourceBudgetChange"))){
                    CheckParameter.checkDecimal(pageData.getString("sourceBudgetChange"), pageData.getString("sourceAccount"), 18, 2);
                }

                if (StringUtils.isNotBlank(pageData.getString("expenseBudgetChange"))){
                    CheckParameter.checkDecimal(pageData.getString("expenseBudgetChange"), pageData.getString("expenseAccount"), 18, 2);
                }
            }
        }

    }
}
