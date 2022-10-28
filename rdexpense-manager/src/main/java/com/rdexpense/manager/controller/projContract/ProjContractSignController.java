package com.rdexpense.manager.controller.projContract;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.common.util.SerialNumberUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.projContract.*;
import com.rdexpense.manager.dto.projectApply.ProjectApplyListDto;
import com.rdexpense.manager.service.projectApply.ProjApplyMainService;
import com.rdexpense.manager.service.projContract.ProjContractSignService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static com.common.util.ConstantMsgUtil.*;
import static com.common.util.ConstantMsgUtil.ERR_QUERY_FAIL;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2021/11/25 22:07
 */

@RestController
@RequestMapping("/projContractSign")
@Api(value = "项目合同签订", tags = "项目合同签订")
public class ProjContractSignController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ProjContractSignController.class);

    @Autowired
    private ProjContractSignService projContractSignService;

    @Autowired
    private ProjApplyMainService projApplyMainService;

    @Autowired
    private LogUtil logUtil;

    @PostMapping("/querProjectList")
    @ApiOperation(value = "查询项目列表", notes = "查询项目列表")
    public ResponseEntity<PageInfo<ProjApplyMainListDto>>
        querProjectList(ProjApplyMainListDto projApplyMainListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = projApplyMainService.queryProjApplyMainList(pd);

            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            result = ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ProjApplyMainListDto.class),
                    ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("查询项目列表,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目信息", pd);
        }
    }

    @ApiOperation(value = "确认项目选择")
    @PostMapping("/commitProjSelection")
    public ResponseEntity<ProjApplyMainSearchDto> commitProjectSelection(ProjApplyMainListDto projApplyMainListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        //校验取出参数
        String idString = pd.getString("businessId");
        if (StringUtils.isBlank(idString)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        try {
            PageData pageData = projApplyMainService.queryByBusinessId(pd);
            result = PropertyUtil.pushData(pageData, ProjApplyMainListDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("项目选择确认,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目信息选择", pd);
        }
    }

    @ApiOperation(value = "新增项目合同")
    @PostMapping("/addContract")
    public ResponseEntity addProjContract(ProjContractAddDto projContractAddDto) {
        PageData pd = this.getParams();
        //checkParam(pd);
        ResponseEntity result = null;

        try {
            projContractSignService.addProjContract(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("添加项目合同失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目合同信息", pd);
        }
    }

    @ApiOperation(value = "更新项目合同")
    @PostMapping("/updateContract")
    public ResponseEntity updateProjContract(ProjContractAddDto projContractAddDto) {
        PageData pd = this.getParams();
        //CheckParameter.checkDefaultParams(pd);
        //CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",128);
        //CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称",128);

        //删除后重新添加
        ResponseEntity result = null;
        try {
            projContractSignService.updateProjContract(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑项目失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "项目合同信息", pd);
        }
    }

    @ApiOperation(value = "删除项目合同")
    @PostMapping("/deleteContract")
    public ResponseEntity deleteProjContract(ProjContractAddDto projContractAddDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("主键ID不能为空");
        }
        //CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            projContractSignService.deleteProjContract(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除项目合同失败, request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "项目合同信息", pd);
        }
    }

    @PostMapping("/queryProjContractList")
    @ApiOperation(value = "查询项目合同列表", notes = "查询项目合同列表")
    public ResponseEntity<PageInfo<ProjContractListDto>>
        querProjectContractList(ProjContractSearchDto projContractSearchDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;

        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = projContractSignService.queryProjContractList(pd);

            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            result = ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ProjContractListDto.class),
                    ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("查询项目列表,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目信息", pd);
        }
    }

    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(IdProjContractListDto idProjContractListDto) {
        PageData pd = this.getParams();

        ResponseEntity result = null;

        //取出菜单id
        CheckParameter.checkDefaultParams(pd);

        //校验取出参数
        String businessIdList = pd.getString("businessIdList");
        if (StringUtils.isBlank(businessIdList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式

        try {
            HSSFWorkbook wb = projContractSignService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("projContractExcel");
            String fileName = "项目合同签订_" + df.format(new Date()) + "_" + serialNumber + ".xls";
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
            logUtil.saveLogData(result.getCode(), 5, "项目合同签订信息Excel", pd);
        }
    }

    @ApiOperation(value = "导出Pdf", notes = "导出pdf")
    @PostMapping(value = "/exportZip")
    public ResponseEntity exportZip(IdProjContractListDto idProjContractListDto) {
        PageData pd = this.getParams();

        ResponseEntity result = null;
        HttpServletResponse response = this.getResponse();
        CheckParameter.checkDefaultParams(pd);

        //校验取出参数
        //String idString = pd.getString("businessIdList");
        String idString = pd.getString("idList");
        if (StringUtils.isBlank(idString)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());
        String serialNumber = SerialNumberUtil.generateSerialNo("planarSectionalViews");

        //文件名
        String fileName = "项目合同签订管理_" + date + "_" + serialNumber + ".zip";
        //输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 压缩流
        ZipOutputStream zos = new ZipOutputStream(bos);

        try {
            projContractSignService.exportContractZip(pd, zos, bos, serialNumber);
            zos.flush();
            zos.close();

            //写入返回response
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" +
                    URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(bos.toByteArray());
            out.flush();
            out.close();
            result = ResponseEntity.success(ConstantMsgUtil.INFO_EXPORT_SUCCESS.val(),
                    ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch ( Exception e ) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            logger.error("项目合同签订管理--导出pdf, request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目合同签订信息pdf", pd);
        }
    }

    @ApiOperation(value = "导出合同", notes = "导出合同")
    @PostMapping(value = "/exportContractPdf")
    @ApiImplicitParam(name = "businessId", value = "业务主键id", required = true, dataType = "string")
    public Object exportContractPdf(IdProjContractListDto idProjContractListDto) {
        PageData pd = this.getParams();

        ResponseEntity result = null;
        HttpServletResponse response = this.getResponse();
        CheckParameter.checkDefaultParams(pd);

        //校验取出参数
        //String idString = pd.getString("businessIdList");
        String idString = pd.getString("idList");
        if (StringUtils.isBlank(idString)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());
        String serialNumber = SerialNumberUtil.generateSerialNo("YFHT");

        //文件名
        String fileName = "项目合同签订管理_" + date + "_" + serialNumber + ".pdf";

        //输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            projContractSignService.exportContractPdf(pd, bos, serialNumber);
            bos.flush();
            bos.close();

            //写入返回response
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" +
                    URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(bos.toByteArray());
            out.flush();
            out.close();
            result = ResponseEntity.success(ConstantMsgUtil.INFO_EXPORT_SUCCESS.val(),
                    ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch ( Exception e ) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            logger.error("项目合同签订管理--导出pdf, request=[{}]", pd);
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目合同签订信息pdf", pd);
        }
    }


    @ApiOperation(value = "打印合同", notes = "打印合同")
    @PostMapping(value = "/printContractPdf")
    @ApiImplicitParam(name = "businessId", value = "业务主键id", required = true, dataType = "string")
    public Object printContractPdf(IdProjContractListDto idProjContractListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        HttpServletResponse response = this.getResponse();
        HttpServletRequest request = this.getRequest();

        //校验取出参数
        String idString = pd.getString("idList");
        if (StringUtils.isBlank(idString)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }

        try {
            String serialNumber = SerialNumberUtil.generateSerialNo("YFHT");
            result = projContractSignService.printContractPdf(pd, serialNumber, response);
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 7, "打印项目合同pdf", pd);
        }
    }
}
