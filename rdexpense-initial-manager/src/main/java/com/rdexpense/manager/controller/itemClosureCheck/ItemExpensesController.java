package com.rdexpense.manager.controller.itemClosureCheck;

import com.alibaba.fastjson.JSONArray;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.common.util.SerialNumberUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.text.Document;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfWriter;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesBudgetAccumulatedDto;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesDetailDto;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesListDto;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.base.FlowAbolishDto;
import com.rdexpense.manager.dto.base.FlowApproveDto;
import com.rdexpense.manager.dto.flow.nextApprovalUserListDTO;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.itemClosureCheck.ItemExpensesService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
@RequestMapping("/itemExpenses")
@Api(value = "研发项目费用支出管理", tags = "研发项目费用支出管理")
public class ItemExpensesController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ItemExpensesController.class);

    @Autowired
    private ItemExpensesService itemExpensesService;

    @Autowired
    @Resource(name = "FlowService")
    private FlowService flowService;

    @Autowired
    private BaseDao dao;

    @Autowired
    private LogUtil logUtil;

    @ApiOperation(value = "获取选择下一个审批节点的人员信息列表", notes = "获取选择下一个审批节点的人员信息列表")
    @GetMapping(value = "/queryNextApprovalUserList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "creatorOrgId", value = "项目ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "serialNumber", value = "单据号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "menuCode", value = "菜单ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "nodeType", value = "节点类型,0:提交,1:审批", required = true, dataType = "String"),
            @ApiImplicitParam(name = "waitId", value = "待办表主键ID,审批时传此参数", required = true, dataType = "String")
    })
    public ResponseEntity<nextApprovalUserListDTO> queryNextApprovalUserList() {
        PageData pd = this.getParams();
        //校验参数
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "业务主键ID", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("menuCode"), "菜单编码", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("nodeType"), "节点类型", 128);
        if (pd.getString("nodeType").equals("1")){
            CheckParameter.stringLengthAndEmpty(pd.getString("serialNumber"), "单据号", 128);
            CheckParameter.stringLengthAndEmpty(pd.getString("waitId"), "待办表主键ID", 128);
            CheckParameter.stringLengthAndEmpty(pd.getString("createUserId"), "当前审批人ID", 128);
        }

        try {
            PageData pageData = flowService.queryNextApprovalUserList(pd);
            return PropertyUtil.pushData(pageData, nextApprovalUserListDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询下一个审批节点的人员信息列表失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }

    }

    @PostMapping("/queryList")
    @ApiOperation(value = "查询费用支出列表", notes = "查询费用支出列表")
    public ResponseEntity<PageInfo<ItemExpensesListDto>> queryList(ItemExpensesListDto itemExpensesListDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = itemExpensesService.queryItemExpensesList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ItemExpensesListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询研发项目费用支出列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryBudgetAccumulated")
    @ApiOperation(value = "查询预算总额、已支出累计", notes = "查询预算总额、已支出累计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectApplyMainId", value = "研发项目申请表业务主键businessId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "secondarySubjectCode", value = "二级科目编码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "bureauLevel", value = "是否为局级 0：否,1：是", required = true, dataType = "String")
    })
    public ResponseEntity<ItemExpensesBudgetAccumulatedDto> queryBudgetAccumulated() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("projectApplyMainId"), "研发项目申请表主键ID",256);
        CheckParameter.stringLengthAndEmpty(pd.getString("secondarySubjectCode"), "二级科目编码",64);
        CheckParameter.stringLengthAndEmpty(pd.getString("bureauLevel"), "是否为局级",8);
        try {

            PageData pageData = itemExpensesService.queryBudgetAccumulated(pd);

            return PropertyUtil.pushData(pageData, ItemExpensesBudgetAccumulatedDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询研发项目预算总额、已支出累计失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @PostMapping("/queryBudgetExcessTips")
    @ApiOperation(value = "查询累计支出总额是否超出该类预算5%", notes = "查询累计支出总额是否超出该类预算5%")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectApplyMainId", value = "研发项目申请表业务主键businessId(长度,256)", required = true, dataType = "String"),
            @ApiImplicitParam(name = "secondarySubjectCode", value = "二级科目编码(长度,64)", required = true, dataType = "String"),
            @ApiImplicitParam(name = "belongingMonth", value = "所属月份", required = true, dataType = "String"),
            @ApiImplicitParam(name = "accumulatedExpenditure", value = "已累计支出(长度,18:2)", required = true, dataType = "String"),
            @ApiImplicitParam(name = "amount", value = "本次金额(长度,18:2)", required = true, dataType = "String")
    })
    public ResponseEntity queryBudgetExcessTips() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("projectApplyMainId"), "研发项目申请表主键ID",256);
        CheckParameter.stringLengthAndEmpty(pd.getString("secondarySubjectCode"), "二级科目编码",64);
        CheckParameter.stringLengthAndEmpty(pd.getString("belongingMonth"), "所属月份", 16);
        CheckParameter.checkDecimal(pd.getString("accumulatedExpenditure"), "已累计支出", 18,2);
        CheckParameter.checkDecimal(pd.getString("amount"), "本次金额", 18,2);
        ResponseEntity result = null;
        try {

            PageData pageData = itemExpensesService.queryBudgetExcessTips(pd);
            result = ResponseEntity.success(pageData, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
            return result;
        } catch (MyException e) {
            logger.error("查询累计支出总额是否超出该类预算5%失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


    @ApiOperation(value = "删除研发项目费用支出申请")
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
            itemExpensesService.deleteItemExpenses(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除研发项目费用支出申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "研发项目费用支出申请", pd);
        }
    }

    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询研发项目费用支出申请详情", notes = "查询研发项目费用支出申请详情")
    @ApiImplicitParam(name = "businessId", value = "业务主键businessId", required = true, dataType = "String")
    public ResponseEntity<ItemExpensesDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键businessId",64);
        try {

            PageData pageData = itemExpensesService.getItemExpensesDetail(pd);

            return PropertyUtil.pushData(pageData, ItemExpensesDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询研发项目费用支出详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @ApiOperation(value = "新增或编辑")
    @PostMapping("/saveOrUpdate")
    public ResponseEntity addUser(ItemExpensesDetailDto itemExpensesDetailDto) {
        PageData pd = this.getParams();
        saveCheckParam(pd);
        ResponseEntity result = null;
        try {

            itemExpensesService.saveOrUpdate(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增或编辑研发项目费用支出失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "研发项目费用支出", pd);
        }
    }

    @ApiOperation(value = "提交")
    @PostMapping("/submit")
    public ResponseEntity submit(ItemExpensesDetailDto itemExpensesDetailDto) {
        PageData pd = this.getParams();

        //1：列表提交，2：保存提交，3：编辑提交
        if (!pd.getString("flag").equals("1")) {
            //先保存或编辑校验
            submitCheckParam(pd);
        } else {
            PageData pageData = itemExpensesService.getItemExpensesDetail(pd);
            pageData.put("menuCode",pd.getString("menuCode"));
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

            itemExpensesService.submit(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("提交研发项目费用支出失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "研发项目费用支出", pd);
        }
    }

    @ApiOperation(value = "审批(同意/回退)")
    @PostMapping(value = "/approve")
    public ResponseEntity approve(FlowApproveDto flowApproveDto){
        PageData pd = this.getParams();
        checkApprove(pd);
        ResponseEntity result = null;
        try {
            itemExpensesService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("研发项目费用支出审批失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 8, "研发项目费用支出", pd);
        }
    }

    @ApiOperation(value = "废除（未开发）")
    @PostMapping(value = "/abolish")
    public ResponseEntity abolishRecord(FlowAbolishDto flowAbolishDto){
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            itemExpensesService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("研发项目费用支出废除失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 9, "研发项目费用支出", pd);
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
            HSSFWorkbook wb = itemExpensesService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "研发项目费用支出_" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            logUtil.saveLogData(result.getCode(), 6, "研发项目费用支出EXCEL", pd);
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
            String fileName =  "研发项目费用支出_" + date + ".pdf";

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
                        data  = itemExpensesService.getItemExpensesDetail(data);
                    }
                    //生成单个pdf
                    itemExpensesService.exportPDF(data, document);
                }
            } else {
                // 第一页
                if (listId.size() > 0) {
                    PageData data = new PageData();
                    data.put("businessId", listId.get(0));
                    if (data.size() == 1) {//导出选择部分
                        data  = itemExpensesService.getItemExpensesDetail(data);
                    }
                    itemExpensesService.exportPDF(data, document);
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
            logUtil.saveLogData(result.getCode(), 6, "研发项目费用支出PDF", pageData);
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
        if (pd.getString("businessIdOther").isEmpty() && pd.getString("businessId").isEmpty()){
            throw new MyException("其它模块的业务主键ID和本模块的业务主键ID不能同时为空！");
        }
    }

    /**
     * 提交时校验
     * @param pd
     */
    private void submitCheckParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("belongingMonth"), "所属月份", 16);
        CheckParameter.stringLengthAndEmpty(pd.getString("payNoted"), "支出依据", 1024);
        CheckParameter.stringLengthAndEmpty(pd.getString("firstSubject"), "研发项目支出一级科目", 64);
        CheckParameter.stringLength(pd.getString("secondarySubject"), "研发项目支出二级科目", 64);
        CheckParameter.checkDecimal(pd.getString("budgetAmount"), "预算总额", 18,2);
        CheckParameter.checkDecimal(pd.getString("accumulatedExpenditure"), "已累计支出", 18,2);
        CheckParameter.checkDecimal(pd.getString("budgetBalance"), "预算结余", 18,2);
        CheckParameter.checkDecimal(pd.getString("amount"), "本次金额", 18,2);

        CheckParameter.checkDecimal(pd.getString("balanceAmount"), "结余金额", 18,2);
        CheckParameter.stringLengthAndEmpty(pd.getString("remark"), "申报意见", 1024);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
        String belongingMonth = pd.getString("belongingMonth");
        CheckParameter.stringLengthAndEmpty(pd.getString("startYear"), "研发项目开始年份", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("endYear"), "研发项目结束年份", 256);
        String startYear = pd.getString("startYear");
        String endYear = pd.getString("endYear");
        try {
            if (df.parse(belongingMonth).before(df.parse(startYear)) || df.parse(belongingMonth).after(df.parse(endYear))){
                throw new MyException("所属月份不在研发项目的日期范围之内");
            }
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
        }
    }


    /**
     * 保存时校验
     * @param pd
     */
    private void saveCheckParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLength(pd.getString("projectName"), "项目名称", 256);
        CheckParameter.stringLength(pd.getString("payNoted"), "支出依据", 1024);
        CheckParameter.stringLength(pd.getString("remark"), "申报意见", 1024);
    }
}
