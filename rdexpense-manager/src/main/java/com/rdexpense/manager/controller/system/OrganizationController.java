package com.rdexpense.manager.controller.system;

import com.alibaba.fastjson.JSONArray;
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
import com.rdexpense.manager.dto.system.organization.*;
import com.rdexpense.manager.dto.system.user.IdListDto;
import com.rdexpense.manager.service.system.OrganizationService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @author luxiangbao
 * @date 2021/11/12 11:44
 * @description 项目管理
 */
@RestController
@RequestMapping("/organization")
@Api(value = "项目管理", tags = "项目管理")
public class OrganizationController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private OrganizationService organizationService;


    @PostMapping("/queryOrganizationList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<OrganizationListDTO>> queryUserList(OrganizationQueryDTO organizationQueryDTO) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = organizationService.queryOrganizationList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, OrganizationListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询项目列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "新增项目")
    @PostMapping(value = "/addOrganization", consumes = "application/json")
    public ResponseEntity addOrganization(OrganizationAddDTO projectAddDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "项目名称",128);

        ResponseEntity result = null;
        try {
            organizationService.addOrganization(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增项目失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "项目", pd);
        }
    }


    @ApiOperation(value = "编辑项目")
    @PostMapping(value = "/updateOrganization", consumes = "application/json")
    public ResponseEntity updateOrganization(OrganizationUpdateDTO projectUpdateDTO) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("orgName"), "项目名称",128);

        ResponseEntity result = null;
        try {
            organizationService.updateOrganization(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑项目失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "项目", pd);
        }
    }



    @ApiOperation(value = "删除项目")
    @PostMapping(value = "/deleteOrganization", consumes = "application/json")
    public ResponseEntity deleteSecondOrgNode(IdListDto idListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        ResponseEntity result = null;
        try {
            organizationService.deleteOrganization(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除项目,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "项目", pd);
        }
    }



    @PostMapping("/getOrganizationDetail")
    @ApiOperation(value = "查询项目详情", notes = "查询项目详情")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String")
    public ResponseEntity<OrganizationDetailDTO> getOrganizationDetail() {
        PageData pd = this.getParams();
        try {
            PageData pageData = organizationService.getOrganizationDetail(pd);
            return PropertyUtil.pushData(pageData, OrganizationDetailDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询项目详情,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
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
            HSSFWorkbook wb = organizationService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("projectExcel");
            String fileName = "项目管理_" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            logUtil.saveLogData(result.getCode(), 6, "项目管理EXCEL", pd);
        }
    }


    @ApiOperation(value = "导出PDF")
    @PostMapping(value = "/exportPDF")
    public ResponseEntity exportPDF(IdListDto idListDto) {
        PageData pd = this.getPageDataJson();
        ResponseEntity result = null;
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException(ConstantMsgUtil.ERR_NO_EMPTY.desc());
        }
        //取出菜单id
        CheckParameter.checkDefaultParams(pd);

        try {
            HttpServletResponse response = this.getResponse();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String serialNumber = SerialNumberUtil.generateSerialNo("projectPdf");
            String fileName = "项目管理_" + df.format(new Date()) + "_"+serialNumber +".pdf";
            //输出流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String idStr = pd.getString("idList");
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
                    data.put("id", listId.get(i));
                    //生成单个pdf
                    organizationService.exportPDF(data, document);
                }
            } else {
                if (listId.size() > 0) {
                    PageData data = new PageData();
                    data.put("id", listId.get(0));
                    organizationService.exportPDF(data, document);
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
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目管理PDF", pd);
        }
    }

}
