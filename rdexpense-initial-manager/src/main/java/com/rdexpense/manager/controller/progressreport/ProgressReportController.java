package com.rdexpense.manager.controller.progressreport;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.base.FlowApproveDto;
import com.rdexpense.manager.dto.base.UploadTemplateFileDto;
import com.rdexpense.manager.dto.itemClosureCheck.ItemClosureCheckDetailDto;
import com.rdexpense.manager.dto.progressreport.*;
import com.rdexpense.manager.dto.system.user.IdListDto;
import com.rdexpense.manager.enums.ApprovalstatusEnum;
import com.rdexpense.manager.service.file.FileService;
import com.rdexpense.manager.service.progressreport.ProgressReportService;
import com.rdexpense.manager.util.Date2CnDate;
import com.rdexpense.manager.util.FileParamsUtil;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @author dengteng
 * @title: ProgressReportController
 * @projectName rdexpense-back
 * @description: 研发项目进展报告
 * @date 2021/11/24
 */
@RestController
@RequestMapping("/progressreport")
@Api(value = "研发项目进展报告", tags = {"研发项目进展报告"})
public class ProgressReportController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProgressReportController.class);

    @Autowired
    private ProgressReportService progressReportService;

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private FileService fileService;

    /**
    * @Description:  查询进展报告列表
    * @Param: [progressReportSearchDTO]
    * @return: ResponseEntity<PageInfo<ProgressReportListDTO>>
    * @Author: dengteng
    * @Date: 2021/11/24
    */
    @PostMapping("/queryReportList")
    @ApiOperation(value = "查询进展报告列表", notes = "查询进展报告列表")
    public ResponseEntity<PageInfo<ProgressReportListDTO>> queryReportList(ProgressReportSearchDTO progressReportSearchDTO){
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = progressReportService.queryReportList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ProgressReportListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询人员列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @PostMapping("/queryDetail")
    @ApiOperation(value = "我的待办查询进展报告详情", notes = "我的待办查询进展报告详情")
    @ApiImplicitParam(name = "businessId", value = "业务主键businessId", required = true, dataType = "String")
    public ResponseEntity<ProgressReportDetailDTO> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键businessId",64);
        try {

            PageData pageData = progressReportService.getDetailByBusinessid(pd);

            return PropertyUtil.pushData(pageData, ProgressReportDetailDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询项目立项申请详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    /**
    * @Description:  新增进展报告
    * @Param: [progressReportAddDTO]
    * @return: com.common.entity.ResponseEntity
    * @Author: dengteng
    * @Date: 2021/11/26
    */
    @PostMapping("/addReport")
    @ApiOperation(value = "新增进展报告", notes = "新增进展报告")
    public ResponseEntity addReport(ProgressReportAddDTO progressReportAddDTO){
        PageData pd = this.getParams();

        String saveStaus = pd.getString("saveStaus");
        // 0保存 1提交
        if (("0").equals(saveStaus) ) {

        } else {
            checkParam(pd);
        }

        ResponseEntity result = null;

        try {
            progressReportService.addReport(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增用户信息失败,request=[{}]",pd);
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "用户信息", pd);
        }
    }



    @PostMapping("/submitReport")
    @ApiOperation(value = "提交项目进展报告", notes = "提交项目进展报告")
    public ResponseEntity<ProgressReportDetailDTO> submitReport(IdListDto idListDto){
        PageData pd = this.getParams();

        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("主键ID不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            progressReportService.submitReport(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("提交项目进展报告,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SUBMIT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目进展报告", pd);
        }
    }

    @ApiOperation(value = "审批(同意/回退)")
    @PostMapping(value = "/approve")
    public ResponseEntity approve(FlowApproveDto flowApproveDto){
        PageData pd = this.getParams();
        checkApprove(pd);
        ResponseEntity result = null;
        try {
            progressReportService.approveRecord(pd);
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


    /**
    * @Description:  查询单条项目进展报告详情
    * @Param: []
    * @return: ResponseEntity<ProgressReportDetailDTO>
    * @Author: dengteng
    * @Date: 2021/11/26
    */
    @PostMapping("/getReportDetail")
    @ApiOperation(value = "查询单条项目进展报告详情", notes = "查询单条项目进展报告详情")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String")
    public ResponseEntity<ProgressReportDetailDTO> getReportDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID", 64);
        try {
            PageData pageData = progressReportService.getReportDetail(pd);
            return PropertyUtil.pushData(pageData, ProgressReportDetailDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询用户详情,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


    /**
    * @Description:  修改项目进展报告
    * @Param: [progressreportUpdateDTO]
    * @return: com.common.entity.ResponseEntity
    * @Author: dengteng
    * @Date: 2021/11/27
    */
    @PostMapping("/updateReport")
    @ApiOperation(value = "修改项目进展报告", notes = "修改项目进展报告")
    public ResponseEntity updateReport(ProgressreportUpdateDTO progressreportUpdateDTO){
        PageData pd = this.getParams();
//        checkParam(pd);

        ResponseEntity result = null;
        try {

            progressReportService.updateReport(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("修改用户失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "用户信息", pd);
        }
    }


    @ApiOperation(value = "删除项目进展报告")
    @PostMapping("/deleteReport")
    public ResponseEntity deleteReport(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String businessIdList = pd.getString("businessIdList");
        if (StringUtils.isBlank(businessIdList)) {
            throw new MyException("业务主键businessId不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            progressReportService.deleteReport(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除用户失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "项目进展报告", pd);
        }

    }

    @ApiOperation(value = "废除项目进展报告")
    @PostMapping("/repealedReport")
    public ResponseEntity repealedReport(IdListDto idListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("主键ID不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            progressReportService.repealedReport(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("废除项目进展报告,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SUBMIT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SUBMIT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "项目进展报告", pd);
        }

    }


    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(IdListDto idListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        //取出菜单id
        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        try {
            HSSFWorkbook wb = progressReportService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "项目研发进展报告_" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            logUtil.saveLogData(result.getCode(), 6, "用户管理EXCEL", pd);
        }
    }



    @ApiOperation(value = "导出项目进展报告PDF", notes = "导出项目进展报告PDF")
    @PostMapping(value = "/exportPDF")
    @ApiImplicitParam(name = "idList", value = "业务主键id", required = true, dataType = "list")
    public Object exportContract() {
        PageData pd = this.getParams();
        HttpServletRequest httpServletRequest = this.getRequest();
        ResponseEntity result = null;
        try {
            List<PageData> reportDetailList = progressReportService.getReportDetailList(pd);
                result = shieldContractExport(reportDetailList.get(0));
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "导出项目进展报告pdf", pd);
        }
    }

    @ApiOperation(value = "打印", notes = "打印")
    @PostMapping(value = "/exportPrint")
    public Object exportPrint(IdListDto idListDto) {
        PageData pd = this.getParams();
        HttpServletRequest httpServletRequest = this.getRequest();
        HttpServletResponse httpServletResponse = this.getResponse();
        ResponseEntity result = null;
        try {
            result = progressReportService.export2(pd,httpServletResponse);
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "导出项目进展报告pdf", pd);
        }
    }


    @ApiOperation(value = "导出压缩包")
    @PostMapping(value = "/export")
    public ResponseEntity export(IdListDto idListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        HttpServletResponse response = this.getResponse();
        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList= pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String serialNumber = SerialNumberUtil.generateSerialNo("planarSectionalViews");

        //文件名
        String fileName = "研发项目进展报告_"+date+"_"+ serialNumber +".zip";
        //输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 压缩流
        ZipOutputStream zos = new ZipOutputStream(bos);
        try {

            //生成excel
            progressReportService.export(pd, zos, bos,serialNumber);
            zos.flush();
            zos.close();
            //写入返回response
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(bos.toByteArray());
            out.flush();
            out.close();
            result = ResponseEntity.success(ConstantMsgUtil.INFO_EXPORT_SUCCESS.val(), ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            logger.error("项目研发进展报告--导出PDf,request=[{}]", pd);
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目研发进展报告", pd);
        }
    }


    @ApiOperation(value = "下载模板")
    @GetMapping(value = "/downloadTemplate")
    public void saveFile(){
        HttpServletResponse response = this.getResponse();
        String name = "进展报告导入模板.xlsx";
        String path = this.getClass().getClassLoader().getResource("template/").getPath();
        try {
            DownloadUtil.download(response,name,path,name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "导入模板")
    @PostMapping("/upload")
    public ResponseEntity upload(UploadTemplateFileDto dto) {

        PageData pd = FileParamsUtil.checkParams(dto);

        ResponseEntity result = null;
        try {
            progressReportService.uploadSurvey(dto.getFile(),pd);
            result = ResponseEntity.success(pd, INFO_IMPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("上传模板,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 9, "研发项目进展报告模板", pd);
        }

    }


    /**
     *
     *
     * @param data
     * @return
     */
    private ResponseEntity shieldContractExport(PageData data) {
        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        XWPFDocument doc;
        InputStream is = null;
        HttpServletResponse response = this.getResponse();
        File file = progressReportService.createFile();

        List<PageData> attachment = fileService.query(data);

        try {
            //获取文件路径
            logger.info("==============获取文件路径================");
            is = this.getClass().getResourceAsStream("/template/进展报告导出.docx");
            doc = new XWPFDocument(is);
            if (data.size() > 0) {
                String taxRate = "";
                //文本
                Map<String, Object> params = new HashMap<>();

                //单据编号
                params.put("(serialNumber)", data.getString("serialNumber"));
                //编制人
                params.put("(creatorUserName)", data.getString("creatorUserName"));
                //创建日期
                params.put("(createdDate)", data.getString("createdDate"));
                //项目名称
                params.put("(projectName)", data.getString("projectName"));
                //所属单位名称
                params.put("(unitName)", data.getString("unitName"));
                //项目负责人
                params.put("(projectLeaderName)", data.getString("projectLeaderName"));
                //联系电话
                params.put("(contactNumber)", data.getString("contactNumber"));
                //岗位
                params.put("(leaderPostName)", data.getString("leaderPostName"));
                //起始年度
                params.put("(startDate)", data.getString("startDate"));
                //结束年度
                params.put("(endDate)", data.getString("endDate"));
                //报告人
                params.put("(reporterName)", data.getString("reporterName"));
                //报告日期
                String reporterDate = data.getString("reporterDate");
                if(StringUtils.isNotBlank(reporterDate) && reporterDate.length()>4){
                    reporterDate = data.getString("reporterDate").substring(0,4);
                }
                params.put("(reporterDate)", reporterDate);
                //项目概况
                params.put("(projectOverview)", data.getString("projectOverview"));
                //研发过程
                params.put("(developmentProcess)", data.getString("developmentProcess"));
                //关键技术及创新
                params.put("(keyTechnology)", data.getString("keyTechnology"));
                //取得的阶段性成果
                params.put("(achieveResults)", data.getString("achieveResults"));
                //经济效益和社会、环保效益
                params.put("(beneficialResult)", data.getString("beneficialResult"));
                //成果报告说明
                params.put("(reportDescription)", data.getString("reportDescription"));

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String date = df.format(new Date());
                params.put("(now)", Date2CnDate.convertDate2CnDate(date));

                //替换文字
                xwpfTUtil.replaceInPara(doc, params);

                //插入附件清单
                if(attachment.size() > 0){
                    XWPFTable table1 = doc.createTable(attachment.size()+1, 4);
                    CTTblPr tblPr = table1.getCTTbl().getTblPr();
                    tblPr.getTblW().setType(STTblWidth.DXA);
                    tblPr.getTblW().setW(new BigInteger("7000"));
                    // 获取到刚刚插入的行
                    XWPFTableRow row1 = table1.getRow(0);
                    // 设置单元格内容
                    row1.getCell(0).setText("序号");
                    row1.getCell(1).setText("附件名称");
                    row1.getCell(2).setText("附件大小");
                    row1.getCell(3).setText("上传人");

                    for (int i = 1; i <= attachment.size(); i++) {
                        // 获取到刚刚插入的行
                        XWPFTableRow row = table1.getRow(i);
                        row.setCantSplitRow(true);
                        row.setRepeatHeader(true);
                        // 设置单元格内容
                        row.getCell(0).setText(i+"");
                        row.getCell(1).setText(attachment.get(i-1).getString("fileName"));
                        row.getCell(2).setText(attachment.get(i-1).getString("fileSize"));
                        row.getCell(3).setText(attachment.get(i-1).getString("uploadUserName"));
                    }
                    doc.setTable(0, table1);


                }

                //  在默认文件夹下创建临时文件
                //prefix :临时文件名的前缀,   suffix :临时文件名的后缀
                String fileName = "研发项目进展报告" + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
                File newWordTempFile = new File(file, fileName + ".docx");
                String newWordTempFilePath = newWordTempFile.getCanonicalPath();
                File pdfTempFile = new File(file, fileName + ".pdf");
                String pdfTempFilePath = pdfTempFile.getCanonicalPath();

                //下载到临时word文件中
                FileOutputStream os = new FileOutputStream(newWordTempFilePath);
                doc.write(os);
                xwpfTUtil.close(is);
                xwpfTUtil.close(os);
                //将word转为pdf
                Doc2Pdf.doc2pdf(newWordTempFilePath, pdfTempFilePath);
                //输出pdf文件
                Word2pdfUtil.fileResponse(pdfTempFilePath, response);
                //删除临时文件
                newWordTempFile.delete();
                pdfTempFile.delete();
                return ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            } else {
                return ResponseEntity.failure(WAN_EXPORT_DETAIL_EMPTY.val(), WAN_EXPORT_DETAIL_EMPTY.desc());
            }
        } catch (Exception e) {
            e.printStackTrace();
            xwpfTUtil.close(is);
            logger.error("项目研发进展报告--导出报告异常");
            return ResponseEntity.failure(ERR_EXPORT_FAIL.val(), ERR_EXPORT_FAIL.desc());
        }
    }



    /**
    * @Description:  校验新增进展报告
    * @Param: [pd]
    * @return: void
    * @Author: dengteng
    * @Date: 2021/11/24
    */
    private void checkParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringIsNull(pd.getString("projectName"), "项目名称");
        CheckParameter.stringIsNull(pd.getString("contactNumber"), "联系电话");
        CheckParameter.stringIsNull(pd.getString("startDate"), "起始年度");
        CheckParameter.stringIsNull(pd.getString("endDate"), "结束年度");
        CheckParameter.stringIsNull(pd.getString("reporterName"), "报告人");
        CheckParameter.stringIsNull(pd.getString("reporterDate"), "报告日期");
        CheckParameter.stringIsNull(pd.getString("projectOverview"), "项目概况");
        CheckParameter.stringIsNull(pd.getString("developmentProcess"), "研发过程");
        CheckParameter.stringIsNull(pd.getString("keyTechnology"), "关键技术及创新");
        CheckParameter.stringIsNull(pd.getString("achieveResults"), "取得的阶段性成果");
        CheckParameter.stringIsNull(pd.getString("beneficialResult"), "经济效益和社会、环保效益");
        CheckParameter.stringIsNull(pd.getString("reportDescription"), "成果报告说明");
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



}
