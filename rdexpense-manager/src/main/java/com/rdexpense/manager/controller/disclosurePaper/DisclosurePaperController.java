package com.rdexpense.manager.controller.disclosurePaper;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.disclosurePaper.*;
import com.rdexpense.manager.dto.system.user.IdListDto;
import com.rdexpense.manager.service.disclosurePaper.DisclosurePaperService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.util.ConstantMsgUtil.*;

@RestController
@RequestMapping("/disclosure")
@Api(value = "项目交底书", tags = "项目交底书")
public class DisclosurePaperController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DisclosurePaperController.class);

    @Autowired
    private DisclosurePaperService disclosurePaperService;

    @Autowired
    private LogUtil logUtil;


    //分页查询
    @PostMapping("/queryDisclosureList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<DisclosurePaperListDTO>> queryUserList(DisclosurePaperSearchDTO disclosurePaperSearchDTO) {
        PageData pd = this.getParams();
       try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = disclosurePaperService.queryDisclosurePaperList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, DisclosurePaperListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e){
           logger.error("查询交底书列表,request=[{}]",pd);
           return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
       }

    }


    //新增
    @ApiOperation(value = "新增项目交底书")
    @PostMapping(value ="/addDisclosurePaper")
    public ResponseEntity addDisclosurePaper(DisclosurePaperAddDTO disclosurePaperAddDTO) {
        PageData pd = this.getParams();

        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("contactNumber"), "联系电话", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("preparedName"), "交底书编制人", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("preparedDate"), "编制日期", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("preparedYear"), "编制年度", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("preparedQuarter"), "编制季度", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("keyTechnology"), "核心技术", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("workPlan"), "工作安排", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("artificialPlan"), "人工安排", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("equipmentPlan"), "设备安排", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("costPlan"), "费用安排", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("testPlan"), "测试安排", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("otherPlan"), "其他安排", 128);

        ResponseEntity result = null;
        try {
            disclosurePaperService.addDisclosurePaper(pd);
            result = ResponseEntity.success(null,ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        }catch (Exception e){
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_FAIL.val(), e.getMessage());
            logger.error("新增项目失败，request=[{}]",pd);
            throw new MyException(ERR_SAVE_FAIL.desc(),e);
        }finally {
            logUtil.saveLogData(result.getCode(),1,"交底书",pd);
        }
    }



    //删除项目交底书
    @ApiOperation(value = "删除项目")
    @PostMapping(value = "/deleteDisclosurePaper", consumes = "application/json")
    public ResponseEntity deleteDisclosurePaper(IdListDto idListDto){
        PageData pd = this.getParams();
//        CheckParameter.checkDefaultParams(pd);
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)){
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        ResponseEntity result = null;
        try {
            disclosurePaperService.deleteDisclosurePaper(pd);
            result = ResponseEntity.success(null,INFO_DELETE_SUCCESS.desc());
            return result;
        }catch(Exception e){
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(),e.getMessage());
            logger.error("删除项目交底书，request=[{}]",pd);
            throw new MyException(ERR_DELETE_FAIL.desc(),e);
        }finally {
            logUtil.saveLogData(result.getCode(),3,"项目交底书",pd);
        }
    }

    //编辑项目交底书
    @ApiOperation(value = "编辑项目交底书")
    @PostMapping(value = "/updateDisclosurePaper", consumes = "application/json")
    public ResponseEntity updateDisclosurePaper(DisclosurePaperUpdateDTO DisclosurePaperUpdateDTO) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称",128);

        ResponseEntity result = null;
        try {
            disclosurePaperService.updateDisclosurePaper(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
    }catch (Exception e){
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑项目交底书失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        }finally{
            logUtil.saveLogData(result.getCode(), 2, "项目", pd);
        }
    }



    //查询用户详情
    @PostMapping("/getDisclosurePaperDetail")
    @ApiOperation(value = "查询项目交底书详情", notes = "查询项目交底书详情")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String")
    public ResponseEntity<DisclosurePaperDetailDTO> getDisclosurePaperDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID", 64);
        try {
            PageData pageData = disclosurePaperService.getDisclosurePaperDetail(pd);
            return PropertyUtil.pushData(pageData, DisclosurePaperDetailDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询项目交底书详情,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }





    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(IdListDto idListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        //取出菜单id
//        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        try {
            HSSFWorkbook wb = disclosurePaperService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("DisclosurePaperExcel");
            String fileName = "项目交底书管理" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            logUtil.saveLogData(result.getCode(), 6, "项目交底书管理EXCEL", pd);
        }
    }



    @ApiOperation(value = "导出项目交底书PDF", notes = "导出项目交底书PDF")
    @PostMapping(value = "/exportPDF")
    @ApiImplicitParam(name = "idList", value = "业务主键id", required = true, dataType = "list")
    public Object exportContract() {
        PageData pd = this.getParams();
        HttpServletRequest httpServletRequest = this.getRequest();
        ResponseEntity result = null;
        try {
            List<PageData> DisclosurePaperDetailList = disclosurePaperService.getDisclosurePaperDetailList(pd);
            if (DisclosurePaperDetailList.size() > 1) {
                // 多条记录
                return null;
            }else {
                // 单条记录
                result = shieldContractExport(DisclosurePaperDetailList.get(0));
            }
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "导出项目交底书pdf", pd);
        }
    }



    private ResponseEntity shieldContractExport(PageData data) {
        XwpfTUtil xwpfTUtil = new XwpfTUtil();
        XWPFDocument doc;
        InputStream is = null;
        HttpServletResponse response = this.getResponse();
        File file = disclosurePaperService.createFile();

        try {
            //获取文件路径
            logger.info("==============获取文件路径================");
            is = this.getClass().getResourceAsStream("/template/技术经济交底书.docx");
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
                //交底书编制人
                params.put("(preparedName)", data.getString("preparedName"));
                //编制日期
                params.put("(preparedDate)", data.getString("preparedDate"));
                //编制年度
                params.put("(preparedYear)", data.getString("preparedYear"));
                //编制季度
                params.put("(preparedQuarter)", data.getString("preparedQuarter"));
                //核心技术
                params.put("(keyTechnology)", data.getString("keyTechnology"));
                //工作安排
                params.put("(workPlan)", data.getString("workPlan"));
                //忍冬安排
                params.put("(artificialPlan)", data.getString("artificialPlan"));
                //设备安排
                params.put("(equipmentPlan)", data.getString("equipmentPlan"));
                //费用安排
                params.put("(costPlan)", data.getString("costPlan"));
                //成果报告说明
                params.put("(testPlan)", data.getString("testPlan"));
                //其他安排
                params.put("(otherPlan)", data.getString("otherPlan"));

                //替换文字
                xwpfTUtil.replaceInPara(doc, params);

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
                return ResponseEntity.failure(ConstantMsgUtil.WAN_EXPORT_DETAIL_EMPTY.val(), ConstantMsgUtil.WAN_EXPORT_DETAIL_EMPTY.desc());
            }
        } catch (Exception e) {
            e.printStackTrace();
            xwpfTUtil.close(is);
            logger.error("项目研发进展报告--导出报告异常");
            return ResponseEntity.failure(ERR_EXPORT_FAIL.val(), ERR_EXPORT_FAIL.desc());
        }
    }
}





