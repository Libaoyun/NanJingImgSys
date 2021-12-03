package com.rdexpense.manager.controller.itemClosureCheck;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.base.FlowAbolishDto;
import com.rdexpense.manager.dto.base.FlowApproveDto;
import com.rdexpense.manager.dto.itemClosureCheck.ItemClosureCheckDetailDto;
import com.rdexpense.manager.dto.itemClosureCheck.ItemClosureCheckListDto;
import com.rdexpense.manager.dto.itemClosureCheck.ItemClosureProjectApplyListDto;
import com.rdexpense.manager.dto.projectApply.ProjectApplyListDto;
import com.rdexpense.manager.dto.projectApply.ProjectApplySearchDto;
import com.rdexpense.manager.dto.projectApply.ResearchUserDto;
import com.rdexpense.manager.dto.system.user.IdListDto;
import com.rdexpense.manager.dto.system.user.UserAddDto;
import com.rdexpense.manager.service.itemClosureCheck.ItemClosureCheckService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
import static com.common.util.ConstantMsgUtil.ERR_EXPORT_FAIL;

@RestController
@RequestMapping("/itemClosureCheck")
@Api(value = "研发项目结项验收", tags = "研发项目结项验收")
public class ItemClosureCheckController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ItemClosureCheckController.class);

    @Autowired
    private ItemClosureCheckService itemClosureCheckService;

    @Autowired
    private BaseDao dao;

    @Autowired
    private LogUtil logUtil;

    @PostMapping("/queryItemClosureCheckList")
    @ApiOperation(value = "查询研发项目结题验收列表", notes = "查询研发项目结题验收列表")
    public ResponseEntity<PageInfo<ItemClosureCheckListDto>> queryUserList(ItemClosureCheckListDto itemClosureCheckListDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = itemClosureCheckService.queryItemClosureCheckList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ItemClosureCheckListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询研发项目结题验收列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryApplyList")
    @ApiOperation(value = "查询已审批完成的研发项目申请列表", notes = "查询已审批完成的研发项目申请列表")
    public ResponseEntity<PageInfo<ItemClosureProjectApplyListDto>> queryApplyList(ItemClosureProjectApplyListDto itemClosureProjectApplyListDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = itemClosureCheckService.queryProjectApplyList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ItemClosureProjectApplyListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询已审批完成的研发项目申请列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryApplyUserList")
    @ApiOperation(value = "查询已审批完成的研发项目的人员信息列表", notes = "查询已审批完成的研发项目的人员信息列表")
    @ApiImplicitParam(name = "id", value = "研发项目申请表ID", required = true, dataType = "String")
    public ResponseEntity<PageInfo<ResearchUserDto>> queryApplyUserList() {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = itemClosureCheckService.queryApplyUserList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ResearchUserDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询已审批完成的研发项目的人员信息列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @ApiOperation(value = "删除研发项目结题验收申请")
    @PostMapping("/delete")
    public ResponseEntity deleteApply(IdListDto idListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("主键ID不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            itemClosureCheckService.deleteItemClosure(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除研发项目结题验收申请失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "研发项目结题验收申请", pd);
        }
    }

    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询研发项目结题验收申请详情", notes = "查询研发项目结题验收申请详情")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String")
    public ResponseEntity<ItemClosureCheckDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",64);
        try {

            PageData pageData = itemClosureCheckService.getItemClosureCheckDetail(pd);

            return PropertyUtil.pushData(pageData, ItemClosureCheckDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询项目立项申请详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    @ApiOperation(value = "新增或编辑")
    @PostMapping("/saveOrUpdate")
    public ResponseEntity addUser(ItemClosureCheckDetailDto itemClosureCheckDetailDto) {
        PageData pd = this.getParams();
        saveCheckParam(pd);
        ResponseEntity result = null;
        try {

            itemClosureCheckService.saveOrUpdate(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增或编辑研发项目结题验收失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "研发项目结题验收", pd);
        }
    }

    @ApiOperation(value = "提交")
    @PostMapping("/submit")
    public ResponseEntity submit(ItemClosureCheckDetailDto itemClosureCheckDetailDto) {
        PageData pd = this.getParams();

        //1：列表提交，2：保存提交，3：编辑提交
        if (!pd.getString("flag").equals("1")) {
            //先保存或编辑校验
            submitCheckParam(pd);
        } else {
            //列表提交校验
            submitCheckParam(pd);
            CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",64);
        }

        ResponseEntity result = null;
        try {

            itemClosureCheckService.submit(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("提交研发项目结题验收失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "研发项目结题验收", pd);
        }
    }

    @ApiOperation(value = "审批(同意/回退)")
    @PostMapping(value = "/approve")
    public ResponseEntity approveRecord(FlowApproveDto flowApproveDto){
        PageData pd = this.getParams();
        checkApprove(pd);
        ResponseEntity result = null;
        try {
            itemClosureCheckService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("研发项目结题验收审批失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 8, "研发项目结题验收", pd);
        }
    }

    @ApiOperation(value = "废除（未开发）")
    @PostMapping(value = "/abolish")
    public ResponseEntity abolishRecord(FlowAbolishDto flowAbolishDto){
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        ResponseEntity result = null;
        try {
            itemClosureCheckService.approveRecord(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_APPROVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_APPROVE_FAIL.val(), e.getMessage());
            logger.error("研发项目结题验收废除失败,request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_APPROVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 9, "研发项目结题验收", pd);
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
            HSSFWorkbook wb = itemClosureCheckService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "研发项目结题验收_" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            logUtil.saveLogData(result.getCode(), 6, "研发项目结题验收EXCEL", pd);
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
            String fileName =  "研发项目结题验收_" + date + ".pdf";

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
                        data  = itemClosureCheckService.getItemClosureCheckDetail(data);
                    }
                    //生成单个pdf
                    itemClosureCheckService.exportPDF(data, document);
                }
            } else {
                // 第一页
                if (listId.size() > 0) {
                    PageData data = new PageData();
                    data.put("businessId", listId.get(0));
                    if (data.size() == 1) {//导出选择部分
                        data  = itemClosureCheckService.getItemClosureCheckDetail(data);
                    }
                    itemClosureCheckService.exportPDF(data, document);
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
            logUtil.saveLogData(result.getCode(), 6, "研发项目结题验收PDF", pageData);
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
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("jobTitle"), "成果名称", 256);
        CheckParameter.stringLength(pd.getString("taskSource"), "任务来源", 1024);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectAbstract"), "成果内容简介", 1024);
        CheckParameter.stringLengthAndEmpty(pd.getString("directoryAndUnit"), "经济技术文件目录及提供单位", 1024);
        CheckParameter.stringLength(pd.getString("checkRemark"), "申请评审单位意见", 1024);
    }


    /**
     * 保存时校验
     * @param pd
     */
    private void saveCheckParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLength(pd.getString("jobTitle"), "成果名称", 256);
        CheckParameter.stringLength(pd.getString("taskSource"), "任务来源", 1024);
        CheckParameter.stringLength(pd.getString("projectAbstract"), "成果内容简介", 1024);
        CheckParameter.stringLength(pd.getString("directoryAndUnit"), "经济技术文件目录及提供单位", 1024);
        CheckParameter.stringLength(pd.getString("checkRemark"), "申请评审单位意见", 1024);
    }
}
