package com.rdexpense.manager.controller.analysis;


import com.alibaba.fastjson.JSONObject;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.analysis.ExpensesBillListDto;
import com.rdexpense.manager.dto.analysis.ExpensesBillSearchDto;
import com.rdexpense.manager.dto.analysis.ExpensesBillUploadFileDto;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.dto.projectApply.*;
import com.rdexpense.manager.service.analysis.ExpensesBillService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:27
 */
@RestController
@RequestMapping("/expensesBill")
@Api(value = "项目支出辅助账", tags = "项目支出辅助账")
public class ExpensesBillController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpensesBillController.class);

    @Autowired
    private ExpensesBillService expensesBillService;

    @Autowired
    private LogUtil logUtil;

    private static final String FILE_PREFIX = "项目支出辅助账_";


    @PostMapping("/queryList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<ExpensesBillListDto>> queryList(ExpensesBillSearchDto expensesBillSearchDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = expensesBillService.queryList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ExpensesBillListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询项目支出辅助账列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "删除辅助账")
    @PostMapping("/delete")
    public ResponseEntity deleteApply(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkBusinessIdList(pd);

        ResponseEntity result = null;
        try {
            expensesBillService.deleteApply(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除项目支出辅助账失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "项目支出辅助账", pd);
        }


    }




    @ApiOperation(value = "导入")
    @PostMapping(value = "/upload")
    public ResponseEntity upload(ExpensesBillUploadFileDto dto) {
        PageData pd = checkParams(dto);
        ResponseEntity result = null;
        try {
            expensesBillService.uploadAll(dto.getFile(),pd);
            result = ResponseEntity.success(null, INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
            logger.error("导入项目支出辅助账失败,request=[{}]", pd);
            return  result;

        } finally {
            logUtil.saveLogData(result.getCode(), 7, "项目支出辅助账", pd);
        }

    }




    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        CheckParameter.checkBusinessIdList(pd);

        HttpServletResponse response = this.getResponse();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String number = SerialNumberUtil.generateSerialNo("expensesBillExcel");

        try {


            String businessIdStr = pd.getString("businessIdList");
            List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);

            if(businessIdList.size() == 1){
                HSSFWorkbook wb = expensesBillService.exportExcel(businessIdList.get(0));
                String fileName = FILE_PREFIX+date+"_"+number+".xls";
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = response.getOutputStream();
                wb.write(out);
                out.close();

            }else {
                //文件名
                String fileName = FILE_PREFIX+date+"_"+number+".zip";
                //输出流
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // 压缩流
                ZipOutputStream zos = new ZipOutputStream(bos);
                //生成excel
                expensesBillService.exportZip(1,businessIdList, zos, bos,FILE_PREFIX);
                zos.flush();
                zos.close();
                //写入返回response
                response.reset();
                response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(bos.toByteArray());
                out.flush();
                out.close();
            }

            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目支出辅助账", pd);
        }
    }



    @ApiOperation(value = "导出PDF")
    @PostMapping(value = "/exportPdf")
    public ResponseEntity exportPdf(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkBusinessIdList(pd);
        ResponseEntity result = null;

        HttpServletResponse response = this.getResponse();
        try {

            String businessIdStr = pd.getString("businessIdList");
            List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            String number = SerialNumberUtil.generateSerialNo("expensesBillPdf");


            if(businessIdList.size() == 1){
                expensesBillService.exportPdf(businessIdList.get(0),response,FILE_PREFIX);
            }else {
                //文件名
                String fileName = FILE_PREFIX+date+"_"+number+".zip";
                //输出流
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // 压缩流
                ZipOutputStream zos = new ZipOutputStream(bos);

                expensesBillService.exportZip(2,businessIdList, zos, bos,FILE_PREFIX);
                zos.flush();
                zos.close();
                //写入返回response
                response.reset();
                response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(bos.toByteArray());
                out.flush();
                out.close();
            }


            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "项目支出辅助账", pd);
        }
    }


    public static PageData checkParams(ExpensesBillUploadFileDto dto) {

        PageData pageData = new PageData();

        String  createUserId = dto.getCreateUserId();
        CheckParameter.stringLengthAndEmpty(createUserId, "上传人id",128);

        String  createUser = dto.getCreateUser();
        CheckParameter.stringLengthAndEmpty(createUser, "上传人姓名",128);

        String  creatorOrgId = dto.getCreatorOrgId();
        CheckParameter.stringLengthAndEmpty(creatorOrgId, "右上角项目ID",128);

        String  creatorOrgName = dto.getCreatorOrgName();
        CheckParameter.stringLengthAndEmpty(creatorOrgName, "右上角项目名称",128);

        String  menuCode = dto.getMenuCode();
        CheckParameter.stringLengthAndEmpty(menuCode, "菜单编码",128);

        MultipartFile file = dto.getFile();
        CheckParameter.isNull(file, "上传文件");

        pageData.put("creatorOrgId",creatorOrgId);
        pageData.put("creatorOrgName",creatorOrgName);
        pageData.put("createUserId",createUserId);
        pageData.put("createUser",createUser);
        pageData.put("menuCode",menuCode);

        return pageData;

    }


}
