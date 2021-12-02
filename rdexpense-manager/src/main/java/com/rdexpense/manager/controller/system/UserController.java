package com.rdexpense.manager.controller.system;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.rdexpense.manager.dto.system.user.*;
import com.rdexpense.manager.dto.system.user.UserListDTO;
import com.rdexpense.manager.service.system.UserService;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.ERR_QUERY_FAIL;
import static com.common.util.ConstantMsgUtil.INFO_EXPORT_SUCCESS;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:27
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户管理", tags = "用户管理")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LogUtil logUtil;


    @PostMapping("/queryUserList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<UserListDTO>> queryUserList(UserSearchDto userSearchDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = userService.queryUserList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, UserListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询人员列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "新增用户")
    @PostMapping("/addUser")
    public ResponseEntity addUser(UserAddDto userAddDto) {
        PageData pd = this.getParams();
        checkParam(pd);
        ResponseEntity result = null;
        try {

            userService.addUser(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增用户信息失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "用户信息", pd);
        }
    }

    @ApiOperation(value = "修改用户")
    @PostMapping("/updateUser")
    public ResponseEntity updateUser(UserUpdateDto userUpdateDto) {

        PageData pd = this.getParams();
        checkParam(pd);

        ResponseEntity result = null;
        try {
            userService.updateUser(pd);
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



    @ApiOperation(value = "密码重置")
    @PostMapping("/resetPassword")
    public ResponseEntity resetPassword(IdListDto idListDto) {
        PageData pd = this.getParams();

        CheckParameter.checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("主键ID不能为空");
        }

        ResponseEntity result = null;
        try {
            userService.resetPassword(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("密码重置,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "密码", pd);
        }
    }

    @ApiOperation(value = "更新密码")
    @PostMapping("/updatePassword")
    public ResponseEntity updatePassword(UpdateUserPasswordDto updateUserPasswordDto) {
        PageData pd = this.getParams();
        checkParams(pd);

        ResponseEntity result = null;
        try {
            userService.updatePassword(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("更新密码,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "密码", pd);
        }


    }


    @ApiOperation(value = "删除用户")
    @PostMapping("/deleteUser")
    public ResponseEntity deleteUser(IdListDto idListDto) {
        PageData pd = this.getParams();

        //校验取出参数
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("主键ID不能为空");
        }
        CheckParameter.checkDefaultParams(pd);

        ResponseEntity result = null;
        try {
            userService.deleteUser(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除用户失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "用户信息", pd);
        }


    }


    @PostMapping("/getUserDetail")
    @ApiOperation(value = "查询用户详情", notes = "查询用户详情")
    @ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String")
    public ResponseEntity<UserDetailDto> getUserDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID", 64);
        try {
            PageData pageData = userService.getUserDetail(pd);
            return PropertyUtil.pushData(pageData, UserDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询用户详情,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }


    @ApiOperation(value = "查询职务")
    @PostMapping(value = "/getPost")
    @ApiImplicitParam(name = "departmentCode", value = "部门编码", required = true, dataType = "String")
    public ResponseEntity<List<UserPostDto>> getPost() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "部门编码", 64);
        try {
            List<PageData> list = userService.getPost(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, UserPostDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询职务失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
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
            HSSFWorkbook wb = userService.exportExcel(pd);
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("userExcel");
            String fileName = "用户管理_" + df.format(new Date()) + "_"+serialNumber +".xls";
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
            String serialNumber = SerialNumberUtil.generateSerialNo("userPdf");
            String fileName = "用户管理_" + df.format(new Date()) + "_"+serialNumber +".pdf";
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
                    userService.exportPDF(data, document);
                }
            } else {
                if (listId.size() > 0) {
                    PageData data = new PageData();
                    data.put("id", listId.get(0));
                    userService.exportPDF(data, document);
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
            logUtil.saveLogData(result.getCode(), 6, "用户管理PDF", pd);
        }
    }


    private void checkParams(PageData pd) {
        String oldPassword = pd.getString("oldPassword");
        String password = pd.getString("password");

        if (!CheckParameter.stringLengthIsTrue(oldPassword, 20)) {
            throw new MyException("旧密码填写有误");
        }
        if (!CheckParameter.stringLengthIsTrue(password, 20)) {
            throw new MyException("密码填写有误");
        }

        if (oldPassword.equals(password)) {
            throw new MyException("旧密码和新密码不能一样");
        }

        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{15}$";
        if (!password.matches(regex)) {
            throw new MyException("密码必须包含数字和字母，且位数为15位");
        }

    }


    private void checkParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringLengthAndEmpty(pd.getString("userCode"), "编号", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("userName"), "姓名", 128);
        List<PageData> departmentList = JSONObject.parseArray(pd.getString("departmentList"), PageData.class);
        if(!CollectionUtils.isEmpty(departmentList)){
            for(PageData department : departmentList){
                CheckParameter.stringLengthAndEmpty(department.getString("departmentName"), "部门", 128);
                CheckParameter.stringLengthAndEmpty(department.getString("departmentCode"), "部门编码", 128);
                CheckParameter.stringLengthAndEmpty(department.getString("postName"), "职务", 128);
                CheckParameter.stringLengthAndEmpty(department.getString("postCode"), "职务编码", 128);
            }
        }else{
            throw new MyException("部门职务不能为空");

        }
    }

}
