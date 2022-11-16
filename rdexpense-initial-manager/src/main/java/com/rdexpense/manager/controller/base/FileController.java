package com.rdexpense.manager.controller.base;

import com.alibaba.fastjson.JSONObject;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.file.AttachmentDto;
import com.rdexpense.manager.dto.file.UploadFileDto;
import com.rdexpense.manager.service.file.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.common.util.ConstantMsgUtil.*;

@Api(value = "附件接口", tags = "附件")
@RestController
@RequestMapping(value = "/file")
public class FileController extends BaseController {

    @Autowired
    private FileService fileService;

    public final static Map<Integer, String> map = new LinkedHashMap<>();

    static {
        map.put(0, "立项申请导入模板(局级拨款).xlsx");//立项申请
        map.put(1, "立项申请导入模板(其他拨款).xlsx");//立项申请
        map.put(2, "研发人员考勤导入模板.xlsx");//人员考勤
        map.put(3, "研发项目领料单导入模板.xlsx");//项目领料单
        map.put(4, "租赁设备结算单导入模板.xlsx");//租赁设备结算单
        map.put(5, "设备折旧支出单导入模板.xlsx");//设备折旧支出单
        map.put(6, "研发支出辅助账导入模板.xlsx");//项目支出辅助账
        map.put(7, "交底书导入模板.xlsx");//交底书
        map.put(8, "进展报告导入模板.xlsx");//进展报告
    }


    @ApiOperation(value = "上传附件")
    @PostMapping(value = "/upload")
    public ResponseEntity<AttachmentDto> upload(UploadFileDto uploadFileDto) {
        try {
            MultipartFile file = uploadFileDto.getFile();
            String fileName = file.getOriginalFilename();
            PageData pageData = AwsUtil.upload(file, ConstantValUtil.BUCKET_PRIVATE);
            pageData.put("fileName", fileName);
            return PropertyUtil.pushData(pageData, AttachmentDto.class,INFO_UPLOAD_SUCCESS.desc());
        } catch (MyException e) {
            ResponseEntity.failure(ERR_UPLOAD_FAIL.val(),ERR_UPLOAD_FAIL.desc());
            throw e;
        } catch (Exception e) {
            return ResponseEntity.failure(ERR_UPLOAD_FAIL.val(),ERR_UPLOAD_FAIL.desc());
        }
    }

    @ApiOperation(value = "查询附件")
    @GetMapping(value = "/queryFile")
    @ApiImplicitParam(name="businessId",value="关联id",required=true,paramType="query",dataType="string")
    public ResponseEntity<List<AttachmentDto>> queryFile() {
        PageData pd = this.getParams();
        String businessId = pd.getString("businessId");
        CheckParameter.stringLengthAndEmpty(businessId, "关联id",128);
        try {
            List<PageData> list = fileService.query(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, AttachmentDto.class),INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            ResponseEntity.failure(ERR_QUERY_FAIL.val(),ERR_QUERY_FAIL.desc());
            throw e;
        }catch (Exception e) {
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(),ERR_QUERY_FAIL.desc());
        }
    }

    @ApiOperation(value = "删除附件")
    @PostMapping(value = "/delete")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name="businessId",value="关联id",required=true,dataType="string"),
            @ApiImplicitParam(name = "id", value = "主键id", required = true,dataType = "Integer")})
    public ResponseEntity delete() {
        PageData pd = this.getParams();
        String businessId = pd.getString("businessId");
        CheckParameter.stringLengthAndEmpty(businessId, "关联id",128);
        try {
            fileService.delete(pd);
            return ResponseEntity.success(null,INFO_DELETE_SUCCESS.desc());
        } catch (MyException e) {
            ResponseEntity.failure(ERR_DELETE_FAIL.val(),ERR_DELETE_FAIL.desc());
            throw e;
        }catch (Exception e) {
            return ResponseEntity.failure(ERR_DELETE_FAIL.val(),ERR_DELETE_FAIL.desc());
        }
    }


    @ApiOperation(value = "下载模板文件")
    @PostMapping(value = "/templateDownLoad")
    @ApiImplicitParam(name = "fileFlag", value = "文件标识 0:立项申请(局级拨款) 1:立项申请(其他拨款) 2:人员考勤 3:项目领料单 4:租赁设备结算单 " +
            "5:设备折旧支出单6：项目支出辅助账 7:交底书 8:进展报告",
            required = true, dataType = "Integer")
    public void downLoad(HttpServletResponse response) {
        PageData pd = this.getParams();
        // 下载本地文件
        String fileName = null;
        InputStream inStream= null;
        String fileFlag = pd.getString("fileFlag");
        CheckParameter.stringIsNull(fileFlag, "文件标识");
        try {

            fileName = map.get(Integer.valueOf(fileFlag));
            if(fileName == null){
                throw new MyException("文件不存在");
            }

            StringBuffer buffer = new StringBuffer();
            buffer.append("/template/").append(fileName);
            inStream = this.getClass().getResourceAsStream(buffer.toString());

            if(inStream == null){
                throw new MyException("文件不存在");
            }
            // 设置输出的格式
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8"));
            // 循环取出流中的数据
            byte[] b = new byte[100];
            int len;

            while ((len = inStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
            inStream.close();
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.ERR_DOWNLOAD_FAIL.desc(), e);
        }

    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public void downloadFile() {
        HttpServletResponse response = this.getResponse();
        PageData pd = this.getParams();
        String details = pd.getString("details");
        CheckParameter.stringEmpty("明细", details);
        String menuCode = pd.getString("menuCode");
        CheckParameter.checkPositiveInt(menuCode, "菜单编码");
        List<PageData> listObject = JSONObject.parseArray(details, PageData.class);
        PageData pageData=fileService.selectZipName(pd);
        String zipName=pageData.getString("menuName")+".zip";
        AwsUtil.downloadFile(response,listObject,zipName);

    }

}