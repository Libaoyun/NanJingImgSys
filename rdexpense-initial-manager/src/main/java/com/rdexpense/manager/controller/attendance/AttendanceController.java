package com.rdexpense.manager.controller.attendance;


import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.ItemExpenses.ItemExpensesDetailDto;
import com.rdexpense.manager.dto.attendance.*;
import com.rdexpense.manager.dto.base.BusinessIdListDto;
import com.rdexpense.manager.service.attendance.AttendanceService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@RequestMapping("/attendance")
@Api(value = "人员考勤管理", tags = "人员考勤管理")
public class AttendanceController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private BaseDao dao;

    @Autowired
    private LogUtil logUtil;

    private static final String FILE_PREFIX = "研发人员考勤管理";


    @PostMapping("/queryList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ResponseEntity<PageInfo<AttendanceListDto>> queryList(AttendanceSearchDto attendanceSearchDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = attendanceService.queryList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, AttendanceListDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询研发人员考勤管理列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "新增考勤")
    @PostMapping("/add")
    public ResponseEntity addAttendance(AttendanceAddDto attendanceAddDto) {
        PageData pd = this.getParams();
        checkParam(pd);

        ResponseEntity result = null;
        try {

            attendanceService.addAttendance(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("新增研发人员考勤管理失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, FILE_PREFIX, pd);
        }
    }

    @ApiOperation(value = "编辑考勤")
    @PostMapping("/update")
    public ResponseEntity updateAttendance(AttendanceAddDto attendanceAddDto) {

        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("id"), "主键ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 256);

        // 编辑时，状态只能为已保存
        PageData recordData = (PageData) dao.findForObject("AttendanceMapper.queryDetail", pd);
        String requestStatus = recordData.getString("processStatus");
        if (!requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[0]) && !requestStatus.equals(ConstantValUtil.APPROVAL_STATUS[3])) {
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc());
        }

        checkParam(pd);

        ResponseEntity result = null;
        try {

            attendanceService.updateAttendance(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("修改研发人员考勤管理失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, FILE_PREFIX, pd);
        }
    }



    @ApiOperation(value = "删除考勤")
    @PostMapping("/delete")
    public ResponseEntity deleteAttendance(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        CheckParameter.checkBusinessIdList(pd);

        ResponseEntity result = null;
        try {
            attendanceService.deleteAttendance(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("删除研发人员考勤管理失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, FILE_PREFIX, pd);
        }


    }



    @ApiOperation(value = "查询详情")
    @PostMapping("/queryDetail")
    @ApiImplicitParam(name = "businessId", value = "业务主键ID", required = true, dataType = "String")
    public ResponseEntity<AttendanceDetailDto> queryDetail() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 128);
        try {
            PageData pageData = attendanceService.queryDetail(pd);
            return PropertyUtil.pushData(pageData, AttendanceDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询研发人员考勤管理详情失败,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "查询项目弹框")
    @PostMapping(value = "/queryProject")
    public ResponseEntity<PageInfo<ProjectDto>> queryProject(ProjectSearchDto projectSearchDto) {
        PageData pd = this.getParams();

        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = attendanceService.queryProject(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, ProjectDto.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("查询查询项目弹框失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "查询项目人员")
    @PostMapping(value = "/queryUser")
    public ResponseEntity<List<ResearchUsersDetailDto>> queryUser(UserSearchDto userSearchDto) {
        PageData pd = this.getParams();

        CheckParameter.stringLengthAndEmpty(pd.getString("projectId"), "项目ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("attendanceMonth"), "考勤月份", 256);
        ResponseEntity result = null;
        try {
            List<PageData> list = attendanceService.queryUser(pd);
            result = ResponseEntity.success(PropertyUtil.covertListModel(list, ResearchUsersDetailDto.class), ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("查询项目人员失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "导入考勤表")
    @PostMapping(value = "/uploadAttendance")
    public ResponseEntity<AttendanceListDetailDto> uploadAttendance(AttendanceUploadFileDto dto) {
        PageData pd = checkParams(dto);
        try {
            PageData data = attendanceService.uploadAttendance(dto.getFile(),pd);
            return PropertyUtil.pushData(data, AttendanceListDetailDto.class, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("导入研发人员考勤表失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导入工资表")
    @PostMapping(value = "/uploadSalary")
    public ResponseEntity<SalaryDto> uploadSalary(AttendanceUploadFileDto dto) {
        PageData pd = checkParams(dto);
        try {
            PageData data = attendanceService.uploadSalary(dto.getFile(),pd);
            return PropertyUtil.pushData(data, SalaryDto.class, ConstantMsgUtil.INFO_UPLOAD_SUCCESS.desc());

        } catch (Exception e) {
            logger.error("导入研发人员工资失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(),e.getMessage());
        }
    }


    @ApiOperation(value = "生成分摊表")
    @PostMapping(value = "/generateShare")
    public ResponseEntity uploadShare(AttendanceShareAddDto attendanceShareAddDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        try {
            List<PageData> list = attendanceService.generateShare(pd);
            result = ResponseEntity.success(list);
            return result;
        } catch (Exception e) {
            logger.error("生成分摊表失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_UPLOAD_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "支出申请单")
    @PostMapping(value = "/generateApply")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessId", value = "业务主键ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "amountType", value = "金额类型，1：工资薪金，2：五险一金", required = true, dataType = "String")
    })
    public ResponseEntity<ApplyDetailDto> generateApply() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务主键ID", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("amountType"), "金额类型", 10);
        try {
            PageData data = attendanceService.generateApply(pd);
            return PropertyUtil.pushData(data, ApplyDetailDto.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("支出申请单失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }

    @ApiOperation(value = "查询已生成支出申请单的信息")
    @PostMapping(value = "/alreadyGenerateApply")
    @ApiImplicitParam(name = "businessId", value = "业务编码ID", required = true, dataType = "String")
    public ResponseEntity<ItemExpensesDetailDto> alreadyGenerateApply() {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("businessId"), "业务编码ID", 128);
        ResponseEntity result = null;
        try {
            List<PageData> list = attendanceService.alreadyGenerateApply(pd);
            result = ResponseEntity.success(list);
            return result;
        } catch (Exception e) {
            logger.error("查询已生成支出申请单的信息失败,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }



    @ApiOperation(value = "导出excel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(BusinessIdListDto businessIdListDto) {
        PageData pd = this.getParams();
        ResponseEntity result = null;
        CheckParameter.checkBusinessIdList(pd);

        HttpServletResponse response = this.getResponse();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String number = SerialNumberUtil.generateSerialNo("attendanceExcel");

        try {


            String businessIdStr = pd.getString("businessIdList");
            List<String> businessIdList = JSONObject.parseArray(businessIdStr, String.class);

            if(businessIdList.size() == 1){
                HSSFWorkbook wb = attendanceService.exportExcel(businessIdList.get(0));
                String fileName = FILE_PREFIX+"_"+date+"_"+number+".xls";
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
                OutputStream out = response.getOutputStream();
                wb.write(out);
                out.close();

            }else {
                //文件名
                String fileName = FILE_PREFIX+"_"+date+"_"+number+".zip";
                //输出流
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // 压缩流
                ZipOutputStream zos = new ZipOutputStream(bos);
                //生成excel
                attendanceService.exportZip(businessIdList, zos, bos,FILE_PREFIX);
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
            logUtil.saveLogData(result.getCode(), 6, FILE_PREFIX, pd);
        }
    }


    public static PageData checkParams(AttendanceUploadFileDto dto) {

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

        List<String> idCardList = dto.getIdCardList();

        String  projectName = dto.getProjectName();
        CheckParameter.stringLengthAndEmpty(projectName, "项目名称",256);

        String  attendanceMonth = dto.getAttendanceMonth();
        CheckParameter.stringLengthAndEmpty(attendanceMonth, "attendanceMonth",256);

        pageData.put("attendanceMonth",attendanceMonth);
        pageData.put("projectName",projectName);
        pageData.put("idCardList",idCardList);
        pageData.put("creatorOrgId",creatorOrgId);
        pageData.put("creatorOrgName",creatorOrgName);
        pageData.put("createUserId",createUserId);
        pageData.put("createUser",createUser);
        pageData.put("menuCode",menuCode);

        return pageData;

    }

    private void checkParam(PageData pd) {
        CheckParameter.checkDefaultParams(pd);

        CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserId"), "编制人ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorUserName"), "编制人", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("createdDate"), "编制日期", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectId"), "项目ID", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectName"), "项目名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("unitName"), "单位名称", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectId"), "项目编码", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("projectLeader"), "项目负责人", 256);
        CheckParameter.stringLengthAndEmpty(pd.getString("attendanceMonth"), "考勤日期", 256);



    }



}
